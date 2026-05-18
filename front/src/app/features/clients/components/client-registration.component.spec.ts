import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { provideLocationMocks } from '@angular/common/testing';
import { ClientRegistrationComponent } from './client-registration.component';
import { CLIENT_SERVICE_TOKEN, IClientService } from '@core/services/client.service.interface';

describe('ClientRegistrationComponent', () => {
  let component: ClientRegistrationComponent;
  let fixture: ComponentFixture<ClientRegistrationComponent>;
  let mockClientService: IClientService;

  beforeEach(async () => {
    mockClientService = {
      register: vi.fn(),
      login: vi.fn(),
      getAll: vi.fn(),
      getById: vi.fn(),
      update: vi.fn(),
      delete: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ClientRegistrationComponent],
      providers: [
        { provide: CLIENT_SERVICE_TOKEN, useValue: mockClientService },
        provideRouter([]),
        provideLocationMocks()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.registrationForm.valid).toBe(false);
  });

  it('should validate required fields', () => {
    const form = component.registrationForm;
    expect(form.get('nombre')!.valid).toBe(false);
    expect(form.get('email')!.valid).toBe(false);
    expect(form.get('contrasena')!.valid).toBe(false);
  });

  it('should validate email format', () => {
    const email = component.registrationForm.get('email')!;
    email.setValue('not-an-email');
    expect(email.valid).toBe(false);
    email.setValue('valid@email.com');
    expect(email.valid).toBe(true);
  });

  it('should validate password minlength', () => {
    const pass = component.registrationForm.get('contrasena')!;
    pass.setValue('123');
    expect(pass.valid).toBe(false);
    pass.setValue('12345678');
    expect(pass.valid).toBe(true);
  });

  it('should validate nombre only letters', () => {
    const nombre = component.registrationForm.get('nombre')!;
    nombre.setValue('Juan123');
    expect(nombre.valid).toBe(false);
    nombre.setValue('Juan');
    expect(nombre.valid).toBe(true);
  });

  it('should call register on submit when valid', () => {
    vi.mocked(mockClientService.register).mockReturnValue(of({ id: 1, nombre: 'Juan', apellido: 'Perez', email: 'juan@test.com', domicilio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '1' } }));

    component.registrationForm.setValue({
      nombre: 'Juan', apellido: 'Perez',
      email: 'juan@test.com', contrasena: '12345678',
      domicilio: { pais: 'Argentina', provincia: 'BA', localidad: 'CABA', calle: 'Av Siempre Viva', numero: '123', piso: '', departamento: '' },
      rol: 'CLIENTE'
    });

    component.onSubmit();
    expect(mockClientService.register).toHaveBeenCalled();
    expect(component.isRegistered).toBe(true);
  });

  it('should show error on registration failure', () => {
    vi.mocked(mockClientService.register).mockReturnValue(throwError(() => ({ error: { message: 'Email duplicado' } })));

    component.registrationForm.setValue({
      nombre: 'Juan', apellido: 'Perez',
      email: 'dup@test.com', contrasena: '12345678',
      domicilio: { pais: 'Argentina', provincia: 'BA', localidad: 'CABA', calle: 'Av Siempre Viva', numero: '123', piso: '', departamento: '' },
      rol: 'CLIENTE'
    });

    component.onSubmit();
    expect(component.errorMessage).toBe('Email duplicado');
    expect(component.isLoading).toBe(false);
  });

  it('should mark form as touched when invalid submit', () => {
    component.onSubmit();
    expect(component.registrationForm.get('nombre')!.touched).toBe(true);
  });

  it('hasFieldError should return correct value', () => {
    const field = component.registrationForm.get('nombre')!;
    field.markAsTouched();
    expect(component.hasFieldError('nombre')).toBe(true);
  });

  it('getFieldError should return correct messages', () => {
    expect(component.getFieldError('nombre')).toBe('Este campo es requerido');
    const email = component.registrationForm.get('email')!;
    email.setValue('bad');
    email.markAsTouched();
    expect(component.getFieldError('email')).toBe('Email inválido');
  });
});

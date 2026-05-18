import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ClientLoginComponent } from './client-login.component';
import { CLIENT_SERVICE_TOKEN, IClientService } from '@core/services/client.service.interface';
import { AuthService } from '@core/services/auth.service';

describe('ClientLoginComponent', () => {
  let component: ClientLoginComponent;
  let fixture: ComponentFixture<ClientLoginComponent>;
  let mockClientService: IClientService;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    mockClientService = {
      register: vi.fn(), login: vi.fn(), getAll: vi.fn(),
      getById: vi.fn(), update: vi.fn(), delete: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ClientLoginComponent],
      providers: [
        { provide: CLIENT_SERVICE_TOKEN, useValue: mockClientService },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClientLoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.loginForm.valid).toBe(false);
  });

  it('should validate email and password', () => {
    expect(component.loginForm.get('email')!.valid).toBe(false);
    expect(component.loginForm.get('contrasena')!.valid).toBe(false);
  });

  it('should call login and navigate on success', () => {
    const mockUser = { id: 1, nombre: 'J', apellido: 'P', email: 'j@t.com', domicilio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '1' }, rol: 'ADMIN' };
    vi.mocked(mockClientService.login).mockReturnValue(of(mockUser));

    component.loginForm.setValue({ email: 'j@t.com', contrasena: '12345678' });
    component.onSubmit();

    expect(mockClientService.login).toHaveBeenCalledWith({ email: 'j@t.com', contrasena: '12345678' });
    expect(authService.currentUser).toEqual(mockUser);
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should show error on login failure', () => {
    vi.mocked(mockClientService.login).mockReturnValue(throwError(() => ({ error: { message: 'Credenciales inválidas' } })));

    component.loginForm.setValue({ email: 'bad@test.com', contrasena: '12345678' });
    component.onSubmit();

    expect(component.errorMessage).toBe('Credenciales inválidas');
  });

  it('should show generic error when no message', () => {
    vi.mocked(mockClientService.login).mockReturnValue(throwError(() => ({})));

    component.loginForm.setValue({ email: 'bad@test.com', contrasena: '12345678' });
    component.onSubmit();

    expect(component.errorMessage).toBe('Email o contraseña incorrectos');
  });

  it('getFieldError should return correct messages', () => {
    expect(component.getFieldError('email')).toBe('Este campo es requerido');
    const email = component.loginForm.get('email')!;
    email.setValue('bad');
    email.markAsTouched();
    expect(component.getFieldError('email')).toBe('Email inválido');
  });
});

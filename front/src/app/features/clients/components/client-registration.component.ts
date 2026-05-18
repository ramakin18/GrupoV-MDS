import { Component, OnInit, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CLIENT_SERVICE_TOKEN, IClientService } from '@core/services/client.service.interface';
import { Client, ClientCreateDto } from '@core/models/client.model';

@Component({
  selector: 'app-client-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './client-registration.component.html',
  styleUrls: ['./client-registration.component.css']
})
export class ClientRegistrationComponent implements OnInit {
  registrationForm: FormGroup;
  isRegistered = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    @Inject(CLIENT_SERVICE_TOKEN) private readonly clientService: IClientService,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.registrationForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      apellido: ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
      email: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(8)]],
      domicilio: this.fb.group({
        pais: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
        provincia: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
        localidad: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/)]],
        calle: ['', [Validators.required, Validators.pattern(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\s.,ºª\-]+$/)]],
        numero: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
        piso: ['', [Validators.pattern(/^\d+$/)]],
        departamento: ['', [Validators.pattern(/^[a-zA-Z0-9\s]+$/)]],
      }),
      rol: ['CLIENTE']
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.registrationForm.invalid) {
      this.registrationForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const formValue = this.registrationForm.value;
    const clientData: ClientCreateDto = {
      nombre: formValue.nombre,
      apellido: formValue.apellido,
      email: formValue.email,
      contrasena: formValue.contrasena,
      domicilio: formValue.domicilio,
      rol: formValue.rol
    };

    this.clientService.register(clientData).subscribe({
      next: () => {
        this.isRegistered = true;
        this.isLoading = false;
        this.cdr.detectChanges();
        this.registrationForm.reset({
          rol: 'CLIENTE',
          domicilio: {
            pais: '',
            provincia: '',
            localidad: '',
            calle: '',
            numero: '',
            piso: '',
            departamento: '',
          }
        });
      },
      error: (error: any) => {
        this.errorMessage = error?.error?.message || 'Error al registrar cliente';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  goHome(): void {
    this.router.navigate(['/']);
  }

  hasFieldError(fieldName: string): boolean {
    const field = this.registrationForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.registrationForm.get(fieldName);
    if (!field || !field.errors) return '';

    if (field.errors['required']) return 'Este campo es requerido';
    if (field.errors['email']) return 'Email inválido';
    if (field.errors['minlength']) {
      return `Mínimo ${field.errors['minlength'].requiredLength} caracteres`;
    }

    if (field.errors['pattern']) {
      if (fieldName === 'nombre' || fieldName === 'apellido') {
        return 'Solo se permiten letras';
      }
      if (fieldName === 'numero' || fieldName === 'domicilio.numero') {
        return 'Solo se permiten números';
      }
      if (fieldName === 'piso' || fieldName === 'domicilio.piso') {
        return 'Solo se permiten números';
      }
      if (fieldName === 'pais' || fieldName === 'domicilio.pais'
        || fieldName === 'provincia' || fieldName === 'domicilio.provincia'
        || fieldName === 'localidad' || fieldName === 'domicilio.localidad') {
        return 'Solo se permiten letras';
      }
      if (fieldName === 'calle' || fieldName === 'domicilio.calle') {
        return 'Solo se permiten letras, números y puntos';
      }
      if (fieldName === 'departamento' || fieldName === 'domicilio.departamento') {
        return 'Solo se permiten letras y números';
      }
      return 'Formato inválido';
    }

    return 'Campo inválido';
  }
}

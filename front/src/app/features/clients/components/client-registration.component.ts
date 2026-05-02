import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CLIENT_SERVICE_TOKEN, IClientService } from '@core/services/client.service.interface';
import { Client, ClientCreateDto } from '@core/models/client.model';

@Component({
  selector: 'app-client-registration',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
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
    @Inject(CLIENT_SERVICE_TOKEN) private readonly clientService: IClientService
  ) {
    this.registrationForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2)]],
      apellido: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      pais: ['', Validators.required],
      provincia: ['', Validators.required],
      localidad: ['', Validators.required],
      calle: ['', Validators.required],
      altura: ['', [Validators.required, Validators.pattern(/^\d+$/)]],
      piso: [''],
      departamento: ['']
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
    
    const clientData: ClientCreateDto = this.registrationForm.value;

    this.clientService.register(clientData).subscribe({
      next: () => {
        this.isRegistered = true;
        this.isLoading = false;
        this.registrationForm.reset();
      },
      error: (error: any) => {
        this.errorMessage = error?.error?.message || 'Error al registrar cliente';
        console.error('Registration error:', error);
        this.isLoading = false;
      }
    });
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
    if (field.errors['pattern']) return 'Solo números permitidos';

    return 'Campo inválido';
  }
}

import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CLIENT_SERVICE_TOKEN, IClientService } from '@core/services/client.service.interface';
import { AuthService } from '@core/services/auth.service';
import { ClientLoginDto } from '@core/models/client.model';

@Component({
  selector: 'app-client-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './client-login.component.html',
  styleUrls: ['./client-login.component.css']
})
export class ClientLoginComponent {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor(
    private readonly fb: FormBuilder,
    @Inject(CLIENT_SERVICE_TOKEN) private readonly clientService: IClientService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      contrasena: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const credentials: ClientLoginDto = {
      email: this.loginForm.value.email,
      contrasena: this.loginForm.value.contrasena
    };

    console.log('[Login] Sending request', credentials.email);

    this.clientService.login(credentials).subscribe({
      next: (user) => {
        this.authService.login(user);
        this.isLoading = false;
        this.cdr.detectChanges();
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        this.errorMessage = error?.error?.message || 'Email o contraseña incorrectos';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  hasFieldError(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (!field || !field.errors) return '';
    if (field.errors['required']) return 'Este campo es requerido';
    if (field.errors['email']) return 'Email inválido';
    if (field.errors['minlength']) return 'Mínimo 8 caracteres';
    return 'Campo inválido';
  }
}

import { Component } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-registro-cliente',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registro-cliente.html',
  styleUrl: './registro-cliente.css'
})
export class RegistroClienteComponent {
  registrado: boolean = false;
  isLogin: boolean = true; // Empezamos mostrando el Login// Controla el mensaje de éxito

  registroForm = new FormGroup({
    nombre: new FormControl('', Validators.required),
    apellido: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    contraseña: new FormControl('', [Validators.required, Validators.minLength(8)]),
    pais: new FormControl('', Validators.required),
    provincia: new FormControl('', Validators.required),

        localidad: new FormControl('', Validators.required),
        calle: new FormControl('', Validators.required), // Cambiado de calleNumero a calle
        altura: new FormControl('', Validators.required), // Nuevo campo obligatorio
        piso: new FormControl(''),
    // ...
    departamento: new FormControl('') // Opcional
  });
  cambiarModo() {
  this.isLogin = !this.isLogin;
  this.registrado = false; // Reseteamos el mensaje de éxito al cambiar
}
  onSubmit() {
    if (this.isLogin) {
      // LÓGICA DE LOGIN
      const datosLogin = {
        email: this.registroForm.value.email,
        contraseña: this.registroForm.value.contraseña // <--- Asegurate que acá diga .password
      };
      console.log('Intentando Iniciar Sesión con:', datosLogin);
      // Aquí llamarías a tu servicio: this.authService.login(datosLogin)...

    } else {
      // LÓGICA DE REGISTRO (Lo que ya tenías)
      if (this.registroForm.valid) {
        this.registrado = true;
        console.log('Registrando nuevo usuario:', this.registroForm.value);
      }
    }
  }
}

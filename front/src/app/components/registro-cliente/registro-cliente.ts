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
  registrado: boolean = false; // Controla el mensaje de éxito

  registroForm = new FormGroup({
    nombre: new FormControl('', Validators.required),
    apellido: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    pais: new FormControl('', Validators.required),
    provincia: new FormControl('', Validators.required),

        localidad: new FormControl('', Validators.required),
        calle: new FormControl('', Validators.required), // Cambiado de calleNumero a calle
        altura: new FormControl('', Validators.required), // Nuevo campo obligatorio
        piso: new FormControl(''),
    // ...
    departamento: new FormControl('') // Opcional
  });

  onSubmit() {
    if (this.registroForm.valid) {
      this.registrado = true; // Activa el mensaje en el HTML
      console.log('Datos enviados:', this.registroForm.value);
    }
  }
}

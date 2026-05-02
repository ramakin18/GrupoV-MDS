import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RegistroClienteComponent } from './components/registro-cliente/registro-cliente';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RegistroClienteComponent], // <-- Verificá que RegistroClienteComponent esté acá
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent { // <--- ESTO tiene que decir AppComponent
  title = 'frontend';
}

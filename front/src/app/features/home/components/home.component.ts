import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ModalCarritoComponent } from '../../modal-carrito/modal-carrito';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, ModalCarritoComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  title = 'Sistema de Gestion';
  mostrarModalCarrito = false;

  constructor(
    private router: Router,
    private readonly cartService: CartService
  ) {}

  get cartQuantity(): number {
    return this.cartService.getTotalQuantity();
  }

  navigateTo(path: string): void {
    this.router.navigate([path]);
  }

  abrirModalCarrito(): void {
    this.mostrarModalCarrito = true;
  }

  cerrarModalCarrito(): void {
    this.mostrarModalCarrito = false;
  }
}

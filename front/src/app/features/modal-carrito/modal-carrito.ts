import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output } from '@angular/core';
import { CartItem } from '../../core/models/cart-item.model';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-modal-carrito',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-carrito.html',
  styleUrl: './modal-carrito.css',
})
export class ModalCarritoComponent {
  @Output() cerrar = new EventEmitter<void>();

  checkoutMessage = '';
  checkoutErrorMessage = '';
  isCheckingOut = false;

  constructor(private readonly cartService: CartService) {}

  get cartItems(): CartItem[] {
    return this.cartService.getItems();
  }

  get total(): number {
    return this.cartService.getTotal();
  }

  cerrarModal(): void {
    this.cerrar.emit();
  }

  increase(idProducto: number): void {
    const result = this.cartService.increaseQuantity(idProducto);

    if (!result.success) {
      this.checkoutErrorMessage = result.message || 'No se pudo aumentar la cantidad.';
    }
  }

  decrease(idProducto: number): void {
    this.cartService.decreaseQuantity(idProducto);
    this.clearMessages();
  }

  remove(idProducto: number): void {
    this.cartService.removeProduct(idProducto);
    this.clearMessages();
  }

  checkout(): void {
    this.clearMessages();

    if (this.cartItems.length === 0) {
      this.checkoutErrorMessage = 'El carrito esta vacio.';
      return;
    }

    const stockValidation = this.cartService.validateStock();

    if (!stockValidation.success) {
      this.checkoutErrorMessage = stockValidation.message || 'Hay productos sin stock suficiente.';
      return;
    }

    this.isCheckingOut = true;

    this.cartService.validateCartWithBackend().subscribe({
      next: (response) => {
        this.checkoutMessage = `Carrito validado correctamente. Total confirmado: $${response.total}`;
        this.isCheckingOut = false;
      },
      error: (error: unknown) => {
        this.checkoutErrorMessage = this.getErrorMessage(error);
        this.isCheckingOut = false;
      }
    });
  }

  private clearMessages(): void {
    this.checkoutMessage = '';
    this.checkoutErrorMessage = '';
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.error?.message) {
      return error.error.message;
    }

    return 'No se pudo validar el carrito con el backend.';
  }
}

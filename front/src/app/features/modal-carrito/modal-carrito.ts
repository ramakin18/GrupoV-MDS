import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output, Inject, ChangeDetectorRef } from '@angular/core';
import { CartItem } from '../../core/models/cart-item.model';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { ORDER_SERVICE_TOKEN, IOrderService } from '../../core/services/order.service.interface';

@Component({
  selector: 'app-modal-carrito',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal-carrito.html',
  styleUrl: './modal-carrito.css',
})
export class ModalCarritoComponent {
  @Output() cerrar = new EventEmitter<void>();

  step: 'cart' | 'confirm' | 'success' = 'cart';
  checkoutMessage = '';
  checkoutErrorMessage = '';
  isCheckingOut = false;
  confirmedTotal = 0;
  createdOrderId: number | null = null;

  constructor(
    private readonly cartService: CartService,
    private readonly authService: AuthService,
    private readonly cdr: ChangeDetectorRef,
    @Inject(ORDER_SERVICE_TOKEN) private readonly orderService: IOrderService
  ) {}

  get cartItems(): CartItem[] {
    return this.cartService.getItems();
  }

  get total(): number {
    return this.cartService.getTotal();
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn;
  }

  cerrarModal(): void {
    this.cerrar.emit();
  }

  increase(idProducto: number): void {
    if (this.step !== 'cart') return;
    const result = this.cartService.increaseQuantity(idProducto);
    if (!result.success) {
      this.checkoutErrorMessage = result.message || 'No se pudo aumentar la cantidad.';
    }
  }

  decrease(idProducto: number): void {
    if (this.step !== 'cart') return;
    this.cartService.decreaseQuantity(idProducto);
    this.clearMessages();
  }

  remove(idProducto: number): void {
    if (this.step !== 'cart') return;
    this.cartService.removeProduct(idProducto);
    this.clearMessages();
  }

  checkout(): void {
    this.clearMessages();

    if (!this.isLoggedIn) {
      this.checkoutErrorMessage = 'Debes iniciar sesion para comprar.';
      return;
    }

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
        this.confirmedTotal = response.total;
        this.step = 'confirm';
        this.isCheckingOut = false;
        this.cdr.detectChanges();
      },
      error: (error: unknown) => {
        this.checkoutErrorMessage = this.getErrorMessage(error);
        this.isCheckingOut = false;
        this.cdr.detectChanges();
      }
    });
  }

  confirmOrder(): void {
    this.clearMessages();
    this.isCheckingOut = true;

    const user = this.authService.currentUser;
    if (!user || !user.id) {
      this.checkoutErrorMessage = 'Debes iniciar sesion para comprar.';
      this.isCheckingOut = false;
      return;
    }

    this.orderService.create({
      clienteId: user.id,
      items: this.cartItems.map(item => ({
        idProducto: item.idProducto,
        cantidad: item.cantidad
      })),
      formaPago: 'EFECTIVO'
    }).subscribe({
      next: (pedido) => {
        this.createdOrderId = pedido.idPedido;
        this.step = 'success';
        this.isCheckingOut = false;
        this.cartService.clearCart();
        this.cdr.detectChanges();
      },
      error: (error: unknown) => {
        this.checkoutErrorMessage = this.getErrorMessage(error);
        this.isCheckingOut = false;
        this.cdr.detectChanges();
      }
    });
  }

  volverAlCarrito(): void {
    this.step = 'cart';
    this.checkoutMessage = '';
    this.checkoutErrorMessage = '';
  }

  private clearMessages(): void {
    this.checkoutMessage = '';
    this.checkoutErrorMessage = '';
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.error?.message) {
      return error.error.message;
    }
    return 'No se pudo procesar la solicitud.';
  }
}

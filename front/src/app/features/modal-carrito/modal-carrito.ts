import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, EventEmitter, Output, Inject, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CartItem } from '../../core/models/cart-item.model';
import { CouponApplyResponse } from '../../core/models/coupon.model';
import { CartService } from '../../core/services/cart.service';
import { AuthService } from '../../core/services/auth.service';
import { ORDER_SERVICE_TOKEN, IOrderService } from '../../core/services/order.service.interface';
import { CouponApiService } from '../coupons/services/coupon-api.service';

@Component({
  selector: 'app-modal-carrito',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modal-carrito.html',
  styleUrl: './modal-carrito.css',
})
export class ModalCarritoComponent {
  @Output() cerrar = new EventEmitter<void>();

  step: 'cart' | 'confirm' | 'success' = 'cart';
  checkoutMessage = '';
  checkoutErrorMessage = '';
  isCheckingOut = false;
  isApplyingCoupon = false;
  confirmedTotal = 0;
  createdOrderId: number | null = null;
  couponCode = '';
  appliedCoupon: CouponApplyResponse | null = null;

  constructor(
    private readonly cartService: CartService,
    private readonly authService: AuthService,
    private readonly cdr: ChangeDetectorRef,
    private readonly couponService: CouponApiService,
    @Inject(ORDER_SERVICE_TOKEN) private readonly orderService: IOrderService
  ) {}

  get cartItems(): CartItem[] {
    return this.cartService.getItems();
  }

  get total(): number {
    return this.cartService.getTotal();
  }

  get finalTotal(): number {
    return this.appliedCoupon?.totalConDescuento ?? this.total;
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
    this.clearAppliedCoupon();
  }

  decrease(idProducto: number): void {
    if (this.step !== 'cart') return;
    this.cartService.decreaseQuantity(idProducto);
    this.clearAppliedCoupon();
    this.clearMessages();
  }

  remove(idProducto: number): void {
    if (this.step !== 'cart') return;
    this.cartService.removeProduct(idProducto);
    this.clearAppliedCoupon();
    this.clearMessages();
  }

  applyCoupon(): void {
    this.clearMessages();

    const user = this.authService.currentUser;
    if (!user || !user.id) {
      this.checkoutErrorMessage = 'Debes iniciar sesion para usar un cupon.';
      return;
    }

    const code = this.couponCode.trim();
    if (!code) {
      this.checkoutErrorMessage = 'Ingresa el codigo del cupon.';
      return;
    }

    if (this.cartItems.length === 0) {
      this.checkoutErrorMessage = 'El carrito esta vacio.';
      return;
    }

    this.isApplyingCoupon = true;
    this.couponService.apply({
      clienteId: user.id,
      codigo: code,
      items: this.cartItems.map(item => ({
        idProducto: item.idProducto,
        cantidad: item.cantidad
      }))
    }).subscribe({
      next: (coupon) => {
        this.appliedCoupon = coupon;
        this.couponCode = coupon.codigo;
        this.checkoutMessage = 'Cupon aplicado correctamente.';
        this.isApplyingCoupon = false;
        this.cdr.detectChanges();
      },
      error: (error: unknown) => {
        this.appliedCoupon = null;
        this.checkoutErrorMessage = this.getErrorMessage(error);
        this.isApplyingCoupon = false;
        this.cdr.detectChanges();
      }
    });
  }

  removeCoupon(): void {
    this.couponCode = '';
    this.clearAppliedCoupon();
    this.clearMessages();
  }

  onCouponCodeChange(): void {
    if (this.appliedCoupon) {
      this.clearAppliedCoupon();
      this.checkoutMessage = '';
    }
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

    const expandedItems = this.cartService.expandKits(this.cartItems);
    if (expandedItems.length === 0) {
      this.checkoutErrorMessage = 'El carrito esta vacio.';
      return;
    }

    this.isCheckingOut = true;

    this.cartService.validateCartWithBackend(expandedItems).subscribe({
      next: (response) => {
        const code = this.couponCode.trim();
        if (!code) {
          this.confirmedTotal = response.total;
          this.step = 'confirm';
          this.isCheckingOut = false;
          this.cdr.detectChanges();
          return;
        }

        this.applyCouponForCheckout();
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

    const expandedItems = this.cartService.expandKits(this.cartItems);

    this.orderService.create({
      clienteId: user.id,
      items: expandedItems.map(item => ({
        idProducto: item.idProducto,
        cantidad: item.cantidad,
        ...(item.precioUnitario != null ? { precioUnitario: item.precioUnitario } : {})
      })),
      formaPago: 'EFECTIVO',
      codigoCupon: this.appliedCoupon?.codigo
    }).subscribe({
      next: (pedido) => {
        this.createdOrderId = pedido.idPedido;
        this.step = 'success';
        this.isCheckingOut = false;
        this.cartService.clearCart();
        this.removeCoupon();
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

  private clearAppliedCoupon(): void {
    this.appliedCoupon = null;
  }

  private applyCouponForCheckout(): void {
    const user = this.authService.currentUser;
    if (!user || !user.id) {
      this.checkoutErrorMessage = 'Debes iniciar sesion para comprar.';
      this.isCheckingOut = false;
      return;
    }

    this.couponService.apply({
      clienteId: user.id,
      codigo: this.couponCode.trim(),
      items: this.cartItems.map(item => ({
        idProducto: item.idProducto,
        cantidad: item.cantidad
      }))
    }).subscribe({
      next: (coupon) => {
        this.appliedCoupon = coupon;
        this.confirmedTotal = coupon.totalConDescuento;
        this.step = 'confirm';
        this.isCheckingOut = false;
        this.cdr.detectChanges();
      },
      error: (error: unknown) => {
        this.appliedCoupon = null;
        this.checkoutErrorMessage = this.getErrorMessage(error);
        this.isCheckingOut = false;
        this.cdr.detectChanges();
      }
    });
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.error?.message) {
      return error.error.message;
    }
    return 'No se pudo procesar la solicitud.';
  }
}

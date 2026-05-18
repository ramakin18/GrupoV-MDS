import { Component, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { timeout } from 'rxjs';
import { ModalCarritoComponent } from '../../modal-carrito/modal-carrito';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';
import { ProductApiService } from '../../products/services/product-api.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, ModalCarritoComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements AfterViewInit {
  title = 'BodyPaint - Pintura Corporal';
  mostrarModalCarrito = false;

  products: Product[] = [];
  isLoadingProducts = true;

  selectedProduct: Product | null = null;
  showQuickView = false;
  quickViewQuantity = 1;
  quickViewMessage = '';
  quickViewMessageType: 'success' | 'error' = 'success';

  constructor(
    private router: Router,
    readonly cartService: CartService,
    readonly authService: AuthService,
    private readonly productService: ProductApiService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loadProducts();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.loadProducts(), 100);
  }

  get cartQuantity(): number {
    return this.cartService.getTotalQuantity();
  }

  get isLoggedIn(): boolean {
    return this.authService.isLoggedIn;
  }

  get isAdmin(): boolean {
    return this.authService.currentUser?.rol === 'ADMIN';
  }

  loadProducts(): void {
    this.isLoadingProducts = true;
    this.productService.getAll('USUARIO').pipe(
      timeout(8000)
    ).subscribe({
      next: (data) => {
        this.products = data.filter(p => !p.borrado);
        this.isLoadingProducts = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.products = [];
        this.isLoadingProducts = false;
        this.cdr.detectChanges();
      }
    });
    setTimeout(() => {
      if (this.isLoadingProducts) {
        this.isLoadingProducts = false;
        this.cdr.detectChanges();
      }
    }, 10000);
  }

  openQuickView(product: Product): void {
    if (!this.isLoggedIn) return;
    this.selectedProduct = product;
    this.quickViewQuantity = 1;
    this.quickViewMessage = '';
    this.showQuickView = true;
  }

  closeQuickView(): void {
    this.showQuickView = false;
    this.selectedProduct = null;
    this.quickViewMessage = '';
  }

  addToCartFromQuickView(): void {
    if (!this.selectedProduct) return;

    const product = this.selectedProduct;
    let result = this.cartService.addProduct(product);

    if (result.success) {
      for (let i = 1; i < this.quickViewQuantity; i++) {
        result = this.cartService.increaseQuantity(product.idProducto!);
        if (!result.success) break;
      }
      this.quickViewMessage = 'Producto agregado al carrito';
      this.quickViewMessageType = 'success';
    } else {
      this.quickViewMessage = result.message || 'Error al agregar al carrito';
      this.quickViewMessageType = 'error';
    }
  }

  addToCart(product: Product): void {
    if (!this.isLoggedIn) return;
    this.cartService.addProduct(product);
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}

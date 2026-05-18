import { Component, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { timeout, forkJoin } from 'rxjs';
import { ModalCarritoComponent } from '../../modal-carrito/modal-carrito';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';
import { Kit } from '../../../core/models/kit.model';
import { CatalogItem } from '../../../core/models/catalog-item.model';
import { ProductApiService } from '../../products/services/product-api.service';
import { KitApiService } from '../../kits/services/kit-api.service';

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

  catalogItems: CatalogItem[] = [];
  isLoading = true;

  selectedItem: CatalogItem | null = null;
  showQuickView = false;
  quickViewQuantity = 1;
  quickViewMessage = '';
  quickViewMessageType: 'success' | 'error' = 'success';

  private _products: Product[] = [];
  private _kits: Kit[] = [];

  get products(): Product[] { return this._products; }
  get kits(): Kit[] { return this._kits; }

  constructor(
    private router: Router,
    readonly cartService: CartService,
    readonly authService: AuthService,
    private readonly productService: ProductApiService,
    private readonly kitService: KitApiService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loadCatalog();
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.loadCatalog(), 100);
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

  loadCatalog(): void {
    this.isLoading = true;

    forkJoin({
      products: this.productService.getAll('USUARIO').pipe(timeout(8000)),
      kits: this.kitService.getAll(true).pipe(timeout(8000))
    }).subscribe({
      next: ({ products, kits }) => {
        this._products = products.filter(p => !p.borrado);
        this._kits = kits.filter(k => k.activo && k.stock > 0);
        const productItems = this._products.map(p => this.toCatalogItem(p));
        const kitItems = this._kits.map(k => {
          const item = this.toCatalogItem(k);
          item.productos = k.productos.map(kp => {
            const prod = this._products.find(p => p.idProducto === kp.idProducto);
            return {
              idProducto: kp.idProducto,
              nombreProducto: kp.nombreProducto,
              cantidad: kp.cantidad,
              imagenUrl: prod?.imagenUrl
            };
          });
          item.imagenesCollage = k.productos
            .map(kp => this._products.find(p => p.idProducto === kp.idProducto)?.imagenUrl)
            .filter((url): url is string => !!url);
          return item;
        });
        this.catalogItems = [...productItems, ...kitItems];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.catalogItems = [];
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });

    setTimeout(() => {
      if (this.isLoading) {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    }, 10000);
  }

  private toCatalogItem(source: Product | Kit): CatalogItem {
    if ('nombreProducto' in source) {
      const p = source as Product;
      return {
        type: 'product',
        id: p.idProducto ?? 0,
        nombre: p.nombreProducto,
        descripcion: p.descripcion,
        precio: p.precio,
        stock: p.stockDisponible,
        imagenUrl: p.imagenUrl
      };
    }
    const k = source as Kit;
    return {
      type: 'kit',
      id: k.idKit ?? 0,
      nombre: k.nombre,
      descripcion: k.descripcion,
      precio: k.precio,
      stock: k.stock,
      productos: k.productos.map(p => ({
        idProducto: p.idProducto,
        nombreProducto: p.nombreProducto,
        cantidad: p.cantidad
      }))
    };
  }

  getStockLabel(item: CatalogItem): string {
    if (item.stock <= 0) return 'Sin stock';
    return `${item.stock} ${item.type === 'kit' ? 'kit(s)' : 'uds.'}`;
  }

  isOutOfStock(item: CatalogItem): boolean {
    return item.stock <= 0;
  }

  openQuickView(item: CatalogItem): void {
    if (!this.isLoggedIn) return;
    this.selectedItem = item;
    this.quickViewQuantity = 1;
    this.quickViewMessage = '';
    this.showQuickView = true;
  }

  closeQuickView(): void {
    this.showQuickView = false;
    this.selectedItem = null;
    this.quickViewMessage = '';
  }

  addToCart(item: CatalogItem): void {
    if (!this.isLoggedIn || item.type !== 'product') return;
    const product = this._products.find(p => p.idProducto === item.id);
    if (!product) return;
    this.cartService.addProduct(product);
  }

  addToCartFromQuickView(): void {
    if (!this.selectedItem) return;

    if (this.selectedItem.type === 'product') {
      const product = this._products.find(p => p.idProducto === this.selectedItem!.id);
      if (!product) return;
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
    } else {
      const kit = this._kits.find(k => k.idKit === this.selectedItem!.id);
      if (!kit) return;
      for (const kp of kit.productos) {
        const product = this._products.find(p => p.idProducto === kp.idProducto);
        if (product) {
          for (let i = 0; i < kp.cantidad; i++) {
            this.cartService.addProduct(product);
          }
        }
      }
      this.quickViewMessage = 'Kit agregado al carrito';
      this.quickViewMessageType = 'success';
    }
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

import { Component, ChangeDetectorRef, AfterViewInit, Inject } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { timeout, forkJoin } from 'rxjs';

import { ModalCarritoComponent } from '../../modal-carrito/modal-carrito';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { Product } from '../../../core/models/product.model';
import { Kit } from '../../../core/models/kit.model';
import { CatalogItem } from '../../../core/models/catalog-item.model';
import { Review, ReviewCreateDto } from '../../../core/models/review.model';

import { PRODUCT_SERVICE_TOKEN, IProductService } from '../../../core/services/product.service.interface';
import { KIT_SERVICE_TOKEN, IKitService } from '../../../core/services/kit.service.interface';
import { ReviewApiService } from '../../../core/services/review-api.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ModalCarritoComponent],
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

  // Reseñas 
  reviews: Review[] = [];
  nuevaResenaPuntuacion = 5;
  nuevaResenaDescripcion = '';
  errorResena = '';

  private _products: Product[] = [];
  private _kits: Kit[] = [];

  get products(): Product[] { return this._products; }
  get kits(): Kit[] { return this._kits; }

  constructor(
    private router: Router,
    public readonly cartService: CartService,
    public readonly authService: AuthService,
    @Inject(PRODUCT_SERVICE_TOKEN) private readonly productService: IProductService,
    @Inject(KIT_SERVICE_TOKEN) private readonly kitService: IKitService,
    private readonly reviewService: ReviewApiService,
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
          item.productos = k.productos?.map(kp => {
            const prod = this._products.find(p => p.idProducto === kp.idProducto);
            return {
              idProducto: kp.idProducto,
              nombreProducto: kp.nombreProducto,
              cantidad: kp.cantidad,
              imagenUrl: prod?.imagenUrl
            };
          }) || [];
          
          item.imagenesCollage = k.productos
            ?.map(kp => this._products.find(p => p.idProducto === kp.idProducto)?.imagenUrl)
            .filter((url): url is string => !!url) || [];
            
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
        imagenUrl: p.imagenUrl,
        promedioPuntuacion: p.promedioPuntuacion ?? 0,
        cantidadResenas: p.cantidadResenas ?? 0
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
      productos: k.productos?.map(p => ({
        idProducto: p.idProducto,
        nombreProducto: p.nombreProducto,
        cantidad: p.cantidad
      })) || []
    };
  }

  getStockLabel(item: CatalogItem): string {
    if (item.stock <= 0) return 'Sin stock';
    return `${item.stock} ${item.type === 'kit' ? 'kit(s)' : 'uds.'}`;
  }

  isOutOfStock(item: CatalogItem): boolean {
    return item.stock <= 0;
  }

  // Alternativa segura a .repeat() para cualquier versión de TypeScript
  getStars(rating: number | undefined | null): string {
    const validRating = Math.round(Number(rating) || 0);
    const clampedRating = Math.max(0, Math.min(5, validRating));
    return Array(clampedRating + 1).join('★');
  }

  openQuickView(item: CatalogItem): void {
    if (!this.isLoggedIn) return;
    this.selectedItem = item;
    this.quickViewQuantity = 1;
    this.quickViewMessage = '';
    this.errorResena = '';
    this.showQuickView = true;

    if (item.type === 'product') {
      this.reviewService.getByProducto(item.id).subscribe({
        next: (res) => {
          this.reviews = res || [];
          this.cdr.detectChanges();
        },
        error: () => {
          this.reviews = [];
          this.cdr.detectChanges();
        }
      });
    }
  }

  closeQuickView(): void {
    this.showQuickView = false;
    this.selectedItem = null;
    this.quickViewMessage = '';
    this.reviews = [];
  }

  submitReview(): void {
    // Guardamos el item seleccionado en una constante para evitar que TypeScript 
    // se queje de que puede ser nulo dentro del callback asíncrono.
    const currentItem = this.selectedItem;
    
    if (!currentItem || this.nuevaResenaPuntuacion < 1 || this.nuevaResenaPuntuacion > 5) return;
    
    const user = this.authService.currentUser;
    if (!user || user.id === undefined) {
      this.errorResena = 'Error: no se pudo verificar tu sesión.';
      return;
    }

    const dto: ReviewCreateDto = {
      puntuacion: this.nuevaResenaPuntuacion,
      descripcion: this.nuevaResenaDescripcion.trim(),
      usuario: { id: user.id },
      producto: { idProducto: currentItem.id }
    };

    this.reviewService.create(dto).subscribe({
      next: (res) => {
        this.reviews.push(res);
        this.nuevaResenaDescripcion = '';
        this.nuevaResenaPuntuacion = 5;
        this.errorResena = '';
        
        // Recálculo local seguro
        const count = currentItem.cantidadResenas || 0;
        const prom = currentItem.promedioPuntuacion || 0;
        const totalScore = (prom * count) + res.puntuacion;
        currentItem.cantidadResenas = count + 1;
        currentItem.promedioPuntuacion = Math.round((totalScore / currentItem.cantidadResenas) * 10) / 10;
        
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorResena = err.error?.message || 'No puedes reseñar este producto.';
        this.cdr.detectChanges();
      }
    });
  }

  deleteReview(review: Review): void {
    if (!confirm('¿Estás seguro de que deseas eliminar esta reseña?')) return;

    const user = this.authService.currentUser;
    if (!user || user.id === undefined) return;

    // Si es ADMIN llama al endpoint de admin, sino al de cliente
    const request$ = this.isAdmin 
      ? this.reviewService.deleteByAdmin(review.id)
      : this.reviewService.deleteByCliente(review.id, user.id);

    request$.subscribe({
      next: () => {
        // 1. Quitamos la reseña de la lista visual
        this.reviews = this.reviews.filter(r => r.id !== review.id);
        
        // 2. Recalculamos el promedio y cantidad localmente
        if (this.selectedItem) {
          const count = this.selectedItem.cantidadResenas || 0;
          const prom = this.selectedItem.promedioPuntuacion || 0;
          
          if (count <= 1) {
            this.selectedItem.cantidadResenas = 0;
            this.selectedItem.promedioPuntuacion = 0;
          } else {
            const totalScore = (prom * count) - review.puntuacion;
            this.selectedItem.cantidadResenas = count - 1;
            this.selectedItem.promedioPuntuacion = Math.round((totalScore / this.selectedItem.cantidadResenas) * 10) / 10;
          }
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert(err.error?.message || 'Error al eliminar la reseña');
      }
    });
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
      
      let allAdded = true;
      for (const kp of kit.productos) {
        const product = this._products.find(p => p.idProducto === kp.idProducto);
        if (product) {
          for (let i = 0; i < kp.cantidad; i++) {
            const res = this.cartService.addProduct(product);
            if (!res.success) allAdded = false;
          }
        }
      }
      
      if (allAdded) {
        this.quickViewMessage = 'Kit agregado al carrito';
        this.quickViewMessageType = 'success';
      } else {
        this.quickViewMessage = 'Algunos productos del kit no tienen stock suficiente.';
        this.quickViewMessageType = 'error';
      }
    }
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
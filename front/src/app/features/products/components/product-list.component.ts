import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
  FormsModule
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { PRODUCT_SERVICE_TOKEN, IProductService } from '@core/services/product.service.interface';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import {
  Product,
  ProductCreateDto,
  ProductFilters,
  ProductStatusFilter,
  ProductRow
} from '@core/models/product.model';
import { ModalCarritoComponent } from '../../modal-carrito/modal-carrito';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, RouterLink, ModalCarritoComponent], 
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent {
  products: ProductRow[] = []; 
  productForm: FormGroup;
  filterForm: FormGroup;
  isLoading = false;
  isCartOpen = false;
  errorMessage = '';

  mostrarEscasos = false; 
  selectedFile: File | null = null;
  productToConfirm: ProductRow | null = null;
  pendingChanges: any = null;

  constructor(
    @Inject(PRODUCT_SERVICE_TOKEN) private readonly productService: IProductService,
    private readonly fb: FormBuilder,
    private readonly cartService: CartService,
    private readonly authService: AuthService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.productForm = this.fb.group({
      nombreProducto: ['', [Validators.required, Validators.minLength(4)]],
      descripcion: ['', [Validators.required, Validators.minLength(5)]],
      precio: [0, [Validators.required, Validators.min(0.01), Validators.pattern(/^\d+(\.\d{1,2})?$/)]], 
      stockDisponible: [0, [Validators.required, Validators.min(0)]],
      stockMinimo: [0, [Validators.required, Validators.min(0)]] 
    });
    this.filterForm = this.fb.group({
      nombre: [''],
      precio: [null, [Validators.min(0)]],
      stockMin: [null, [Validators.min(0)]],
      stockMax: [null, [Validators.min(0)]],
      estado: ['TODOS' as ProductStatusFilter]
    }, { validators: this.stockRangeValidator });
    this.loadProducts();
  }

  get isAdmin(): boolean { return this.authService.currentUser?.rol === 'ADMIN'; }
  
  get hayProductosEscasos(): boolean { 
    return this.products.some(p => p.stockDisponible <= p.stockMinimo && !p.borrado); 
  }

  get displayedProducts(): ProductRow[] {
    if (!this.mostrarEscasos) return this.products;
    return this.products
      .filter(p => p.stockDisponible <= p.stockMinimo && !p.borrado)
      .sort((a, b) => (a.stockDisponible - a.stockMinimo) - (b.stockDisponible - b.stockMinimo));
  }

  get pageTitle(): string {
    return 'Panel de Administracion';
  }

  get pageSubtitle(): string {
    return 'Visualiza y administra el inventario de productos';
  }

  get emptyMessage(): string {
    return 'No se encontraron productos con estos filtros.';
  }

  get emptyHint(): string {
    return 'Usa el formulario de arriba para agregar tu primer producto';
  }

  toggleEscasos(): void {
    this.mostrarEscasos = !this.mostrarEscasos;
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  get listDescription(): string {
    const estado = this.filterForm.get('estado')?.value as ProductStatusFilter;
    if (estado === 'ACTIVO') return 'Muestra productos activos';
    if (estado === 'INACTIVO') return 'Muestra productos inactivos';
    return 'Incluye productos activos e inactivos';
  }

  get cartQuantity(): number {
    return this.cartService.getTotalQuantity();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.productService.getAll('ADMIN', this.getFilters()).subscribe({
      next: (data) => {
        this.products = data.map(p => ({
          ...p,
          original: { ...p },
          stockDelta: 0,
          isEditing: false
        }));
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Error al cargar productos';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
    setTimeout(() => {
      this.isLoading = false;
      this.cdr.detectChanges();
    }, 10000);
  }

  adjustStock(row: ProductRow, delta: number): void {
    const newVal = (row.stockDelta || 0) + delta;
    if (row.original.stockDisponible + newVal < 0) return;
    row.stockDelta = newVal;
    row.stockDisponible = row.original.stockDisponible + row.stockDelta;
    this.markAsEdited(row);
  }

  validatePrice(row: ProductRow): void {
    if (row.precio < 0) row.precio = row.original.precio;
    row.precio = parseFloat(row.precio.toFixed(2));
    this.markAsEdited(row);
  }

  validateStockMinimo(row: ProductRow): void {
    const parsed = Math.floor(Number(row.stockMinimo));
    row.stockMinimo = parsed < 0 ? row.original.stockMinimo : parsed;
    this.markAsEdited(row);
  }

  markAsEdited(row: ProductRow): void {
    row.isEditing = true;
  }

  toggleStatus(row: ProductRow): void {
    row.borrado = !row.borrado;
    this.markAsEdited(row);
  }

  requestSave(row: ProductRow): void {
    if (row.nombreProducto.trim() === '') {
      this.errorMessage = 'El nombre no puede estar vacío';
      return;
    }
    
    this.pendingChanges = {
      nombre: row.nombreProducto !== row.original.nombreProducto ? { old: row.original.nombreProducto, new: row.nombreProducto } : null,
      descripcion: row.descripcion !== row.original.descripcion ? { old: row.original.descripcion, new: row.descripcion } : null,
      precio: row.precio !== row.original.precio ? { old: row.original.precio, new: row.precio } : null,
      stock: row.stockDelta !== 0 ? { old: row.original.stockDisponible, new: row.stockDisponible } : null,
      stockMinimo: row.stockMinimo !== row.original.stockMinimo ? { old: row.original.stockMinimo, new: row.stockMinimo } : null,
      estado: row.borrado !== row.original.borrado ? { old: row.original.borrado ? 'Inactivo' : 'Activo', new: row.borrado ? 'Inactivo' : 'Activo' } : null
    };

    this.productToConfirm = row;
  }

  cancelSave(): void {
    if (this.productToConfirm) {
      Object.assign(this.productToConfirm, { ...this.productToConfirm.original, stockDelta: 0, isEditing: false });
    }
    this.productToConfirm = null;
    this.pendingChanges = null;
  }

  confirmSave(): void {
    if (!this.productToConfirm || !this.productToConfirm.idProducto) return;
    
    this.isLoading = true;
    const dto: Partial<ProductCreateDto> = {
      nombreProducto: this.productToConfirm.nombreProducto,
      descripcion: this.productToConfirm.descripcion,
      precio: this.productToConfirm.precio,
      stockDisponible: this.productToConfirm.stockDisponible,
      stockMinimo: this.productToConfirm.stockMinimo,
      borrado: this.productToConfirm.borrado
    };

    this.productService.update(this.productToConfirm.idProducto, dto).subscribe({
      next: () => {
        this.productToConfirm = null;
        this.loadProducts();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al guardar los cambios';
        this.isLoading = false;
        this.productToConfirm = null;
      }
    });
  }

  applyFilters(): void {
    if (this.filterForm.valid) {
      this.loadProducts();
    } else {
      this.filterForm.markAllAsTouched();
    }
  }

  clearFilters(): void {
    this.filterForm.reset({
      nombre: '',
      precio: null,
      stockMin: null,
      stockMax: null,
      estado: 'TODOS'
    });
    this.loadProducts();
  }

  onSubmit(): void {
    if (!this.isAdmin) return;
    
    if (!this.selectedFile) {
      this.errorMessage = 'Debes seleccionar una imagen para el nuevo producto';
      return;
    }

    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    const formData = new FormData();
    const productoBlob = new Blob([JSON.stringify(this.productForm.value)], {
      type: 'application/json'
    });
    
    formData.append('producto', productoBlob);
    formData.append('imagen', this.selectedFile as Blob);

    this.productService.create(formData).subscribe({
      next: () => {
        this.productForm.reset({ precio: 0, stockDisponible: 0, stockMinimo: 0 });
        this.selectedFile = null;
        const fileInput = document.getElementById('imagenProducto') as HTMLInputElement;
        if(fileInput) fileInput.value = '';
        this.loadProducts();
      },
      error: (error) => {
        this.errorMessage = error?.error?.message || 'Error al crear producto';
        this.isLoading = false;
      }
    });
  }

  addToCart(product: Product): void {
    const result = this.cartService.addProduct(product);
    if (!result.success) {
      this.errorMessage = result.message || 'No se pudo agregar el producto al carrito.';
      return;
    }
    this.errorMessage = '';
  }

  openCart(): void {
    this.isCartOpen = true;
  }

  closeCart(): void {
    this.isCartOpen = false;
  }

  private getFilters(): ProductFilters {
    const rawValues = this.filterForm.value;
    return {
      nombre: rawValues.nombre,
      precio: rawValues.precio,
      stockMin: rawValues.stockMin,
      stockMax: rawValues.stockMax,
      estado: this.isAdmin ? rawValues.estado : null
    };
  }

  private stockRangeValidator(control: AbstractControl): ValidationErrors | null {
    const stockMin = control.get('stockMin')?.value;
    const stockMax = control.get('stockMax')?.value;

    if (stockMin === null || stockMin === '' || stockMax === null || stockMax === '') {
      return null;
    }

    return Number(stockMin) <= Number(stockMax) ? null : { stockRange: true };
  }
}

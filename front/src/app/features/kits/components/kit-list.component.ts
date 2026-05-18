import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { KIT_SERVICE_TOKEN, IKitService } from '@core/services/kit.service.interface';
import { PRODUCT_SERVICE_TOKEN, IProductService } from '@core/services/product.service.interface';
import { Kit, KitCreateRequest } from '@core/models/kit.model';
import { Product } from '@core/models/product.model';

@Component({
  selector: 'app-kit-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './kit-list.component.html',
  styleUrls: ['./kit-list.component.css']
})
export class KitListComponent {
  kits: Kit[] = [];
  products: Product[] = [];
  isLoading = false;
  errorMessage = '';
  showForm = false;

  formNombre = '';
  formDescripcion = '';
  formPrecio = 0;
  formActivo = true;
  formProductos: { idProducto: number; nombreProducto: string; cantidad: number }[] = [];
  editingKitId: number | null = null;

  constructor(
    @Inject(KIT_SERVICE_TOKEN) private readonly kitService: IKitService,
    @Inject(PRODUCT_SERVICE_TOKEN) private readonly productService: IProductService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loadKits();
    this.loadActiveProducts();
  }

  get pageTitle(): string { return 'Gestion de Kits'; }
  get pageSubtitle(): string { return 'Crea y administra kits de productos'; }

  get kitCount(): number { return this.kits.length; }

  getStockLabel(stock: number): string {
    if (stock <= 0) return 'Sin stock';
    return `${stock} kit(s)`;
  }

  isLowStock(kit: Kit): boolean {
    return kit.stock <= 0 && kit.activo;
  }

  loadKits(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.kitService.getAll().subscribe({
      next: (data) => {
        this.kits = data;
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      },
      error: () => {
        this.errorMessage = 'Error al cargar kits';
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      }
    });
  }

  loadActiveProducts(): void {
    this.productService.getAll('ADMIN', { estado: 'ACTIVO' }).subscribe({
      next: (data) => {
        this.products = data.filter(p => !p.borrado);
      },
      error: () => {}
    });
  }

  openCreateForm(): void {
    this.editingKitId = null;
    this.formNombre = '';
    this.formDescripcion = '';
    this.formPrecio = 0;
    this.formActivo = true;
    this.formProductos = [{ idProducto: 0, nombreProducto: '', cantidad: 1 }];
    this.showForm = true;
    this.errorMessage = '';
  }

  openEditForm(kit: Kit): void {
    this.editingKitId = kit.idKit ?? null;
    this.formNombre = kit.nombre;
    this.formDescripcion = kit.descripcion;
    this.formPrecio = kit.precio;
    this.formActivo = kit.activo;
    this.formProductos = kit.productos.map(p => ({
      idProducto: p.idProducto,
      nombreProducto: p.nombreProducto,
      cantidad: p.cantidad
    }));
    this.showForm = true;
    this.errorMessage = '';
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingKitId = null;
    this.errorMessage = '';
  }

  get availableProducts(): Product[] {
    return this.products.filter(p => {
      const alreadySelected = this.formProductos.some(fp => fp.idProducto === p.idProducto);
      return !p.borrado && !alreadySelected;
    });
  }

  addProductRow(): void {
    this.formProductos.push({ idProducto: 0, nombreProducto: '', cantidad: 1 });
  }

  removeProductRow(index: number): void {
    if (this.formProductos.length <= 1) return;
    this.formProductos.splice(index, 1);
  }

  onProductSelect(index: number): void {
    const id = this.formProductos[index].idProducto;
    const prod = this.products.find(p => p.idProducto === id);
    this.formProductos[index].nombreProducto = prod ? prod.nombreProducto : '';
    this.formProductos[index].cantidad = 1;
  }

  saveKit(): void {
    this.errorMessage = '';

    if (!this.formNombre.trim()) {
      this.errorMessage = 'El nombre es obligatorio.';
      return;
    }
    if (!this.formDescripcion.trim()) {
      this.errorMessage = 'La descripcion es obligatoria.';
      return;
    }
    if (this.formPrecio <= 0) {
      this.errorMessage = 'El precio debe ser mayor a 0.';
      return;
    }

    const validProducts = this.formProductos.filter(p => p.idProducto > 0);
    if (validProducts.length === 0) {
      this.errorMessage = 'Debe seleccionar al menos un producto.';
      return;
    }

    const totalCantidad = validProducts.reduce((sum, p) => sum + p.cantidad, 0);
    if (validProducts.length === 1 && validProducts[0].cantidad <= 1 && totalCantidad <= 1) {
      this.errorMessage = 'El kit debe tener mas de un producto o un producto con cantidad mayor a 1.';
      return;
    }

    for (const p of validProducts) {
      if (!Number.isInteger(p.cantidad) || p.cantidad < 1) {
        this.errorMessage = 'Las cantidades deben ser numeros enteros positivos.';
        return;
      }
    }

    this.isLoading = true;
    const request: KitCreateRequest = {
      nombre: this.formNombre.trim(),
      descripcion: this.formDescripcion.trim(),
      precio: this.formPrecio,
      activo: this.formActivo,
      productos: validProducts.map(p => ({ idProducto: p.idProducto, cantidad: p.cantidad }))
    };

    const obs = this.editingKitId
      ? this.kitService.update(this.editingKitId, request)
      : this.kitService.create(request);

    obs.subscribe({
      next: () => {
        this.showForm = false;
        this.editingKitId = null;
        this.loadKits();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al guardar el kit';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  deleteKit(kit: Kit): void {
    if (!kit.idKit) return;
    if (!confirm(`Desactivar el kit "${kit.nombre}"?`)) return;

    this.isLoading = true;
    this.kitService.delete(kit.idKit).subscribe({
      next: () => {
        this.loadKits();
      },
      error: () => {
        this.errorMessage = 'Error al desactivar el kit';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}

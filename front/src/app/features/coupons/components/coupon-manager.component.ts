import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Client } from '@core/models/client.model';
import { Coupon, TipoDescuento } from '@core/models/coupon.model';
import { Product } from '@core/models/product.model';
import { ClientApiService } from '../../clients/services/client-api.service';
import { ProductApiService } from '../../products/services/product-api.service';
import { CouponApiService } from '../services/coupon-api.service';

@Component({
  selector: 'app-coupon-manager',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './coupon-manager.component.html',
  styleUrls: ['./coupon-manager.component.css']
})
export class CouponManagerComponent implements OnInit {
  clients: Client[] = [];
  products: Product[] = [];
  coupons: Coupon[] = [];
  selectedClientIds = new Set<number>();
  selectedProductIds = new Set<number>();
  isLoading = false;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  private readonly fb = inject(FormBuilder);

  couponForm = this.fb.group({
    tipoDescuento: ['PORCENTAJE' as TipoDescuento, Validators.required],
    valor: [null as number | null, [
      Validators.required,
      Validators.min(0.01),
      Validators.pattern(/^\d+(\.\d{1,2})?$/)
    ]],
    fechaDesde: ['', [
      Validators.required,
      Validators.pattern(/^\d{2}\/\d{2}\/\d{4}$/)
    ]],
    fechaHasta: ['', [
      Validators.required,
      Validators.pattern(/^\d{2}\/\d{2}\/\d{4}$/)
    ]]
  });

  constructor(
    private readonly couponService: CouponApiService,
    private readonly clientService: ClientApiService,
    private readonly productService: ProductApiService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  get tipoDescuento(): TipoDescuento {
    return this.couponForm.get('tipoDescuento')?.value as TipoDescuento;
  }

  loadData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    forkJoin({
      clients: this.clientService.getAll(),
      products: this.productService.getAll('ADMIN'),
      coupons: this.couponService.getAll()
    }).subscribe({
      next: ({ clients, products, coupons }) => {
        this.clients = clients.filter(client => client.rol !== 'ADMIN');
        this.products = products;
        this.coupons = coupons;
        this.isLoading = false;
      },
      error: (error: unknown) => {
        this.errorMessage = this.getErrorMessage(error);
        this.isLoading = false;
      }
    });
  }

  toggleClient(clientId: number | undefined, checked: boolean): void {
    if (!clientId) return;
    checked ? this.selectedClientIds.add(clientId) : this.selectedClientIds.delete(clientId);
  }

  toggleProduct(productId: number | undefined, checked: boolean): void {
    if (!productId) return;
    checked ? this.selectedProductIds.add(productId) : this.selectedProductIds.delete(productId);
  }

  selectAllClients(): void {
    this.clients
      .filter(client => !!client.id)
      .forEach(client => this.selectedClientIds.add(client.id!));
  }

  clearClientSelection(): void {
    this.selectedClientIds.clear();
  }

  clearProductSelection(): void {
    this.selectedProductIds.clear();
  }

  onSubmit(): void {
    this.successMessage = '';
    this.errorMessage = '';

    if (this.couponForm.invalid) {
      this.couponForm.markAllAsTouched();
      return;
    }

    if (this.selectedClientIds.size === 0) {
      this.errorMessage = 'Selecciona al menos un cliente.';
      return;
    }

    this.isSubmitting = true;
    const raw = this.couponForm.getRawValue();

    this.couponService.create({
      clienteIds: Array.from(this.selectedClientIds),
      productoIds: Array.from(this.selectedProductIds),
      tipoDescuento: raw.tipoDescuento as TipoDescuento,
      valor: Number(raw.valor),
      fechaDesde: raw.fechaDesde ?? '',
      fechaHasta: raw.fechaHasta ?? ''
    }).subscribe({
      next: (coupon) => {
        this.successMessage = `Cupon ${coupon.codigo} generado y notificado a ${coupon.mailsEnviados} cliente(s).`;
        this.coupons = [coupon, ...this.coupons];
        this.couponForm.reset({
          tipoDescuento: 'PORCENTAJE',
          valor: null,
          fechaDesde: '',
          fechaHasta: ''
        });
        this.selectedClientIds.clear();
        this.selectedProductIds.clear();
        this.isSubmitting = false;
      },
      error: (error: unknown) => {
        this.errorMessage = this.getErrorMessage(error);
        this.isSubmitting = false;
      }
    });
  }

  hasFieldError(fieldName: string): boolean {
    const field = this.couponForm.get(fieldName);
    return !!field && field.invalid && (field.touched || field.dirty);
  }

  getFieldError(fieldName: string): string {
    const field = this.couponForm.get(fieldName);
    if (!field) return '';
    if (field.hasError('required')) return 'Campo requerido.';
    if (field.hasError('min')) return 'Debe ser mayor a cero.';
    if (field.hasError('pattern') && fieldName.startsWith('fecha')) {
      return 'Usa formato dd/mm/aaaa.';
    }
    if (field.hasError('pattern')) return 'Usa hasta dos decimales.';
    return 'Valor invalido.';
  }

  discountDescription(coupon: Coupon): string {
    return coupon.tipoDescuento === 'PORCENTAJE'
      ? `${coupon.valor}%`
      : `$${coupon.valor.toFixed(2)}`;
  }

  productScope(coupon: Coupon): string {
    if (!coupon.productos || coupon.productos.length === 0) {
      return 'Todos los productos';
    }
    return coupon.productos.map(product => product.nombreProducto).join(', ');
  }

  usedCount(coupon: Coupon): number {
    return coupon.clientes.filter(client => client.usado).length;
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.error?.message) {
      return error.error.message;
    }
    return 'No se pudo procesar la solicitud.';
  }
}

import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, inject, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subject, takeUntil, timeout } from 'rxjs';
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
export class CouponManagerComponent implements OnInit, OnDestroy {
  clients: Client[] = [];
  products: Product[] = [];
  coupons: Coupon[] = [];
  selectedClientIds = new Set<number>();
  selectedProductIds = new Set<number>();
  loadingClients = false;
  loadingProducts = false;
  loadingCoupons = false;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroy$ = new Subject<void>();

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
    this.loadClients();
    this.loadProducts();
    this.loadCoupons();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get tipoDescuento(): TipoDescuento {
    return this.couponForm.get('tipoDescuento')?.value as TipoDescuento;
  }

  private loadClients(): void {
    this.loadingClients = true;
    this.clientService.getAll().pipe(
      timeout(15000),
      takeUntil(this.destroy$)
    ).subscribe({
      next: (clients) => {
        this.clients = clients.filter(c => c.rol !== 'ADMIN');
        this.loadingClients = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Error al cargar clientes';
        this.loadingClients = false;
        this.cdr.markForCheck();
      }
    });
  }

  private loadProducts(): void {
    this.loadingProducts = true;
    this.productService.getAll('ADMIN').pipe(
      timeout(15000),
      takeUntil(this.destroy$)
    ).subscribe({
      next: (products) => {
        this.products = products;
        this.loadingProducts = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Error al cargar productos';
        this.loadingProducts = false;
        this.cdr.markForCheck();
      }
    });
  }

  private loadCoupons(): void {
    this.loadingCoupons = true;
    this.couponService.getAll().pipe(
      timeout(15000),
      takeUntil(this.destroy$)
    ).subscribe({
      next: (coupons) => {
        this.coupons = coupons;
        this.loadingCoupons = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Error al cargar cupones';
        this.loadingCoupons = false;
        this.cdr.markForCheck();
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
      this.errorMessage = 'Seleccioná al menos un cliente.';
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
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (coupon) => {
        this.successMessage = `Cupón ${coupon.codigo} generado y notificado a ${coupon.mailsEnviados} cliente(s).`;
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
        this.cdr.markForCheck();
      },
      error: (error: unknown) => {
        this.errorMessage = this.getErrorMessage(error);
        this.isSubmitting = false;
        this.cdr.markForCheck();
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
      return 'Usá formato dd/mm/aaaa.';
    }
    if (field.hasError('pattern')) return 'Usá hasta dos decimales.';
    return 'Valor inválido.';
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
    return coupon.productos.map(p => p.nombreProducto).join(', ');
  }

  usedCount(coupon: Coupon): number {
    return coupon.clientes.filter(c => c.usado).length;
  }

  totalCount(coupon: Coupon): number {
    return coupon.clientes.length;
  }

  deleteCoupon(coupon: Coupon): void {
    this.coupons = this.coupons.filter(c => c.idCupon !== coupon.idCupon);
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.error?.message) {
      return error.error.message;
    }
    return 'No se pudo procesar la solicitud.';
  }
}

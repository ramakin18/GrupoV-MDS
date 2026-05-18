import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ORDER_SERVICE_TOKEN, IOrderService } from '@core/services/order.service.interface';
import { Pedido, SituacionPedido } from '@core/models/order.model';

const STATUS_TRANSITIONS: Record<SituacionPedido, SituacionPedido[]> = {
  RESERVADO: ['PENDIENTE', 'CANCELADO'],
  PENDIENTE: ['LISTO', 'CANCELADO'],
  LISTO: ['RETIRADO', 'CANCELADO'],
  RETIRADO: ['ENTREGADO'],
  ENTREGADO: [],
  CANCELADO: []
};

const STATUS_LABELS: Record<SituacionPedido, string> = {
  RESERVADO: 'Reservado',
  PENDIENTE: 'Pendiente',
  LISTO: 'Listo',
  RETIRADO: 'Retirado',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado'
};

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent {
  orders: Pedido[] = [];
  isLoading = false;
  errorMessage = '';
  selectedStatus = 'TODOS';
  orderToCancel: Pedido | null = null;
  cancelMotivo = '';

  constructor(
    @Inject(ORDER_SERVICE_TOKEN) private readonly orderService: IOrderService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loadOrders();
  }

  get pageTitle(): string {
    return 'Gestion de Pedidos';
  }

  get pageSubtitle(): string {
    return 'Visualiza y administra los pedidos de los clientes';
  }

  get filteredOrders(): Pedido[] {
    if (this.selectedStatus === 'TODOS') return this.orders;
    return this.orders.filter(o => o.situacion === this.selectedStatus);
  }

  get orderCount(): number {
    return this.filteredOrders.length;
  }

  getStatusLabel(situacion: SituacionPedido): string {
    return STATUS_LABELS[situacion];
  }

  getAllowedTransitions(situacion: SituacionPedido): SituacionPedido[] {
    return STATUS_TRANSITIONS[situacion] || [];
  }

  canCancel(situacion: SituacionPedido): boolean {
    return situacion !== 'ENTREGADO' && situacion !== 'CANCELADO' && situacion !== 'RETIRADO';
  }

  loadOrders(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.orderService.getAll().subscribe({
      next: (data) => {
        this.orders = data;
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      },
      error: () => {
        this.errorMessage = 'Error al cargar pedidos';
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      }
    });
  }

  onStatusFilterChange(): void {
    this.loadOrders();
  }

  updateStatus(event: Event, order: Pedido): void {
    const newStatus = (event.target as HTMLSelectElement).value as SituacionPedido;
    if (!newStatus) return;
    this.isLoading = true;
    this.orderService.updateSituacion(order.idPedido, newStatus).subscribe({
      next: () => {
        order.situacion = newStatus;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Error al actualizar el estado del pedido';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  requestCancel(order: Pedido): void {
    this.orderToCancel = order;
    this.cancelMotivo = '';
  }

  cancelCancel(): void {
    this.orderToCancel = null;
    this.cancelMotivo = '';
  }

  confirmCancel(): void {
    if (!this.orderToCancel || !this.cancelMotivo.trim()) return;
    this.isLoading = true;
    this.orderService.cancelar(this.orderToCancel.idPedido, this.cancelMotivo).subscribe({
      next: () => {
        if (this.orderToCancel) {
          this.orderToCancel.situacion = 'CANCELADO';
          this.orderToCancel.motivoCancelacion = this.cancelMotivo;
        }
        this.orderToCancel = null;
        this.cancelMotivo = '';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Error al cancelar el pedido';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}

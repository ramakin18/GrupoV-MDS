import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ORDER_SERVICE_TOKEN, IOrderService } from '@core/services/order.service.interface';
import { Pedido, SituacionPedido, DomicilioEnvio } from '@core/models/order.model';

@Component({
  selector: 'app-pending-delivery',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './pending-delivery.component.html',
  styleUrls: ['./pending-delivery.component.css']
})
export class PendingDeliveryComponent {
  orders: Pedido[] = [];
  isLoading = false;
  errorMessage = '';

  showCancelModal = false;
  cancelMotivo = '';
  cancelOrder: Pedido | null = null;

  private readonly estadoLabels: Record<SituacionPedido, string> = {
    RESERVADO: 'Pendiente',
    PENDIENTE: 'En preparacion',
    LISTO: 'Listo',
    RETIRADO: 'Retirado',
    ENTREGADO: 'Entregado',
    CANCELADO: 'Cancelado'
  };

  constructor(
    @Inject(ORDER_SERVICE_TOKEN) private readonly orderService: IOrderService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loadPendingDeliveries();
  }

  get pageTitle(): string {
    return 'Pedidos Pendientes de Entrega';
  }

  get pageSubtitle(): string {
    return 'Gestiona los pedidos que estan pendientes de ser entregados a los clientes';
  }

  get emptyMessage(): string {
    return 'No hay pedidos pendientes de entrega';
  }

  get emptyHint(): string {
    return 'Cuando un cliente realice un pedido, aparecera aqui para su gestion';
  }

  estadoLabel(situacion: SituacionPedido): string {
    return this.estadoLabels[situacion] ?? situacion;
  }

  estadoBadgeClass(situacion: SituacionPedido): string {
    switch (situacion) {
      case 'RESERVADO': return 'badge badge-warning';
      case 'PENDIENTE': return 'badge badge-info';
      default: return 'badge';
    }
  }

  domicilioCompleto(d: DomicilioEnvio): string {
    const parts = [
      d.calle,
      d.numero,
      d.piso ? `Piso ${d.piso}` : null,
      d.departamento ? `Depto ${d.departamento}` : null,
      d.localidad,
      d.provincia,
      d.pais
    ].filter(Boolean);
    return parts.join(', ');
  }

  clienteNombre(p: Pedido): string {
    return `${p.nombreCliente} ${p.apellidoCliente}`;
  }

  loadPendingDeliveries(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.orderService.getPendingDelivery().subscribe({
      next: (data) => {
        this.orders = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Error al cargar pedidos pendientes';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  markAsReady(order: Pedido): void {
    this.isLoading = true;
    this.orderService.updateSituacion(order.idPedido, 'LISTO').subscribe({
      next: () => {
        this.loadPendingDeliveries();
      },
      error: () => {
        this.errorMessage = 'Error al actualizar el estado del pedido';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openCancelModal(order: Pedido): void {
    this.cancelOrder = order;
    this.cancelMotivo = '';
    this.showCancelModal = true;
  }

  closeCancelModal(): void {
    this.showCancelModal = false;
    this.cancelOrder = null;
    this.cancelMotivo = '';
  }

  confirmCancel(): void {
    if (!this.cancelOrder || !this.cancelMotivo.trim()) return;

    this.isLoading = true;
    this.orderService.cancelar(this.cancelOrder.idPedido, this.cancelMotivo.trim()).subscribe({
      next: () => {
        this.closeCancelModal();
        this.loadPendingDeliveries();
      },
      error: () => {
        this.errorMessage = 'Error al cancelar el pedido';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}

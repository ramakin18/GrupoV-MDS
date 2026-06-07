import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { ORDER_SERVICE_TOKEN, IOrderService } from '@core/services/order.service.interface';
import { AuthService } from '@core/services/auth.service';
import { Pedido, SituacionPedido } from '@core/models/order.model';

const STATUS_LABELS: Record<SituacionPedido, string> = {
  RESERVADO: 'Reservado',
  PENDIENTE: 'Pendiente',
  LISTO: 'Listo',
  RETIRADO: 'Retirado',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado'
};

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent {
  orders: Pedido[] = [];
  isLoading = false;
  errorMessage = '';
  expandedOrderId: number | null = null;

  constructor(
    @Inject(ORDER_SERVICE_TOKEN) private readonly orderService: IOrderService,
    private readonly authService: AuthService,
    private readonly cdr: ChangeDetectorRef
  ) {
    this.loadOrders();
  }

  getStatusLabel(situacion: SituacionPedido): string {
    return STATUS_LABELS[situacion];
  }

  toggleDetalle(orderId: number): void {
    this.expandedOrderId = this.expandedOrderId === orderId ? null : orderId;
  }

  private loadOrders(): void {
    const user = this.authService.currentUser;
    if (!user || !user.id) {
      this.errorMessage = 'Debes iniciar sesion para ver tus pedidos.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.orderService.getByClienteId(user.id).subscribe({
      next: (data) => {
        this.orders = data;
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      },
      error: () => {
        this.errorMessage = 'Error al cargar tus pedidos.';
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      }
    });
  }
}

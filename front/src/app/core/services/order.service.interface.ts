import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { Pedido, PedidoCreateRequest, SituacionPedido } from '../models/order.model';

export interface IOrderService {
  getAll(estado?: string): Observable<Pedido[]>;
  getById(id: number): Observable<Pedido>;
  getPendingDelivery(): Observable<Pedido[]>;
  create(request: PedidoCreateRequest): Observable<Pedido>;
  updateSituacion(id: number, situacion: SituacionPedido): Observable<Pedido>;
  cancelar(id: number, motivo: string): Observable<Pedido>;
}

export const ORDER_SERVICE_TOKEN = new InjectionToken<IOrderService>('OrderService');

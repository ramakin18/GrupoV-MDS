import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pedido, PedidoCreateRequest, SituacionPedido } from '@core/models/order.model';
import { IOrderService } from '@core/services/order.service.interface';

@Injectable({ providedIn: 'root' })
export class OrderApiService implements IOrderService {
  private readonly apiUrl = 'http://localhost:8081/api/pedidos';

  constructor(private readonly http: HttpClient) {}

  getAll(estado?: string): Observable<Pedido[]> {
    const params: Record<string, string> = {};
    if (estado && estado !== 'TODOS') {
      params['estado'] = estado;
    }
    return this.http.get<Pedido[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${id}`);
  }

  getPendingDelivery(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${this.apiUrl}/pendientes`);
  }

  create(request: PedidoCreateRequest): Observable<Pedido> {
    return this.http.post<Pedido>(this.apiUrl, request);
  }

  updateSituacion(id: number, situacion: SituacionPedido): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.apiUrl}/${id}/situacion`, { situacion });
  }

  cancelar(id: number, motivo: string): Observable<Pedido> {
    return this.http.put<Pedido>(`${this.apiUrl}/${id}/cancelar`, { motivo });
  }
}

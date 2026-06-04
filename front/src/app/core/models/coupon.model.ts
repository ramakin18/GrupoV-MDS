import { PedidoItemRequest } from './order.model';

export type TipoDescuento = 'PORCENTAJE' | 'MONTO_FIJO';

export interface CouponClient {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  usado: boolean;
}

export interface CouponProduct {
  idProducto: number;
  nombreProducto: string;
}

export interface Coupon {
  idCupon: number;
  codigo: string;
  tipoDescuento: TipoDescuento;
  valor: number;
  fechaDesde: string;
  fechaHasta: string;
  clientes: CouponClient[];
  productos: CouponProduct[];
  mailsEnviados: number;
}

export interface CouponCreateRequest {
  clienteIds: number[];
  productoIds: number[];
  tipoDescuento: TipoDescuento;
  valor: number;
  fechaDesde: string;
  fechaHasta: string;
}

export interface CouponApplyRequest {
  clienteId: number;
  codigo: string;
  items: PedidoItemRequest[];
}

export interface CouponApplyResponse {
  codigo: string;
  tipoDescuento: TipoDescuento;
  valor: number;
  subtotal: number;
  descuento: number;
  totalConDescuento: number;
  fechaDesde: string;
  fechaHasta: string;
}

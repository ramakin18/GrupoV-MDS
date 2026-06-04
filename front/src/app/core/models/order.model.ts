export type SituacionPedido = 'RESERVADO' | 'PENDIENTE' | 'LISTO' | 'RETIRADO' | 'ENTREGADO' | 'CANCELADO';

export interface DomicilioEnvio {
  pais: string;
  provincia: string;
  localidad: string;
  calle: string;
  numero: string;
  piso?: string;
  departamento?: string;
}

export interface PedidoDetalle {
  id: number;
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface Pedido {
  idPedido: number;
  clienteId: number;
  nombreCliente: string;
  apellidoCliente: string;
  emailCliente: string;
  fecha: string;
  fechaActualizacion?: string;
  situacion: SituacionPedido;
  motivoCancelacion?: string;
  formaPago: string;
  total: number;
  subtotal?: number;
  descuento?: number;
  codigoCupon?: string;
  domicilioEnvio: DomicilioEnvio;
  detalles: PedidoDetalle[];
}

export interface PedidoCreateRequest {
  clienteId: number;
  items: PedidoItemRequest[];
  formaPago?: string;
  codigoCupon?: string;
}

export interface PedidoItemRequest {
  idProducto: number;
  cantidad: number;
}

export interface PedidoCancelRequest {
  motivo: string;
}

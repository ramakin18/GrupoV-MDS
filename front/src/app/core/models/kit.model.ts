export type KitEstado = 'ACTIVO' | 'INACTIVO';

export interface KitDetalle {
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
}

export interface Kit {
  idKit: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  estado: KitEstado;
  productos: KitDetalle[];
}

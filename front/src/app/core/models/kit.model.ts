export interface KitProductoItem {
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
}

export interface Kit {
  idKit?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  activo: boolean;
  productos: KitProductoItem[];
  promedioPuntuacion?: number;
  cantidadResenas?: number;
}

export interface KitCreateRequest {
  nombre: string;
  descripcion: string;
  precio: number;
  activo?: boolean;
  productos: { idProducto: number; cantidad: number }[];
}

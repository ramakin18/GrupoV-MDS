export type ProductViewRole = 'ADMIN' | 'USUARIO';
export type ProductStatusFilter = 'TODOS' | 'ACTIVO' | 'INACTIVO';

export interface Product {
  idProducto?: number;
  nombreProducto: string;
  descripcion: string;
  precio: number;
  stockDisponible: number;
  stockMinimo: number;
  borrado?: boolean;
  imagenUrl?: string;
  promedioPuntuacion?: number;
  cantidadResenas?: number;
}

export type ProductCreateDto = Omit<Product, 'idProducto' | 'imagenUrl' | 'promedioPuntuacion' | 'cantidadResenas'>;

export interface ProductRow extends Product {
  original: Product;
  stockDelta: number;
  isEditing: boolean;
}

export interface ProductFilters {
  nombre?: string;
  precio?: number | null;
  stockMin?: number | null;
  stockMax?: number | null;
  estado?: ProductStatusFilter | null;
}

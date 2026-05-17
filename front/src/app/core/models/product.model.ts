export type ProductViewRole = 'ADMIN' | 'USUARIO';
export type ProductStatusFilter = 'TODOS' | 'ACTIVO' | 'INACTIVO';

export interface Product {
  idProducto?: number;
  nombreProducto: string;
  descripcion: string;
  precio: number;
  stockDisponible: number;
  borrado?: boolean;
}

export type ProductCreateDto = Omit<Product, 'idProducto' | 'borrado'>;

export interface ProductFilters {
  nombre?: string;
  precio?: number | null;
  stockMin?: number | null;
  stockMax?: number | null;
  estado?: ProductStatusFilter | null;
}

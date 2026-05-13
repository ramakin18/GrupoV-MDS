export type ProductViewRole = 'ADMIN' | 'USUARIO';

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
  stock?: number | null;
}

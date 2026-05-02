export interface Product {
  idProducto?: number;
  nombreProducto: string;
  descripcion: string;
  precio: number;
  stockDisponible: number;
}

export type ProductCreateDto = Omit<Product, 'idProducto'>;

export interface Product {
  id?: number;
  nombreProducto: string;
  descripcion: string;
  precio: number;
  stockDisponible: number;
}

export type ProductCreateDto = Omit<Product, 'id'>;

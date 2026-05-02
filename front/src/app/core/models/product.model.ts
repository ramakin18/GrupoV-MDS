export interface Product {
  id?: number;
  sku: string;
  description: string;
  price: number;
  stock: number;
}

export type ProductCreateDto = Omit<Product, 'id'>;

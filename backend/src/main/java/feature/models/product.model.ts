export interface Product {
  // SKU funcionara como nuestro id
  sku: string;
  description: string;
  price: number;
  stock: number;
  status: boolean;
}
export interface CreateProductDto {
  sku: string;
  description: string;
  price: number;
  stock: number;
  // No tendremos el estado ya que este siempre se creara con un valor default (True).
}
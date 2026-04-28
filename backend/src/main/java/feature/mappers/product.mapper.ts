import { Product } from '../models/product.model';
import { CreateProductDto } from '../dtos/product.createProductDto';

export class ProductMapper {
  static toEntity(dto: CreateProductDto): Product {
    return {
      sku: dto.sku,
      description: dto.description,
      price: dto.price,
      stock: dto.stock,
      status: true 
      // status sera el estado mencionado en el dto, nos sera util para hacer softdelete en el futuro.
    };
  }
}
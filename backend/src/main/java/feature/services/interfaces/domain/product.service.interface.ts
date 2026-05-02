import { CreateProductDto } from '../../../dtos/product.createProductDto';
import { Product } from '../../../models/product.model';

export interface IProductService {
    create(dto: CreateProductDto): Product;
    listAll(): Product[];
}
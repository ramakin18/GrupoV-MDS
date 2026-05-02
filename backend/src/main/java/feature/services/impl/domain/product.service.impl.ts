import { IProductService } from '../../interfaces/domain/product.service.interface';
import { ProductRepository } from '../../../repositories/product.repository';
import { ProductMapper } from '../../../mappers/product.mapper';
import { CreateProductDto } from '../../../dtos/product.createProductDto';
import { Product } from '../../../models/product.model';

export class ProductServiceImpl implements IProductService {
    private repo = new ProductRepository();

    create(dto: CreateProductDto): Product {
        const existingProduct = this.repo.findBySku(dto.sku);
        if (existingProduct) {
            throw new Error(`El producto con SKU '${dto.sku}' ya existe.`);
        }
        // Comprobamos la unicidad del SKU
        const product = ProductMapper.toEntity(dto);
        return this.repo.save(product);
        // Si es valido lo pasamos al repositorio
    }

    listAll(): Product[] {
        return this.repo.findAll();
    }
}
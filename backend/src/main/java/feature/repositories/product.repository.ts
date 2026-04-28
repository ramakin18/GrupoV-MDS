import { Product } from '../models/product.model';

export class ProductRepository {
    private database: Product[] = [];
    // Nuestra "Base de datos" que por ahora sera un array para probar el funcionamiento.
    
    save(product: Product): Product {
        this.database.push(product);
        return product;
    }

    findAll(): Product[] {
        return this.database;
    }

    findBySku(sku: string): Product | undefined {
        return this.database.find(p => p.sku === sku);
    }
    // Este metodo nos dira si encuentra un SKU identico dentro de nuestra tabla.
}
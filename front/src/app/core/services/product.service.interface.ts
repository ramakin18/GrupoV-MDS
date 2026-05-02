import { Observable } from 'rxjs';
import { Product, ProductCreateDto } from '../models/product.model';

export interface IProductService {
  getAll(): Observable<Product[]>;
  create(product: ProductCreateDto): Observable<Product>;
  getById(id: number): Observable<Product>;
  update(id: number, product: Partial<ProductCreateDto>): Observable<Product>;
  delete(id: number): Observable<void>;
}

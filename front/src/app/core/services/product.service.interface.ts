import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, ProductCreateDto, ProductFilters, ProductViewRole } from '../models/product.model';

export interface IProductService {
  getAll(role?: ProductViewRole, filters?: ProductFilters): Observable<Product[]>;
  create(formData: FormData): Observable<Product>;
  getById(id: number): Observable<Product>;
  update(id: number, product: Partial<ProductCreateDto>): Observable<Product>;
  delete(id: number): Observable<void>;
}

export const PRODUCT_SERVICE_TOKEN = new InjectionToken<IProductService>('ProductService');
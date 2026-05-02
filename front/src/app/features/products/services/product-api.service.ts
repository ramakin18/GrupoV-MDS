import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, ProductCreateDto } from '@core/models/product.model';
import { IProductService } from '@core/services/product.service.interface';

@Injectable({ providedIn: 'root' })
export class ProductApiService implements IProductService {
  private readonly apiUrl = 'http://localhost:8080/api/productos';

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl);
  }

  create(product: ProductCreateDto): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  update(id: number, product: Partial<ProductCreateDto>): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

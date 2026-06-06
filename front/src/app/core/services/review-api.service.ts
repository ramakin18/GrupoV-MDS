import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review, ReviewCreateDto } from '@core/models/review.model';
import { environment } from '@environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewApiService {
  private readonly apiUrl = `${environment.apiUrl}/api/resenas`;

  constructor(private http: HttpClient) {}

  getByProducto(productoId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/producto/${productoId}`);
  }

  create(request: ReviewCreateDto): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, request);
  }

  deleteByCliente(id: number, usuarioId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/cliente/${usuarioId}`);
  }

  deleteByAdmin(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/admin`);
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review, ReviewCreateDto } from '@core/models/review.model';
import { environment } from '@environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewApiService {
  private readonly apiUrl = `${environment.apiUrl}/api/resenas`;

  constructor(private http: HttpClient) {}

  getAll(admin: boolean = false): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}?admin=${admin}`);
  }

  getByProducto(productoId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/producto/${productoId}`);
  }

  getByKit(kitId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/kit/${kitId}`);
  }

  create(request: ReviewCreateDto): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, request);
  }

  update(id: number, usuarioId: number, request: ReviewCreateDto): Observable<Review> {
    return this.http.put<Review>(`${this.apiUrl}/${id}/cliente/${usuarioId}`, request);
  }

  deleteByCliente(id: number, usuarioId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/cliente/${usuarioId}`);
  }

  deleteByAdmin(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/admin`);
  }

  restore(id: number): Observable<Review> {
    return this.http.put<Review>(`${this.apiUrl}/${id}/restore`, {});
  }
}

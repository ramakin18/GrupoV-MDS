import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Coupon,
  CouponApplyRequest,
  CouponApplyResponse,
  CouponCreateRequest
} from '@core/models/coupon.model';
import { environment } from '@environments/environment';

@Injectable({ providedIn: 'root' })
export class CouponApiService {
  private readonly apiUrl = `${environment.apiUrl}/api/cupones`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Coupon[]> {
    return this.http.get<Coupon[]>(this.apiUrl);
  }

  create(request: CouponCreateRequest): Observable<Coupon> {
    return this.http.post<Coupon>(this.apiUrl, request);
  }

  apply(request: CouponApplyRequest): Observable<CouponApplyResponse> {
    return this.http.post<CouponApplyResponse>(`${this.apiUrl}/aplicar`, request);
  }
}

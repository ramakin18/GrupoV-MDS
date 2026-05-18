import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Kit, KitCreateRequest } from '@core/models/kit.model';
import { IKitService } from '@core/services/kit.service.interface';
import { environment } from '@environments/environment';

@Injectable({ providedIn: 'root' })
export class KitApiService implements IKitService {
  private readonly apiUrl = `${environment.apiUrl}/api/kits`;

  constructor(private readonly http: HttpClient) {}

  getAll(activos?: boolean): Observable<Kit[]> {
    const params: Record<string, string> = {};
    if (activos !== undefined) {
      params['activos'] = String(activos);
    }
    return this.http.get<Kit[]>(this.apiUrl, { params });
  }

  getById(id: number): Observable<Kit> {
    return this.http.get<Kit>(`${this.apiUrl}/${id}`);
  }

  create(request: KitCreateRequest): Observable<Kit> {
    return this.http.post<Kit>(this.apiUrl, request);
  }

  update(id: number, request: KitCreateRequest): Observable<Kit> {
    return this.http.put<Kit>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Kit } from '../models/kit.model';

@Injectable({ providedIn: 'root' })
export class KitService {
  private readonly apiUrl = 'http://localhost:8081/api/kits';

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Kit[]> {
    return this.http.get<Kit[]>(this.apiUrl);
  }

  getById(id: number): Observable<Kit> {
    return this.http.get<Kit>(`${this.apiUrl}/${id}`);
  }
}

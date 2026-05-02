import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client, ClientCreateDto } from '@core/models/client.model';
import { IClientService } from '@core/services/client.service.interface';

@Injectable({ providedIn: 'root' })
export class ClientApiService implements IClientService {
  private readonly apiUrl = 'http://localhost:8081/api/clientes';

  constructor(private readonly http: HttpClient) {}

  register(client: ClientCreateDto): Observable<Client> {
    return this.http.post<Client>(this.apiUrl, client);
  }

  getAll(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  getById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.apiUrl}/${id}`);
  }

  update(id: number, client: Partial<ClientCreateDto>): Observable<Client> {
    return this.http.put<Client>(`${this.apiUrl}/${id}`, client);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

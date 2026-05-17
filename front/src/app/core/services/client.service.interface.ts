import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { Client, ClientCreateDto, ClientLoginDto } from '../models/client.model';

export interface IClientService {
  register(client: ClientCreateDto): Observable<Client>;
  login(credentials: ClientLoginDto): Observable<Client>;
  getAll(): Observable<Client[]>;
  getById(id: number): Observable<Client>;
  update(id: number, client: Partial<ClientCreateDto>): Observable<Client>;
  delete(id: number): Observable<void>;
}

export const CLIENT_SERVICE_TOKEN = new InjectionToken<IClientService>('ClientService');

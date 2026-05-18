import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { Kit, KitCreateRequest } from '../models/kit.model';

export interface IKitService {
  getAll(activos?: boolean): Observable<Kit[]>;
  getById(id: number): Observable<Kit>;
  create(request: KitCreateRequest): Observable<Kit>;
  update(id: number, request: KitCreateRequest): Observable<Kit>;
  delete(id: number): Observable<void>;
}

export const KIT_SERVICE_TOKEN = new InjectionToken<IKitService>('KitService');

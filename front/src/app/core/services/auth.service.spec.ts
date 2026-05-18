import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { Client } from '../models/client.model';

describe('AuthService', () => {
  let service: AuthService;
  const mockUser: Client = {
    id: 1, nombre: 'Juan', apellido: 'Perez',
    email: 'juan@test.com',
    domicilio: { pais: 'Argentina', provincia: 'BA', localidad: 'CABA', calle: 'Av Siempre Viva', numero: '123' },
    rol: 'ADMIN'
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthService);
  });

  it('should start with null user', () => {
    expect(service.currentUser).toBeNull();
    expect(service.isLoggedIn).toBe(false);
  });

  it('login should set currentUser and persist to localStorage', () => {
    service.login(mockUser);
    expect(service.currentUser).toEqual(mockUser);
    expect(service.isLoggedIn).toBe(true);
    const stored = JSON.parse(localStorage.getItem('current_user')!);
    expect(stored.email).toBe('juan@test.com');
  });

  it('logout should clear user and localStorage', () => {
    service.login(mockUser);
    service.logout();
    expect(service.currentUser).toBeNull();
    expect(service.isLoggedIn).toBe(false);
    expect(localStorage.getItem('current_user')).toBeNull();
  });

  it('should recover user from localStorage on init', () => {
    localStorage.setItem('current_user', JSON.stringify(mockUser));
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({});
    const svc = TestBed.inject(AuthService);
    expect(svc.currentUser).toEqual(mockUser);
    expect(svc.isLoggedIn).toBe(true);
  });

  it('should emit currentUser changes via currentUser$', () => {
    const values: (Client | null)[] = [];
    service.currentUser$.subscribe(user => values.push(user));
    service.login(mockUser);
    expect(values[values.length - 1]?.email).toBe('juan@test.com');
  });

  it('should handle corrupted localStorage gracefully', () => {
    localStorage.setItem('current_user', 'not-json');
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({});
    const svc = TestBed.inject(AuthService);
    expect(svc.currentUser).toBeNull();
  });
});

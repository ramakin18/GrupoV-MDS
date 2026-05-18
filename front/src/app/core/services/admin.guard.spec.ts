import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AdminGuard } from './admin.guard';
import { AuthService } from './auth.service';

describe('AdminGuard', () => {
  let guard: AdminGuard;
  let authService: AuthService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminGuard,
        { provide: Router, useValue: { parseUrl: vi.fn().mockReturnValue('login_url') } }
      ]
    });
    guard = TestBed.inject(AdminGuard);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should allow activation for ADMIN users', () => {
    authService.login({ id: 1, nombre: 'A', apellido: 'B', email: 'a@b.com', domicilio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '1' }, rol: 'ADMIN' });
    expect(guard.canActivate()).toBe(true);
    expect(guard.canMatch!()).toBe(true);
  });

  it('should redirect to /login for CLIENTE users', () => {
    authService.login({ id: 2, nombre: 'U', apellido: 'X', email: 'u@x.com', domicilio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '2' }, rol: 'CLIENTE' });
    expect(guard.canActivate()).toBe('login_url');
  });

  it('should redirect to /login when not logged in', () => {
    expect(guard.canActivate()).toBe('login_url');
    expect(router.parseUrl).toHaveBeenCalledWith('/login');
  });
});

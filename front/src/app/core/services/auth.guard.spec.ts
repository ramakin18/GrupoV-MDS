import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authService: AuthService;
  let router: Router;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: { parseUrl: vi.fn().mockReturnValue('login_url') } }
      ]
    });
    guard = TestBed.inject(AuthGuard);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should allow activation when logged in', () => {
    authService.login({ id: 1, nombre: 'A', apellido: 'B', email: 'a@b.com', domicilio: { pais: 'AR', provincia: 'BA', localidad: 'CABA', calle: 'C', numero: '1' }, rol: 'ADMIN' });
    expect(guard.canActivate()).toBe(true);
  });

  it('should redirect to /login when not logged in', () => {
    expect(guard.canActivate()).toBe('login_url');
    expect(router.parseUrl).toHaveBeenCalledWith('/login');
  });
});

import { Injectable, Inject } from '@angular/core';
import { CanActivate, CanMatch, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate, CanMatch {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    return this.checkAdmin();
  }

  canMatch(): boolean | UrlTree {
    return this.checkAdmin();
  }

  private checkAdmin(): boolean | UrlTree {
    if (this.authService.currentUser?.rol === 'ADMIN') {
      return true;
    }
    return this.router.parseUrl('/login');
  }
}

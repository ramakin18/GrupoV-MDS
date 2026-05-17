import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Client } from '../models/client.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'current_user';
  private currentUserSubject = new BehaviorSubject<Client | null>(this.loadUser());

  currentUser$: Observable<Client | null> = this.currentUserSubject.asObservable();

  get currentUser(): Client | null {
    return this.currentUserSubject.value;
  }

  get isLoggedIn(): boolean {
    return this.currentUser !== null;
  }

  login(user: Client): void {
    localStorage.setItem(this.storageKey, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.currentUserSubject.next(null);
  }

  private loadUser(): Client | null {
    const raw = localStorage.getItem(this.storageKey);
    if (!raw) return null;
    try { return JSON.parse(raw) as Client; } catch { return null; }
  }
}

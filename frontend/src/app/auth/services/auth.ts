import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface Profile {
  username: string;
  fullName: string;
  email: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})

export class Auth {

  private readonly TOKEN_KEY = 'jwtToken';
  private readonly USER_KEY = 'username';
  private readonly ROLE_KEY = 'role';

  constructor(private http: HttpClient, private router: Router) { }

  register(userData: any): Observable<any> {
    return this.http.post('/api/auth/register', userData, { responseType: 'text' });
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>('/api/auth/login', credentials).pipe(
      tap(response => this.setSession(response))
    );
  }

  private setSession(authResponse: { token: string, username: string, role: string }): void {
    localStorage.setItem(this.TOKEN_KEY, authResponse.token);
    localStorage.setItem(this.USER_KEY, authResponse.username);
    localStorage.setItem(this.ROLE_KEY, authResponse.role);
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getUserRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY);
  }

    getMyProfile(): Observable<Profile> {
    return this.http.get<Profile>('/api/auth/profile');
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private refreshTimeout: any;
  private apiUrl = 'http://localhost:8080/api/auth';

  isAuthenticated = new BehaviorSubject<boolean>(this.hasValidToken());

  constructor(private http: HttpClient, private router: Router) {}


  hasValidToken(): boolean {
    const token = localStorage.getItem('access-token');
    const expiresAt = Number(localStorage.getItem('expires-at'));
    return !!token && Date.now() < expiresAt;
  }


  scheduleTokenRefresh(): void {
    const expiresAt = Number(localStorage.getItem('expires-at'));
    const now = Date.now();
    // const delay = expiresAt - now - 60000*4; //каждую минуту
    const delay = expiresAt - now - 60000; //за минуту до
    console.log("Delay set " + delay);

    if (delay > 0) {
      this.refreshTimeout = setTimeout(() => this.refreshToken(), delay);
    }
  }


  refreshToken(): void {
    console.log("Refreshing...")

    this.http.post<{ accessToken: string; expiresAt: number }>(`${this.apiUrl}/refreshToken`, {}, { withCredentials: true })
      .subscribe({
        next: (response) => {
          localStorage.setItem('access-token', response.accessToken);
          localStorage.setItem('expires-at', response.expiresAt.toString());
          this.scheduleTokenRefresh();
          this.isAuthenticated.next(true);
        },
        error: () => {
          this.logout();
        }
      });
  }


  logout(): void {
    localStorage.removeItem('access-token');
    localStorage.removeItem('expires-at');
    clearTimeout(this.refreshTimeout);
    this.isAuthenticated.next(false);
    this.http.post<{ accessToken: string; expiresAt: number }>(`${this.apiUrl}/logout`, {}, { withCredentials: true })
    this.router.navigate(['/']);
  }
}

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

  constructor(private http: HttpClient, private router: Router) {
    window.addEventListener('storage', (event) => {
      if (event.key === 'logout') {
        this.logout(false);
      }
    });
  }

  hasValidToken(): boolean {
    const token = localStorage.getItem('access-token');
    const expiresAt = Number(localStorage.getItem('expires-at'));
    return !!token && Date.now() < expiresAt;
  }


  scheduleTokenRefresh(): void {
    const expiresAt = Number(localStorage.getItem('expires-at'));
    const now = Date.now();
    // const delay = expiresAt - now - 60000*14.5; //каждую минуту
    const delay = expiresAt - now - 60000; //за минуту до
    console.log("Delay set " + delay);

    if (delay > 0) {
      this.refreshTimeout = setTimeout(() => this.refreshToken(), delay);
    } else {
      this.refreshToken();
    }
  }


  refreshToken(): void {
    console.log("Refreshing access token...");

    this.http.post<{ accessToken: string; expiresAt: number }>(`${this.apiUrl}/refreshToken`, {email: localStorage.getItem('user-email')}, { withCredentials: true })
      .subscribe({
        next: (response) => {
          localStorage.setItem('access-token', response.accessToken);
          localStorage.setItem('expires-at', response.expiresAt.toString());
          this.scheduleTokenRefresh();
          this.isAuthenticated.next(true);
          console.log("Successfully refreshed.")
        },
        error: (err) => {
          console.log(err)
          this.logout();
        }
      });
  }


  logout(triggerStorage=true): void {
    localStorage.removeItem('access-token');
    localStorage.removeItem('expires-at');
    localStorage.removeItem('user-email');
    localStorage.removeItem('user-roles');
    localStorage.removeItem('user-id');
    clearTimeout(this.refreshTimeout);
    this.isAuthenticated.next(false);
    // this.http.post<{ accessToken: string; expiresAt: number }>(`${this.apiUrl}/logout`, {}, { withCredentials: true })

    if (triggerStorage) {
      console.log("add logout to storage")
      localStorage.setItem('logout', Date.now().toString());
      localStorage.removeItem('logout');
    }

    this.router.navigate(['/']);
  }
}

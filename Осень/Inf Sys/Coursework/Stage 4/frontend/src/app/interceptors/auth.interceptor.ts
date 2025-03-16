import { HttpInterceptorFn } from '@angular/common/http';
import {catchError, throwError} from 'rxjs';
import {AuthService} from '../services/auth.service';
import {inject} from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const token = localStorage.getItem('access-token');

  if (req.url.includes('/api/auth')) {
    return next(req);
  }

  const cloned = req.clone({
    setHeaders: token ? { Authorization: `Bearer ${token}` } : {},
  });

  return next(cloned).pipe(
    catchError((error) => {
      if (error.status === 401 && (error.error?.error === 'Token expired' || error.error?.error === 'Invalid token')) {
        authService.logout();
      }
      return throwError(() => error);
    })
  );
};


import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {


  const token = localStorage.getItem('access-token');

  if (req.url.includes('/api/auth')) {
    return next(req);
  }

  const cloned = req.clone({
    setHeaders: token ? { Authorization: `Bearer ${token}` } : {},
  });

  return next(cloned);
};


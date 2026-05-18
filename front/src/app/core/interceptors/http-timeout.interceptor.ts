import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { timeout, catchError } from 'rxjs/operators';
import { throwError, TimeoutError } from 'rxjs';

const TIMEOUT = 15000;

export const httpTimeoutInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    timeout(TIMEOUT),
    catchError(error => {
      if (error instanceof TimeoutError) {
        return throwError(() => new HttpErrorResponse({
          status: 0,
          error: { message: 'El servidor no respondió. Verificá que el backend esté corriendo.' }
        }));
      }
      return throwError(() => error);
    })
  );
};

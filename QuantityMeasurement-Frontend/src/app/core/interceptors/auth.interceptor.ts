/**
 * Auth Interceptor - DEPRECATED
 *
 * NOTE: This interceptor is no longer needed as JwtInterceptor handles Authorization.
 * Kept for backwards compatibility but does not add any headers.
 *
 * The JwtInterceptor (in jwt.interceptor.ts) is the primary interceptor that:
 * - Retrieves token from localStorage (key: 'qma_access_token')
 * - Adds Authorization: Bearer <token> header to all requests
 *
 * This class now simply passes requests through without modification.
 */
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Authorization header is added by JwtInterceptor
    // This interceptor is kept for backwards compatibility but does nothing
    return next.handle(req);
  }
}
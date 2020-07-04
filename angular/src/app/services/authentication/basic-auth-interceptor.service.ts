import { Injectable, Injector } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class BasicAuthInterceptorService implements HttpInterceptor {
  constructor(private injector : Injector){}
  intercept(req: HttpRequest<HttpInterceptor>, next: HttpHandler): Observable<HttpEvent<HttpInterceptor>> {
   
    const authenticationService = this.injector.get(AuthenticationService);
    if(authenticationService) {
      const cloned = req.clone({
         setHeaders:{
           Authorization: `Basic ${authenticationService.getToken()}`
         }
      });
     return next.handle(cloned);
   }
    else{
      next.handle(req);
    }
 }  
}

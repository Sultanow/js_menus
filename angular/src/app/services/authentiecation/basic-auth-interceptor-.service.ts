import { Injectable, Injector } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SettingsService } from '../settings/settings.service';

@Injectable()
export class BasicAuthInterceptorService implements HttpInterceptor {
  constructor(private injector : Injector){}
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
   
    const idToken = this.injector.get(SettingsService);
    if(idToken) {
      const cloned = req.clone({
         setHeaders:{
           Authorization: `Basic ${idToken.getToken()}`
         }
      });
     return next.handle(cloned);
   }
    else{
      next.handle(req);
    }
 }  
}
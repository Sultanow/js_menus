import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private backendUrl: string = 'backend/authentication';
  constructor (private http: HttpClient) { }

   ClearLocalStorgeAfter12Hour() {
		let lastclear  = localStorage.getItem('lastclear');
		let lastclearNumber : number = + lastclear;
		let time_now  = (new Date()).getTime();
		if ((time_now - lastclearNumber) > 1000 * 60 * 60 * 12) {
		  localStorage.clear();
		  localStorage.setItem('lastclear', JSON.stringify(time_now));
    } 
  }

  getToken(){
     return localStorage.getItem('token');
  }

  setToken(token : string){
    return localStorage.setItem('token', token);
 }

 login(password: string): Observable<any> {
  return this.http.post(`${this.backendUrl}/login`, password, { responseType: "text" });
}

  getIsValid():Observable<any>{
  let token =this.getToken();
  return  this.http.post(`${this.backendUrl}/isvalid`,token, { responseType: "text" });
 }
 changePassword(oldPassword: string, newPassword: string):Observable<any>{
 let changedPassword= {
   "oldPassword": oldPassword,
   "newPassword": newPassword
 }
 let headers = {"content-type": "application/json"}

  return  this.http.post(`${this.backendUrl}/changePassword`,changedPassword, {headers:headers, responseType: "text" });
 }
}

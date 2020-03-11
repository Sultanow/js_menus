import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private backendUrl: string = 'backend/webapi/';
  constructor(private http: HttpClient) { }

  getAllSettings(): Observable<any> {
    return this.http.get(`${this.backendUrl}/settings`);
  }

  updateSettings(values: JSON): Observable<any> {
    return this.http.put(`${this.backendUrl}/settings`, values)
  }
}

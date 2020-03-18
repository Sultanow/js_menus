import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private backendUrl: string = 'backend/settings';
  constructor(private http: HttpClient) { }

  getAllSettings(): Observable<any> {
    return this.http.get(`${this.backendUrl}/getAllConfig`);
  }

  updateSettings(values: JSON): Observable<any> {
    return this.http.post(`${this.backendUrl}`, values)
  }

  getTitel(): Observable<any> {
    return this.http.get(`${this.backendUrl}/title`);
  }

  setTitle(newTitel: string):Observable<any> {
    return this.http.post(`${this.backendUrl}/titel`, newTitel)
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Configuration } from 'src/app/model/configuration';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private backendUrl: string = 'backend/settings';
  constructor(private http: HttpClient) { }

  getAllSettings(): Observable<any> {
    return this.http.get(`${this.backendUrl}/getAllConfig`);
  }

  updateSettings(values: Configuration[]): Observable<any> {
    return this.http.post<Configuration[]>(`${this.backendUrl}/setConfig`, values)
      .pipe(
        catchError(this.handleError)
      );
  }

  getTitel(): Observable<any> {
    return this.http.get(`${this.backendUrl}/title`);
  }

  setTitle(newTitel: string):Observable<any> {
    return this.http.post(`${this.backendUrl}/titel`, newTitel)
  }

  private handleError(error: HttpErrorResponse) {
    if( error.error instanceof ErrorEvent) {
      console.error('An error occurred:', error.error.message)
    } else {
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`
      );
    }

    return throwError(
      'Something bad happened; please try again later.'
    );
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { Batch } from '../../model/batch';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SettingsService } from '../settings/settings.service';

@Injectable({ providedIn: 'root' })
export class BatchService {

  private batchesUrl = 'backend/elasticsearch/batches';  

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };
  constructor (
    private http: HttpClient,
    private snackBar: MatSnackBar,
    private settingsService: SettingsService) { }

  //////// CRUD methods //////////


  getTableStructure(): Observable<any> {
    return this.settingsService.getTableStructure();
  }

  /** POST: add a new batch to the server */
  addBatch(batch: any): Observable<any> {
    return this.http.post<any>(this.batchesUrl+"/", batch, this.httpOptions).pipe(
      tap((newBatch: any) => {
        console.log(`added batch w/ id ${newBatch.id}`)
      }),
      catchError(this.handleError<any>('addBatch'))
    );
  }
  /** DELETE: delete the batch from the server */
  deleteBatch(batch: any): Observable<Batch> {
    const id = typeof batch === 'number' ? batch : batch.id;
    const url = `${this.batchesUrl}/${id}`;

    return this.http.delete<any>(url, this.httpOptions).pipe(
      tap(_ => console.log(`deleted batch id=${id}`)),
      catchError(this.handleError<any>('deleteBatch'))
    );
  }
  /** GET batches from the server */
  getBatches(): Observable<any[]> {
    return this.http.get<any[]>(`${this.batchesUrl}/`)
      .pipe(
        tap((newBatches: any[]) => console.log(`fetched batches ${newBatches[0]}, ${newBatches[1]}`)),
        catchError(this.handleError<any[]>('getBatches', []))
      );
  }

  /** GET batch by id. Will 404 if id not found */
  getBatch(id: string): Observable<any> {
    const url = `${this.batchesUrl}/${id}`;
    return this.http.get<any>(url).pipe(
      tap(_ => console.log(`fetched batch id=${id}`)),
      catchError(this.handleError<any>(`getBatch id=${id}`))
    );
  }

  /** PUT: update the batch on the server */
  updateBatch(batch: any): Observable<any> {  
    return this.http.put(`${this.batchesUrl}/${batch.id}`, batch, this.httpOptions).pipe(
      tap(result => console.log(`updated batch id=${batch.id}, result: ${result}`)),
      catchError(this.handleError<any>('updateBatch'))
    );
  }



  //////// handlers //////////

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: HttpErrorResponse): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);
      if(error.status == 401) this.snackBar.open("Unauthorized", "Ok", { duration: 3000 });
      // Let the app keep running by returning an empty result.
      return of(result);
    };
  }

}

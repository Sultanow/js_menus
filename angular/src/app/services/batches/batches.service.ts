import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { Batch } from '../../model/batch';
import { MOCKBATCHES } from '../../model/mockBatches';

@Injectable({ providedIn: 'root' })
export class BatchService {

  mockBatches = MOCKBATCHES;
  
  private batchesUrl = 'backend/elasticsearch/batches';  

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor (private http: HttpClient) { }

  //////// CRUD methods //////////

  /** POST: add a new batch to the server */
  addBatch(batch: Batch): Observable<Batch> {
    //this.mockBatches.push(batch);
    return of(batch);
    
    return this.http.post<Batch>(this.batchesUrl, batch, this.httpOptions).pipe(
      tap((newBatch: Batch) => this.log(`added batch w/ id=${newBatch.id}`)),
      catchError(this.handleError<Batch>('addBatch'))
    );
  }

  /** GET batches from the server */
  getBatches(): Observable<Batch[]> {
    return of(this.mockBatches);

    return this.http.get<Batch[]>(`${this.batchesUrl}/column`)
      .pipe(
        tap((newBatches: Batch[]) => this.log(`fetched batches ${newBatches[0]}, ${newBatches[1]}`)),
        catchError(this.handleError<Batch[]>('getBatches', []))
      );
  }

  /** GET batch by id. Will 404 if id not found */
  getBatch(id: string): Observable<Batch> {
    const url = `${this.batchesUrl}/${id}`;
    return this.http.get<Batch>(url).pipe(
      tap(_ => this.log(`fetched batch id=${id}`)),
      catchError(this.handleError<Batch>(`getBatch id=${id}`))
    );
  }

  /** PUT: update the batch on the server */
  updateBatch(batch: Batch): Observable<any> {
    return of(batch);
    return this.http.put(this.batchesUrl, batch, this.httpOptions).pipe(
      tap(_ => this.log(`updated batch id=${batch.id}`)),
      catchError(this.handleError<any>('updateBatch'))
    );
  }

  /** DELETE: delete the batch from the server */
  deleteBatch(batch: Batch): Observable<Batch> {
    return of(batch);
    
    const id = typeof batch === 'number' ? batch : batch.id;
    const url = `${this.batchesUrl}/${id}`;

    return this.http.delete<Batch>(url, this.httpOptions).pipe(
      tap(_ => this.log(`deleted batch id=${id}`)),
      catchError(this.handleError<Batch>('deleteBatch'))
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
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      //console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result);
    };
  }

  /** Log a BatchService message with the MessageService */
  private log(message: string) {
    console.log(`BatchService: ${message}`);
  }
}

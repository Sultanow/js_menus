import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

import { DependencyChart } from '../../model/dependencychart';

@Injectable({ providedIn: 'root' })
export class DependencyService {

  private dependenciesUrl = 'api/dependencies';  // TODO: das richtige Backend anbinden

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor (private http: HttpClient) { }

  //////// CRUD methods //////////

  /** POST: add a new dependency to the server */
  addDependency(dependency: DependencyChart): Observable<DependencyChart> {
    return this.http.post<DependencyChart>(this.dependenciesUrl, dependency, this.httpOptions).pipe(
      tap((newDependency: DependencyChart) => this.log(`added dependency w/ id=${newDependency.id}`)),
      catchError(this.handleError<DependencyChart>('addDependency'))
    );
  }

  /** GET dependencies from the server */
  getDependencies(): Observable<DependencyChart[]> {
    return this.http.get<DependencyChart[]>(this.dependenciesUrl)
      .pipe(
        tap(_ => this.log('fetched dependencies')),
        catchError(this.handleError<DependencyChart[]>('getDependencies', []))
      );
  }

  /** GET dependency by id. Will 404 if id not found */
  getDependency(id: string): Observable<DependencyChart> {
    const url = `${this.dependenciesUrl}/${id}`;
    return this.http.get<DependencyChart>(url).pipe(
      tap(_ => this.log(`fetched dependency id=${id}`)),
      catchError(this.handleError<DependencyChart>(`getDependency id=${id}`))
    );
  }

  /** PUT: update the dependency on the server */
  updateDependency(dependency: DependencyChart): Observable<any> {
    return this.http.put(this.dependenciesUrl, dependency, this.httpOptions).pipe(
      tap(_ => this.log(`updated dependency id=${dependency.id}`)),
      catchError(this.handleError<any>('updateDependency'))
    );
  }

  /** DELETE: delete the dependency from the server */
  deleteDependency(dependency: DependencyChart | number): Observable<DependencyChart> {
    const id = typeof dependency === 'number' ? dependency : dependency.id;
    const url = `${this.dependenciesUrl}/${id}`;

    return this.http.delete<DependencyChart>(url, this.httpOptions).pipe(
      tap(_ => this.log(`deleted dependency chart id=${id}`)),
      catchError(this.handleError<DependencyChart>('deleteDependencyChart'))
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
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }

  /** Log a DependencyService message with the MessageService */
  private log(message: string) {
    console.log(`DependencyService: ${message}`);
  }
}

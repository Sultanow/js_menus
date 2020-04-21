import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatisticService {
  private backendURLStatistic: string = '/backend/statistic';
  constructor(private http: HttpClient) { }

  uploadFile() {
    throw "not implemented"
  }

  getChartData() : Observable<any> {
    return this.http.get(`${this.backendURLStatistic}/data`);
    
  }
}

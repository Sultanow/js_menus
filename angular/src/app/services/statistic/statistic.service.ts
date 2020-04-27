import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatisticService {
  deleteChart(chartName: string) {
    let params = new HttpParams().set("chart",chartName);
    return this.http.delete(`${this.backendURLStatistic}/deleteChart`, {params});
  }
  
  updateData(chartname: string, file: File) : Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("chart", chartname);

    return this.http.post(`${this.backendURLStatistic}/updateData`, formData)
  }
  private backendURLStatistic: string = '/backend/statistic';
  constructor(private http: HttpClient) { }

  getChartData(chartname: string) : Observable<any> {
    let params = new HttpParams().set("chart",chartname); 
    return this.http.get(`${this.backendURLStatistic}/chartData`, {params});
    
  }

  getChartNames(): Observable<any> {
    return this.http.get(`${this.backendURLStatistic}/allChartNames`, {responseType: "text"});
  }

  createChart(chartname: string, file: File, description: string): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("chartName", chartname);
    formData.append("description", description);
    
    return this.http.post(`${this.backendURLStatistic}/createChart`, formData, {responseType: "text"});
  }
}

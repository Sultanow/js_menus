import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StatisticGroup } from '../model/statisticGroup';
import { StatisticData } from '../model/statisticData';

@Injectable({
  providedIn: 'root'
})
export class StatisticService {
  private backendURLStatistic: string = '/backend/statistic';
  constructor (private http: HttpClient) { }

  getGroups(): Observable<string[]> {
    return this.http.get<string[]>(`${this.backendURLStatistic}/groups`);
  }
  deleteChart(chartName: string) {
    let params = new HttpParams().set("chart", chartName);
    return this.http.delete(`${this.backendURLStatistic}/deleteChart`, { params });
  }

  updateData(chartname: string, file: File): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("chart", chartname);

    return this.http.post(`${this.backendURLStatistic}/updateData`, formData);
  }

  getChartData(chartname: string): Observable<StatisticData> {
    let params = new HttpParams().set("chart", chartname);
    return this.http.get<StatisticData>(`${this.backendURLStatistic}/chartData`, { params });

  }

  getChartNames(): Observable<StatisticGroup[]> {
    return this.http.get<StatisticGroup[]>(`${this.backendURLStatistic}/allChartNames`);
  }

  createChart(
    chartname: string,
    groupname: string,
    file: File,
    description: string
  ): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    formData.append("chartName", chartname);
    formData.append("description", description);
    formData.append("groupName", groupname);

    return this.http.post(`${this.backendURLStatistic}/createChart`, formData, { responseType: "text" });
  }

  getTimeseriesDates(chartName: string): Observable<string[]> {
    let params = new HttpParams().set("chart", chartName);
    return this.http.get<string[]>(`${this.backendURLStatistic}/timeseriesDates`, { params });
  }
  
  getChartDataForDate(chartName: string, date: string): Observable<StatisticData> {
    let params = new HttpParams().set("chart", chartName).set("date", date).set("update", "true");
    return this.http.get<StatisticData>(`${this.backendURLStatistic}/chartData`, { params });
  }
  
}

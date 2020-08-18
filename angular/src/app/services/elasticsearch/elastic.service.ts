import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class ElasticService {


  private backendElasticsearchUrl: string = 'backend/elasticsearch';
  constructor(private http: HttpClient) { }

  getAllKeys(): Observable<any> {
    return this.http.get<string[]>(`${this.backendElasticsearchUrl}/getAllKeys`, {});
  }
  getAllHostName(): Observable<any> {
    return this.http.get<string[]>(`${this.backendElasticsearchUrl}/getAllHostName`, {});
  }
  getLastValue(hostName: string, itemKey: string): Observable<any> {
    let params = new HttpParams();

    params = params.append('hostname', hostName);
    params = params.append('itemkey', itemKey);
    return this.http.get(`${this.backendElasticsearchUrl}/getLastValue`,
      { params: params, responseType: "text" });
  }
  getHostInformation(): Observable<any> {
    return this.http.get<JSON>(`${this.backendElasticsearchUrl}/getallhostinformation`);
  }

  saveSollwerte(hostName: string, key: string, sollValue: string): Observable<any> {
    let params = new HttpParams();

    params = params.append('hostname', hostName);
    params = params.append('key', key);
    params = params.append('sollvalue', sollValue);

    return this.http.get(`${this.backendElasticsearchUrl}/insertSollWerte`, { params: params, responseType: "text" });
  }

  getSollwerte(): Observable<any> {
    return this.http.get<JSON>(`${this.backendElasticsearchUrl}/getSollWerte`, {});
  }

  getSollValue(hostName: string, key: string): Observable<any> {
    let params = new HttpParams();

    params = params.append('hostname', hostName);
    params = params.append('key', key);

    return this.http.get(`${this.backendElasticsearchUrl}/getSollWValueByHostnameAndKey`,
      { params: params, responseType: "text" });
  }

  getHostinformationByNames(hsots: string[]): Observable<any> {
    let params = new HttpParams();
    hsots.forEach(hostName => {
      params = params.append('hostname', hostName);
    });
    return this.http.get<JSON>(`${this.backendElasticsearchUrl}/getHostInformationByListOfNames`,
      { params: params });
  }

  saveHistorySollWerte(hostName: string, key: string, sollValue: string) {
    let params = new HttpParams();

    params = params.append('hostname', hostName);
    params = params.append('key', key);
    params = params.append('sollvalue', sollValue);

    return this.http.get(`${this.backendElasticsearchUrl}/insertHistorySollWert`, { params: params, responseType: "text" });
  }

  getHistoryBetweenTwoDates(firstDate: string, secondDate: string, indexName: string) {
    let params = new HttpParams();

    params = params.append('unixtime1', firstDate);
    params = params.append('unixtime2', secondDate);
    params = params.append('indexname', indexName);
    console.log(params)
    return this.http.get<string[]>(`${this.backendElasticsearchUrl}/gethistorybetweentowdatum`, { params: params });
  }

  getHistoryindexnames() {
    return this.http.get<string[]>(`${this.backendElasticsearchUrl}/allHistoryIndexName`, {});
  }
}

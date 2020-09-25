import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class ElasticService {


  private backendElasticsearchUrl: string = 'backend/elasticsearch';
  private backendElasticsearchUrlHosts: string = 'backend/elasticsearch/hosts';
  private backendElasticsearchUrlHostInfo: string = 'backend/elasticsearch/hostInformation';
  private backendElasticsearchUrlHistory: string = 'backend/elasticsearch/history';
  private backendElasticsearchUrlExpValue: string = 'backend/elasticsearch/expectedValues';
  constructor(private http: HttpClient) { }

  getAllKeys(): Observable<any> {
    return this.http.get<string[]>(`${this.backendElasticsearchUrlHostInfo}/all/keys`, {});
  }
  getAllHostName(): Observable<any> {
    return this.http.get<string[]>(`${this.backendElasticsearchUrlHosts}/hostnames/`, {});
  }

  getLastValue(hostName: string, itemKey: string): Observable<any> {
    return this.http.get<string>(`${this.backendElasticsearchUrlHostInfo}/lastValue/` + hostName + '/' + itemKey, {});
  }

  getHostInformation(): Observable<any> {
    return this.http.get<JSON[]>(`${this.backendElasticsearchUrlHostInfo}/all`);
  }

  saveExpectedValue(hostName: string, key: string, expectedValue: string): Observable<any> {
    console.log(hostName + '/' + key + '/' + expectedValue);
    return this.http.put(`${this.backendElasticsearchUrlExpValue}/` + hostName + '/' + key + '/' + expectedValue, {});
  }

  // getExpectedValues(): Observable<any> {
  //   return this.http.get<JSON>(`${this.backendElasticsearchUrlExpValue}/all`, {});
  // }

  getExpectedValueByHostnameAndKey(hostName: string, key: string):Observable<any> {
    return this.http.get(`${this.backendElasticsearchUrlExpValue}/` + hostName + '/' + key,{responseType:"text"});
  }
 public listhost: string[] = [];
  getHostinformationByNames(hosts: string[]): void{
    hosts.forEach(host => {
      let hostName: string = host
      this.http.get<string>(`${this.backendElasticsearchUrlHostInfo}/` + hostName, {}).subscribe(data =>{
        this.listhost.push(data);
      });  
    });
  }


  getHistoryBetweenTwoDates(firstDate: string, secondDate: string, indexName: string):Observable<any> {
    return this.http.get<string[]>(`${this.backendElasticsearchUrlHistory}/` + firstDate + '/' + secondDate + '/' + indexName,
      {});
  }

  getHistoryindexnames():Observable<any> {
    return this.http.get<string[]>(`${this.backendElasticsearchUrl}/historyIndexNames`, {});
  }
}

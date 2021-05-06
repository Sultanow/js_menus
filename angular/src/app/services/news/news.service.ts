import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private static readonly urlRecentNews: string = "backend/news";

  constructor(private http: HttpClient) { }

  getRecentNews(amount: number = 10, offset: number = 0): Observable<any[]> {
    let params: HttpParams = new HttpParams()
      .set("amount", amount.toString())
      .set("offset", offset.toString());

    return this.http.get<object[]>(
      NewsService.urlRecentNews, 
      { params: params });
  }
}

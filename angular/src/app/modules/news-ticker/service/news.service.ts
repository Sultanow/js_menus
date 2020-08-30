import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NewsItem } from '../model/news-item';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private backendURLNews: string = '/backend/news';
  constructor (private http: HttpClient) { }

  getAllVisibleNews(): Observable<NewsItem[]> {
    return this.http.get<NewsItem[]>(this.backendURLNews);
  }

  getAllNews(): Observable<NewsItem[]> {
    return this.http.get<NewsItem[]>(`${this.backendURLNews}/all`);
  }

  updateNews(item: NewsItem): Observable<void> {
    return this.http.post<void>(`${this.backendURLNews}/${item.id}`, item);
  }

  deleteNews(item: NewsItem): Observable<void> {
    return this.http.delete<void>(`${this.backendURLNews}/${item.id}`);
  }

  createNews(item: NewsItem): Observable<number> {
    return this.http.put<number>(`${this.backendURLNews}`, item);
  }

  findNewsByTag(searchValue: string) : Observable<NewsItem[]>{
    return this.http.get<NewsItem[]>(`${this.backendURLNews}/tag/${searchValue}`);
  }
  

}


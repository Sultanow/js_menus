import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class JsonServerApiService {
  private baseUrl = 'http://192.168.122.120:3000';

  constructor(private http: HttpClient) {}

  // TODO: Interface for Minicards
  getMinicards() {
    return this.http.get<any[]>(`${this.baseUrl}/minicards`);
  }

  getMinicard(id: number) {
    return this.http.get<any>(`${this.baseUrl}/minicards/${id}`);
  }
}

import { Component, OnInit } from '@angular/core';
import { JsonServerApiService } from '../services/json-server.service';

@Component({
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  public minicards: any[] = [];

  constructor(private api: JsonServerApiService) {}

  ngOnInit() {
    this.api.getMinicards().subscribe((mcs) => {
      this.minicards = mcs;
    });
  }
}

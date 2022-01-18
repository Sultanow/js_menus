import { Component, OnInit } from '@angular/core';
import { ConfigurationItemsService } from 'src/app/services/configuration/configuration-items.service';
//import { ElasticService } from 'src/app/services/elasticsearch/elastic.service';

@Component({
  selector: 'app-historybetweentowdates',
  templateUrl: './historybetweentowdates.component.html',
  styleUrls: ['./historybetweentowdates.component.css']
})
export class HistorybetweentowdatesComponent implements OnInit {

  constructor(private configItemService: ConfigurationItemsService) { }

  records: string[];
  ngOnInit(): void {
    this.records = this.configItemService.historyRecords;
  }
}

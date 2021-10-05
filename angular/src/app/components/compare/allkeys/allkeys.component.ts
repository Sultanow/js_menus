import { Component, OnInit} from '@angular/core';
import { ConfigurationItem } from 'src/app/model/configurationItem';
import { ElasticService } from 'src/app/services/elasticsearch/elastic.service';

@Component({
  selector: 'app-allkeys',
  templateUrl: './allkeys.component.html',
  styleUrls: ['./allkeys.component.css']
})
export class AllkeysComponent implements OnInit {

  constructor(private elasticService: ElasticService) { }
  items: ConfigurationItem[] = [];
  allKey: string[] = [];
  dialogClose: boolean = true;
  ngOnInit(): void {
    this.displayAllkeys();
  }

  displayAllkeys() {
    this.elasticService.getAllKeys().subscribe(data => {
      this.allKey = data;
    })
  }
}    

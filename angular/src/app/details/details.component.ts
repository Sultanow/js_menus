import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ConfigurationService } from '../services/configuration.service';
import { Configuration } from '../model/configuration';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styleUrls: ['./details.component.css']
})
export class DetailsComponent implements OnInit {

  configData:Configuration[] = [];

  @Input() showDetails: boolean;
  // Notify parent (app) when details box should close
  @Output() notifyDetailsClose = new EventEmitter<boolean>();

  constructor(private configService: ConfigurationService) { }

  ngOnInit() {
  }

  ngOnChanges(changes) {
     if(this.showDetails){
       this.getConfigData();
     }
  }

  getConfigData(){
    let p:Promise<Configuration[]> = this.configService.getRedisConfiguration("a220", "a610", "a620", "a999");

    p.then(response => {
      this.configData = response; // udpate values in ui table
    })

  }

  close(){
    this.showDetails = false;
    this.notifyDetailsClose.emit(true);
  }


}

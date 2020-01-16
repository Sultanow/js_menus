import { Component, OnInit, Input, Inject, EventEmitter, Output } from '@angular/core';
import { ConfigurationService } from 'src/app/services/configuration.service';
import { Configuration } from 'src/app/model/configuration';

@Component({
  selector: 'g[o-north-menu]',
  templateUrl: './north-menu.component.html'
})
export class NorthMenuComponent implements OnInit {
  
  @Input() isOpen: boolean;

  // Notify parent (app) when details box should open
  @Output() notifyBatchesOpen = new EventEmitter<boolean>();
  @Output() notifyCompareOpen = new EventEmitter<boolean>();

  constructor(private configService: ConfigurationService) { }

  openDetails() {
      this.notifyBatchesOpen.emit(true);
  }
  
  openCompare() {
    this.notifyCompareOpen.emit(true);
  }
  
  ngOnInit() {
  }

  /* openDetails(){
    var config = this.configService.getRedisConfiguration("testkey");
    config.then(resolved => console.log(resolved), error => console.error(error));
  } */

}

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
  @Output() notifyEventOpen = new EventEmitter<string>();

  constructor(private configService: ConfigurationService) { }

  openTest() {
    this.notifyEventOpen.emit("batches");
      }
  
  openDev() {
    this.notifyEventOpen.emit("compare");
  }

  openProd() {
    this.notifyEventOpen.emit("prod");
  }
  
  ngOnInit() {
  }

  /* openDetails(){
    var config = this.configService.getRedisConfiguration("testkey");
    config.then(resolved => console.log(resolved), error => console.error(error));
  } */

}

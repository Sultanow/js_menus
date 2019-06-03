import { Component, OnInit, Input, Inject } from '@angular/core';
import { ConfigurationService } from 'src/app/services/configuration.service';
import { Configuration } from 'src/app/model/configuration';

@Component({
  selector: 'g[o-north-menu]',
  templateUrl: './north-menu.component.html'
})
export class NorthMenuComponent implements OnInit {
  
  @Input() isOpen: boolean;
  
  constructor(private configService: ConfigurationService) { }

  ngOnInit() {
  }

  openDetails(){
    var config = this.configService.getConfiguration("test");
    config.then(resolved => console.log(resolved), error => console.error(error));
  }

  test(){
    console.log("test");
  }

}

import { Component, OnInit, Input } from '@angular/core';
import { ConfigurationItemsService } from 'src/app/services/configuration/configuration-items.service';
import { ConfigurationItem } from 'src/app/model/configurationItem';

@Component({
  selector: 'app-configuration-view',
  templateUrl: './configuration-view.component.html',
  styleUrls: [ './configuration-view.component.css', '../viewbox/viewbox.component.css' ]
})
export class ConfigurationViewComponent implements OnInit {

  @Input() showConfigView: boolean;
  @Input() viewType: string;
  config: ConfigurationItem[] = [];

  constructor (private configService: ConfigurationItemsService) { }

  ngOnInit() {

  }

  ngOnChanges(changes) {
    if (this.showConfigView) {
      this.config = this.configService.itemlist;
      console.log("After");
    }
  }
}

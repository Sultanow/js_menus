import { Component, OnInit, Input } from '@angular/core';
import { ConfigurationItemsService } from 'src/app/services/configuration/configuration-items.service';
import { ConfigurationItem } from 'src/app/model/configurationItem';
import { ServerConfiguration } from 'src/config/ServerConfiguration';

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

  async ngOnChanges(changes) {
    if (this.showConfigView) {
      this.configService.getServerConfiguration(ServerConfiguration.ENV_LIST).subscribe(data => {
        this.configService.addConfiguration(this.configService.createServerConf(data));
      });
      console.log("After");
    }
  }
}

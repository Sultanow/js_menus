import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ConfigurationService } from 'src/app/services/configuration.service';
import { ConfigurationItem } from 'src/app/model/configurationItem';
import { ConfigurationItemsService } from 'src/app/services/configuration/configuration-items.service';

@Component({
  selector: 'app-right-management',
  templateUrl: './right-management.component.html',
  styleUrls: [ './right-management.component.css', '../viewbox/viewbox.component.css' ]
})
export class RightManagementComponent implements OnInit {

  @Input() showRightManagement: boolean;
  @Input() viewType: string;
  @Output() notifyTitle = new EventEmitter<string>();
  headline: string;
  infos: ConfigurationItem[];

  constructor (private configurationService: ConfigurationItemsService) { }

  ngOnInit() {
  }

  ngOnChanges(changes) {
    if (this.showRightManagement) {
      if (this.viewType === "test") {
        this.headline = "Rechte für Testumgebungen";
        this.infos = this.configurationService.getRightConfigurationTest();
        this.notifyTitle.emit("Test");
      } else if (this.viewType === "dev") {
        this.headline = "Rechte für Dev-Umgebungen";
        this.infos = this.configurationService.getRightConfigurationDev();
        this.notifyTitle.emit("Dev");
      } else if (this.viewType === "prod") {
        this.headline = "Rechte für Prod-Umgebungen";
        this.infos = this.configurationService.getRightConfigurationProd();
        this.notifyTitle.emit("Confluence");
      }
    }
  }
}

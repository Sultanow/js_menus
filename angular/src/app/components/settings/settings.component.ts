import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Configuration } from 'src/app/model/configuration';
import { Observable } from 'rxjs';
import { SettingsService } from 'src/app/services/settings/settings.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: [ './settings.component.css' ]
})
export class SettingsComponent implements OnInit {
  @Input() showSettings: boolean;
  @Output() notifyTitle = new EventEmitter<string>();

  settings: Observable<Configuration[]>;

  constructor (private settingsService: SettingsService) {
  }

  ngOnInit() {
    this.reloadData();
  }

  ngOnChanges(changes) {
    if (this.showSettings) {
      this.notifyTitle.emit("Einstellungen");
    }
  }

  reloadData() {
    this.settings = this.settingsService.getAllSettings();
  }

  updateSettings() {
    let changedSettings: JSON = this.checkChangedSettings();
    this.settingsService.updateSettings(changedSettings).subscribe(data => {
      console.log(data);
      this.reloadData();
    }, error => console.log(error));
  }

  checkChangedSettings(): JSON {
    throw new Error("Method not implemented.");
  }
}

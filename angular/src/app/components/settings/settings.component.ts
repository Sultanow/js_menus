import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Configuration } from 'src/app/model/configuration';
import { SettingsService } from 'src/app/services/settings/settings.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: [ './settings.component.css' ]
})
export class SettingsComponent implements OnInit {
  @Input() showSettings: boolean;
  @Output() notifyshowViewBoxClose = new EventEmitter<boolean>();
  @Output() notifyTitle = new EventEmitter<string>();

  settings: Configuration[];
  version: string;
  globalTitle: string;



  constructor (private settingsService: SettingsService, private titleService: Title) {
  }

  ngOnInit() {
    this.reloadData();
    this.showVersion();
    this.titleService.setTitle(this.globalTitle);

  }

  ngOnChanges(changes) {
    if (this.showSettings) {
      this.notifyTitle.emit("Einstellungen");
    }
  }

  getNewTitle() {
    this.settingsService.getTitel().subscribe(title => {
      if (title !== "")
        this.globalTitle = title;
      this.settingsService.changeTitle(this.globalTitle);
      this.titleService.setTitle(this.globalTitle);

    });

  }

  reloadData() {
    this.settingsService.getAllSettings().subscribe(data => {
      this.settings = data;
      console.log(this.settings);
    });

  }

  onChangeItem(event) {
    this.settings.forEach(setting => {
      if (setting.key === event.srcElement.name) {
        if (setting.oldValue === undefined) {
          setting.value !== undefined ? setting.oldValue = setting.value : setting.oldValue = "";
          setting.value = event.srcElement.value;
        }
        return;
      }
    });
  }

  onSaveSettings() {
    let changedSettings: Configuration[] = [];
    this.settings.forEach(setting => {
      if (setting.oldValue !== undefined)
        changedSettings.push(setting);
    });
    if (changedSettings.length === 0) {
      console.log("No changed settings");
      return;
    }
    this.settingsService.updateSettings(changedSettings).subscribe(data => {
      console.log(data);
      this.reloadData();
      this.getNewTitle();
    }, error => console.log(error));
  }
  showVersion() {
    this.settingsService.getVersion().subscribe(data => {
      this.version = data;
      console.log(this.version);
    });

  }

  logout() {
    localStorage.removeItem("token");
    this.notifyshowViewBoxClose.emit(true);
    }
}

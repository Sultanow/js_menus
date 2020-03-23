import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { SettingsService } from 'src/app/services/settings/settings.service';

@Component({
  selector: 'app-status-warnings',
  templateUrl: './status-warnings.component.html',
  styleUrls: [ './status-warnings.component.css' ]
})
export class StatusWarningsComponent implements OnInit {

  @Input() showStatusWarnings: boolean;
  scrollingMenu: boolean = false;
  globalTitle: string = "Dashboard";
  
  @ViewChild('globalLogo') logo: ElementRef;
  globalLogo: string = "<svg viewBox='0 0 80 2' xmlns='http://www.w3.org/2000/svg'><style>.logo { font: italic 2px sans-serif; fill: white; } </style>  <text x='0' y='2' class='logo'>KC</text></svg>";

  constructor (private settingsService: SettingsService) { }

  ngOnInit() {
    this.settingsService.getTitel().subscribe(title => {
      console.log(title);
      if(title !== "")
        this.globalTitle = title;
    })
    console.log(this.logo)
    this.settingsService.getSVGLogo().subscribe(logo => {
      console.log(logo);
      if(logo !== "")
        this.globalLogo = logo;
    })
  }

  ngAfterViewInit() {
    this.logo.nativeElement.innerHTML = this.globalLogo;
  }

}

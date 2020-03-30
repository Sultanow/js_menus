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
  globalLogo: string = "<svg viewBox='5 -10 12 12' xmlns='http://www.w3.org/2000/svg' width='40px' height='20px'><style>.logo { font: italic 13px sans-serif; fill: white; } </style>  <text x='0' y='2' class='logo'>KC</text></svg>";

  menuItems: JSON;

  constructor (private settingsService: SettingsService) { }

  ngOnInit() {
    this.settingsService.getTitel().subscribe(title => {
      console.log(title);
      if(title !== "")
        this.globalTitle = title;
    });
    this.settingsService.getSVGLogo().subscribe(logo => {
      console.log(logo);
      if(logo !== "")
        this.globalLogo = logo;

      if(this.logo.nativeElement)
        this.logo.nativeElement.innerHTML = this.globalLogo;
    });

    this.settingsService.getDummyStatusWarnings().subscribe(data => {
      console.log(data);
      this.menuItems = data;
    })
  }

  ngAfterViewInit() {
    this.logo.nativeElement.innerHTML = this.globalLogo;
  }

  /*
    Statuswarning JSON sieht so aus:
    [
      {
        "name" : "dev1",
        "items" : [{
          "name" : "Item1"},
          {"name" : "Item2"}
        ]
      },
      {
        "name" : "dev2",
        "items": [
          {"name": "Item3"},
          {"name": "Item4"}
        ]
      }
    ]


  */
}

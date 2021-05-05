import { Component } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent {
  title: String = "ALLEGRO-Cockpit";
  
  menuItems: Array<object> = [
    {
      name: "Dashboards",
      route: "dashboard",
    },
    {
      name: "Konfigurationsmanagement",
      route: "configurations",
    },
    {
      name: "Lieferungen",
      route: "Deliveries",
    },
    {
      name: "Batchmanagement",
      route: "batches",
    },
    {
      name: "Legacy",
      route: "legacy",
    },
  ];
  
  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  constructor(private breakpointObserver: BreakpointObserver) {}

}

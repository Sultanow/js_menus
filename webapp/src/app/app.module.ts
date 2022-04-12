import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { NavMenuComponent } from './navmenu/navmenu.component';
import {
  ExternalDashboard,
  ExternalWebsite,
  SafePipe,
} from './external-dashboard.component';

// TODO: move to routes.ts
export const routes: Routes = [
  { path: 'external', component: ExternalDashboard },
  { path: '', redirectTo: 'external', pathMatch: 'full' },
  {
    path: 'dashboard',
    loadChildren: () =>
      import('./dashboard/dashboard.module').then((m) => m.DashboardModule),
  },
];

@NgModule({
  declarations: [
    AppComponent,
    NavMenuComponent,
    ExternalDashboard,
    ExternalWebsite,
    SafePipe,
  ],
  imports: [BrowserModule, RouterModule.forRoot(routes), NgbModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { NavMenuComponent } from './navmenu/navmenu.component';
import {
  ExternalDashboard,
  ExternalWebsite,
  SafePipe,
} from './external-dashboard.component';
import { JsonServerApiService } from './services/json-server.service';

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
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule.forRoot(routes),
    NgbModule,
  ],
  providers: [JsonServerApiService],
  bootstrap: [AppComponent],
})
export class AppModule {}

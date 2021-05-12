import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';

// Middle Layer
import { NorthMenuComponent as M_NorthMenuComponent } from './menus/middle/north-menu/north-menu.component';
import { EastMenuComponent as M_EastMenuComponent } from './menus/middle/east-menu/east-menu.component';
import { SouthMenuComponent as M_SouthMenuComponent } from './menus/middle/south-menu/south-menu.component';
import { WestMenuComponent as M_WestMenuComponent } from './menus/middle/west-menu/west-menu.component';

// Outside Layer
import { NorthMenuComponent as O_NorthMenuComponent } from './menus/outside/north-menu/north-menu.component';
import { EastMenuComponent as O_EastMenuComponent } from './menus/outside/east-menu/east-menu.component';
import { SouthMenuComponent as O_SouthMenuComponent } from './menus/outside/south-menu/south-menu.component';
import { WestMenuComponent as O_WestMenuComponent } from './menus/outside/west-menu/west-menu.component';

// Configuration Service
import { DetailsComponent } from './components/details/details.component';
import { BatchesComponent } from './components/batches/batches.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { DependencyChartsComponent, EditDialog } from './components/dependencycharts/dependencycharts.component';
import { CompareComponent } from './components/compare/compare.component';
import { ViewboxComponent } from './components/viewbox/viewbox.component';
import { WarningsComponent } from './components/warnings/warnings.component';
import { StatusWarningsComponent } from './components/status-warnings/status-warnings.component';
import { ConfigurationViewComponent } from './components/configuration-view/configuration-view.component';
import { RightManagementComponent } from './components/right-management/right-management.component';
import { TrackerComponent } from './components/tracker/tracker.component';
import { TreetableModule } from './components/treetable/treetable.module';
import { SettingsComponent } from './components/settings/settings.component';
import { BasicAuthInterceptorService } from './services/authentication/basic-auth-interceptor.service';
import { AuthenticationComponent } from './components/authentication/authentication.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { MaterialModule } from './material.module';
import { StatisticModule } from './modules/statistic/statistic.module';
import { IconHelperModule } from './modules/icon-helper/icon-helper.module';
import { NewsTickerModule } from './modules/news-ticker/news-ticker.module';
import { IconHelperModule } from './modules/icon-helper/icon-helper.module';
import { AllkeysComponent } from './components/compare/allkeys/allkeys.component';
import { HistorybetweentowdatesComponent } from './components/compare/historybetweentowdates/historybetweentowdates.component';
import { BatchChartsComponent } from './components/batch-charts/batch-charts.component';
import { ChartDependenciesComponent } from './components/chart-dependencies/chart-dependencies.component';
import { ChartNodepsComponent } from './components/chart-nodeps/chart-nodeps.component';
import { SearchComponent } from './components/search/search.component';
import { TableDialogComponent } from './components/table-dialog/table-dialog.component';
import { BatchInfoDialogComponent } from './components/batch-info-dialog/batch-info-dialog.component';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { NavigationComponent } from './components/navigation/navigation.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { Routes, RouterModule } from '@angular/router';
import { MatBadgeModule } from '@angular/material/badge';
import { NewsDisplayComponent } from './components/news-display/news-display.component';
import { MaterialPasswordFieldComponent } from './components/material-password-field/material-password-field.component';

const appRoutes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent,
  },
  {
    path: 'legacy',
    component: AppComponent,
  }
]


@NgModule({
  declarations: [
    AppComponent,
    M_NorthMenuComponent,
    M_EastMenuComponent,
    M_SouthMenuComponent,
    M_WestMenuComponent,
    O_NorthMenuComponent,
    O_EastMenuComponent,
    O_SouthMenuComponent,
    O_WestMenuComponent,
    DetailsComponent,
    BatchesComponent,
    DependencyChartsComponent,
    EditDialog,
    CompareComponent,
    ViewboxComponent,
    WarningsComponent,
    StatusWarningsComponent,
    ConfigurationViewComponent,
    RightManagementComponent,
    TrackerComponent,
    SettingsComponent,
    AuthenticationComponent,
    ChangePasswordComponent,
    SpinnerComponent,
    AllkeysComponent,
    HistorybetweentowdatesComponent,
    BatchChartsComponent,
    ChartDependenciesComponent,
    ChartNodepsComponent,
    SearchComponent,
    TableDialogComponent,
    BatchInfoDialogComponent,
    NavigationComponent,
    DashboardComponent,
    NewsDisplayComponent,
    MaterialPasswordFieldComponent,

  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(appRoutes, {useHash: true}),
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    TreetableModule,
    MaterialModule,
    StatisticModule,
    IconHelperModule,
    NewsTickerModule
    IconHelperModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    MatBadgeModule,
  ],
  providers: [
    DetailsComponent,
    {
      provide : HTTP_INTERCEPTORS,
      useClass: BasicAuthInterceptorService,
      multi:true
    },
  ],
  bootstrap: [ NavigationComponent ],

  entryComponents: [ EditDialog, DependencyChartsComponent, AuthenticationComponent, TableDialogComponent]

})
export class AppModule { }

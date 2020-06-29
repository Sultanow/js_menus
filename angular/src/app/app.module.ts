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
import { NewsComponent } from './components/news/news.component';
import { StatusWarningsComponent } from './components/status-warnings/status-warnings.component';
import { ConfigurationViewComponent } from './components/configuration-view/configuration-view.component';
import { RightManagementComponent } from './components/right-management/right-management.component';
import { TrackerComponent } from './components/tracker/tracker.component';
import { TreetableModule } from './components/treetable/treetable.module';
import { SettingsComponent } from './components/settings/settings.component';
import { SettingsPasswordComponent } from './components/settings-password/settings-password.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { MaterialModule } from './material.module';
import { StatisticModule } from './components/statistic/statistic.module';
import { BasicAuthInterceptorService } from './services/authentiecation/basic-auth-interceptor-.service';

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
    NewsComponent,
    StatusWarningsComponent,
    ConfigurationViewComponent,
    RightManagementComponent,
    TrackerComponent,
    SettingsComponent,
    SettingsPasswordComponent,
    SpinnerComponent,

  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    TreetableModule,
    MaterialModule,
    StatisticModule,
  ],
  providers: [
    DetailsComponent,
    {
      provide : HTTP_INTERCEPTORS,
      useClass: BasicAuthInterceptorService,
      multi:true
    },
    {
      provide: MatDialogRef,
      useValue: {}
    },
  ],
  bootstrap: [ AppComponent ],
  entryComponents: [ EditDialog, DependencyChartsComponent, SettingsPasswordComponent ]

})
export class AppModule { }

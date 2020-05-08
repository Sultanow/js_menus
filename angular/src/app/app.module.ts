import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatMenuModule } from '@angular/material/menu';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';

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
import { ConfigurationService } from './services/configuration.service';
import { DetailsComponent } from './components/details/details.component';
import { BatchesComponent } from './components/batches/batches.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { DependencyChartsComponent, EditDialog } from './components/dependencycharts/dependencycharts.component';
import { CompareComponent } from './components/compare/compare.component';
import { ViewboxComponent } from './components/viewbox/viewbox.component';
import { StatisticComponent } from './components/statistic/statistic.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { WarningsComponent } from './components/warnings/warnings.component';
import { NewsComponent } from './components/news/news.component';
import { StatusWarningsComponent } from './components/status-warnings/status-warnings.component';
import { ConfigurationViewComponent } from './components/configuration-view/configuration-view.component';
import { RightManagementComponent } from './components/right-management/right-management.component';
import { TrackerComponent } from './components/tracker/tracker.component';
import { TreetableModule } from './components/treetable/treetable.module';
import { SettingsComponent } from './components/settings/settings.component';
import { GraphsComponent } from './components/statistic/graphs/graphs.component';
import { MatButtonModule } from '@angular/material/button';
import { CreateChartComponent } from './components/statistic/create-chart/create-chart.component';
import { SettingsPasswordComponent } from './components/settings-password/settings-password.component';
import { OldBrowserBannerComponent } from './components/old-browser-banner/old-browser-banner.component';

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
    StatisticComponent,
    WarningsComponent,
    NewsComponent,
    StatusWarningsComponent,
    ConfigurationViewComponent,
    RightManagementComponent,
    TrackerComponent,
    SettingsComponent,
    GraphsComponent,
    CreateChartComponent,
	SettingsPasswordComponent,
	OldBrowserBannerComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    MatCardModule,
    MatIconModule,
    MatDialogModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
    MatSelectModule,
    MatTableModule,
    MatMenuModule,
    ReactiveFormsModule,
    DragDropModule,
    TreetableModule,
    MatMenuModule,
    MatButtonModule
  ],
  providers: [
    ConfigurationService,
    DetailsComponent
  ],
  bootstrap: [ AppComponent ],
  entryComponents: [ EditDialog, DependencyChartsComponent, SettingsPasswordComponent ]

})
export class AppModule { }

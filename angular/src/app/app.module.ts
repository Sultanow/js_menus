import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatCardModule,
  MatIconModule,
  MatDialogModule,
  MatFormFieldModule,
  MatInputModule,
  MatRadioModule,
  MatSelectModule } from '@angular/material';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';

// Middle Layer
import { NorthMenuComponent as M_NorthMenuComponent } from './menus/middle/north-menu/north-menu.component';
import { EastMenuComponent as M_EastMenuComponent} from './menus/middle/east-menu/east-menu.component';
import { SouthMenuComponent as M_SouthMenuComponent } from './menus/middle/south-menu/south-menu.component';
import { WestMenuComponent as M_WestMenuComponent} from './menus/middle/west-menu/west-menu.component';

// Outside Layer
import { NorthMenuComponent as O_NorthMenuComponent } from './menus/outside/north-menu/north-menu.component';
import { EastMenuComponent as O_EastMenuComponent} from './menus/outside/east-menu/east-menu.component';
import { SouthMenuComponent as O_SouthMenuComponent } from './menus/outside/south-menu/south-menu.component';
import { WestMenuComponent as O_WestMenuComponent} from './menus/outside/west-menu/west-menu.component';

// Configuration Service
import { ConfigurationService } from './services/configuration.service';
import { DetailsComponent } from './details/details.component';
import { BatchesComponent } from './batches/batches.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from './services/in-memory-data/in-memory-data.service';
import { DependencyChartsComponent, EditDialog } from './dependencycharts/dependencycharts.component';
import { CompareComponent } from './compare/compare.component';


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
    CompareComponent
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
    ReactiveFormsModule,

    // The HttpClientInMemoryWebApiModule module intercepts HTTP requests
    // and returns simulated server responses.
    // Remove it when a real server is ready to receive requests.
    HttpClientInMemoryWebApiModule.forRoot(
      InMemoryDataService, { dataEncapsulation: false }
    )
  ],
  providers: [
    ConfigurationService,
    DetailsComponent
  ],
  bootstrap: [AppComponent],
  entryComponents: [EditDialog, DependencyChartsComponent]

})
export class AppModule { }

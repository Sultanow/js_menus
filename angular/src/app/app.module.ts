import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';

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
    O_WestMenuComponent
  ],
  imports: [
    BrowserModule,
    HttpModule
  ],
  providers: [
    ConfigurationService
  ],
  bootstrap: [AppComponent]
  
})
export class AppModule { }

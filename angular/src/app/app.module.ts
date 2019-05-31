import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { NorthMenuComponent } from './menus/outside/north-menu/north-menu.component';
import { EastMenuComponent } from './menus/outside/east-menu/east-menu.component';
import { SouthMenuComponent } from './menus/outside/south-menu/south-menu.component';
import { WestMenuComponent } from './menus/outside/west-menu/west-menu.component';

@NgModule({
  declarations: [
    AppComponent,
    NorthMenuComponent,
    EastMenuComponent,
    SouthMenuComponent,
    WestMenuComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  
})
export class AppModule { }

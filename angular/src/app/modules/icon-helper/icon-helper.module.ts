import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MenuIconComponent } from './menu-icon/menu-icon.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [MenuIconComponent],
  imports: [
    CommonModule,
    BrowserAnimationsModule
  ],
  exports: [
    MenuIconComponent,
  ]
})
export class IconHelperModule { }

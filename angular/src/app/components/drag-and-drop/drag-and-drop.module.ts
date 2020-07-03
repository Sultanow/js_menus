import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DragAndDropComponent } from './drag-and-drop.component';
import { DragAndDropDirective } from './drag-and-drop.directive';
import { MaterialModule } from 'src/app/material.module';



@NgModule({
  declarations: [
    DragAndDropComponent,
    DragAndDropDirective
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    DragAndDropComponent
  ]
})
export class DragAndDropModule { }

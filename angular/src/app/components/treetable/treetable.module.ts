import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { TreetableComponent } from './component/treetable.component';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatRadioModule, MatSelectModule } from '@angular/material';
import { DragDropModule } from '@angular/cdk/drag-drop';
export { Node, Options} from './models';

@NgModule({
  declarations: [
    TreetableComponent
  ],
  imports: [
    BrowserAnimationsModule,
    MatTableModule,
    MatIconModule,
    BrowserModule,
    FormsModule,
    MatCardModule,
    MatIconModule,
    MatDialogModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatInputModule,
    MatRadioModule,
    MatSelectModule,
    MatTableModule,
    ReactiveFormsModule,
    DragDropModule,
  ],
  exports: [
    TreetableComponent
  ]
})
export class TreetableModule { }

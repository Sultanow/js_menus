
import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
    selector: 'dialog-delete-chart',
    templateUrl: 'dialog-delete-chart.html',
    styleUrls: [ './graphs.component.css' ]
  })
  export class DialogDeleteChart {
  
    constructor (
      public dialogRef: MatDialogRef<DialogDeleteChart>) { }
  
    onNoClick(): void {
      this.dialogRef.close();
    }
  
    onYesClick(): void {
      this.dialogRef.close(true);
    }
  }
  
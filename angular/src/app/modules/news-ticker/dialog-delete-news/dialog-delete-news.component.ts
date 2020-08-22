import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-delete-news',
  templateUrl: './dialog-delete-news.component.html',
  styleUrls: [ './dialog-delete-news.component.css' ]
})
export class DialogDeleteNewsComponent {

  constructor (
    public dialogRef: MatDialogRef<DialogDeleteNewsComponent>) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }
}

import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NewsItem } from '../model/news-item';
import { Priorities } from '../model/news-priority';

@Component({
  selector: 'app-dialog-news',
  templateUrl: './dialog-news.component.html',
  styleUrls: ['./dialog-news.component.css']
})
export class DialogNewsComponent implements OnInit {
  priorities = Priorities;
  constructor(
    public dialogRef: MatDialogRef<DialogNewsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: NewsItem) {
    }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}

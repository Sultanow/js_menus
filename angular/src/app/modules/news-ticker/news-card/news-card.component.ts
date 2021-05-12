import { Component, OnInit, Input } from '@angular/core';
import { NewsItem } from '../model/news-item';

@Component({
  selector: 'news-card[newsItem]',
  templateUrl: './news-card.component.html',
  styleUrls: ['./news-card.component.css']
})
export class NewsCardComponent implements OnInit {
  @Input() newsItem: NewsItem;

  constructor() { }

  ngOnInit(): void {
  }

}

import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { NewsService } from '../service/news.service';
import { NewsItem } from '../model/news-item';

@Component({
  selector: 'app-news-overlay',
  templateUrl: './news-overlay.component.html',
  styleUrls: ['./news-overlay.component.css']
})
export class NewsOverlayComponent implements OnInit, OnChanges {
  @Input() isOpen: boolean = false;

  newsItems: NewsItem[];

  constructor(private service: NewsService) { }

  ngOnInit(): void {
    if(this.isOpen) {
      this.loadAllVisibleNews();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(this.isOpen && !changes.isOpen.previousValue) {
      this.loadAllVisibleNews();
    }
  }

  private loadAllVisibleNews() {
    this.service.getAllVisibleNews().subscribe(data => {
      this.newsItems = data.sort((a, b)=> a.id < b.id ? 1 : a.id > b.id ? -1 : 1);;
    });
  }


}

import { Component, OnDestroy, OnInit, Output } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { NewsItem } from '../model/news-item';
import { NewsService } from '../service/news.service';

@Component({
  selector: 'app-ticking-news-card',
  templateUrl: './ticking-news-card.component.html',
  styleUrls: ['./ticking-news-card.component.css']
})
export class TickingNewsCardComponent implements OnInit, OnDestroy {
  private subscription: Subscription;

  @Output()
  newsItems: Array<NewsItem>;

  constructor(private newsService: NewsService) { }

  ngOnInit(): void {
    this.subscription = interval(10000).subscribe(
      () => this.newsService.getAllVisibleNews().subscribe((data) => this.newsItems = data)
    );
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}

import { Component, OnInit } from '@angular/core';
import { timer, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { NewsService } from 'src/app/services/news/news.service';

/**
 * Newer version of a news bar that displays the latest news item and additionally more news
 * when clicked.
 */
@Component({
  selector: 'app-news-display',
  templateUrl: './news-display.component.html',
  styleUrls: ['./news-display.component.css']
})
export class NewsDisplayComponent implements OnInit {
  title: string = "Laden des Titels...";
  content: string = "Laden des Inhalts...";

  private subscription: Subscription;

  constructor(private newsService: NewsService) { }

  // TODO: actually use newsService.

  ngOnInit(): void {
    this.subscription = timer(1, 5000).pipe(
      switchMap(() => this.newsService.getRecentNews())
    ).subscribe(data => {
      this.title = data[0]?.title;
      this.content = data[0]?.content;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}

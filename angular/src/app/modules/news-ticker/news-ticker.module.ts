import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewsOverlayComponent } from './news-overlay/news-overlay.component';
import { MaterialModule} from './../../material.module';
import { HttpClientModule } from '@angular/common/http';
import { NewsComponent } from './news/news.component';
import { FormsModule } from '@angular/forms';
import { DialogNewsComponent } from './dialog-news/dialog-news.component';
import { DialogDeleteNewsComponent } from './dialog-delete-news/dialog-delete-news.component';
import { NewsCardComponent } from './news-card/news-card.component';



@NgModule({
  declarations: [NewsOverlayComponent, NewsComponent, DialogNewsComponent, DialogDeleteNewsComponent, NewsCardComponent],
  imports: [
    CommonModule,
    MaterialModule,
    HttpClientModule,
    FormsModule,
    
  ],
  exports: [
    NewsOverlayComponent,
    NewsComponent
  ]
})
export class NewsTickerModule { }

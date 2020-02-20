import { Component, OnInit, Input } from '@angular/core';
import { MessagesService } from 'src/app/services/messages/messages.service';
import { MessageItem } from 'src/app/model/messageItem';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: [ './news.component.css', '../viewbox/viewbox.component.css' ]
})
export class NewsComponent implements OnInit {

  @Input() showNews: boolean;

  messages: MessageItem[];
  constructor (private messagesService: MessagesService) {

  }

  ngOnInit() {

  }

  ngOnChanges(changes) {
    if (this.showNews) {
      this.messages = this.messagesService.getSuccessMessages();
    }
  }

}

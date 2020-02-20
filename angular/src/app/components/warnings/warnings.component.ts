import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { MessageItem } from 'src/app/model/messageItem';
import { MessagesService } from 'src/app/services/messages/messages.service';

@Component({
  selector: 'app-warnings',
  templateUrl: './warnings.component.html',
  styleUrls: [ './warnings.component.css', '../viewbox/viewbox.component.css' ]
})
export class WarningsComponent implements OnInit {

  @Input() showWarnings: boolean;
  @Output() notifyTitle = new EventEmitter<string>();

  messages: MessageItem[];
  constructor (private messagesService: MessagesService) {

  }

  ngOnInit() {

  }

  ngOnChanges(changes) {
    if (this.showWarnings) {
      this.messages = this.messagesService.getFailedMessages();
    }
  }

}

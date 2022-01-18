import { Injectable } from '@angular/core';
import { MessageItem } from 'src/app/model/messageItem';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {

  constructor () { }


  getSuccessMessages(): MessageItem[] {
    return this.getDummySuccessMessages();
  }

  getDummySuccessMessages(): MessageItem[] {
    let messages: MessageItem[] = [];
    messages.push(this.fillMessageItem("Dev2", "Info", "Neugestartet am 26.01.2020"));
    messages.push(this.fillMessageItem("Batch TL1", "Info", "grün"));
    messages.push(this.fillMessageItem("Dev1", "Info", "Neugestartet am 26.01.2020"));
    messages.push(this.fillMessageItem("Test1", "Info", "Externe Schnittstelle ist verfügbar"));
    messages.push(this.fillMessageItem("Dev1", "Info", "Hilfsapplication deployed"));
    return messages;
  }

  getFailedMessages(): MessageItem[] {
    return this.getDummyFailedMessages();
  }

  getDummyFailedMessages(): MessageItem[] {
    let messages: MessageItem[] = [];
    messages.push(this.fillMessageItem("Dev2", "Error", "Neugestartet fehlgeschlagen"));
    messages.push(this.fillMessageItem("Batch TL4", "Warnung", "Laufzeit > 10 Minuten"));
    messages.push(this.fillMessageItem("Dev5", "Warnung", "Hilfsapplication Deployment fehlgeschlagen"));
    messages.push(this.fillMessageItem("Test4", "Error", "Externe Schnittstelle nicht erreichbar"));
    return messages;
  }

  fillMessageItem(env: string, type: string, info: string): MessageItem {
    return new MessageItem(env, type, info);
  }
}

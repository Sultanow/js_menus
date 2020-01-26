export class MessageItem {
    env: string;
    type: string;
    info: string;

    constructor(e: string, t: string, i: string) {
      this.env = e;
      this.type = t;
      this.info = i;
    }
  }
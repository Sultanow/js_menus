export class Batch {
  batchid: string;
  duration?: number;
  fk_doku?: string;
  periodizitaet?: string;
  ersterlauf?: string;
  letzterlauf?: string;
  
  constructor(id: string) {
    this.batchid = id;
  }
}

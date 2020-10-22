export class Batch {
  id?: string;
  name: string;
  duration?: number;
  fk_doku?: string;
  periodizitaet?: string;
  ersterlauf?: string;
  letzterlauf?: string;
  
  constructor(name: string) {
    this.name = name;
  }
}

export class Batches {
  id: string;
  date: string | Date;
  values?: BatchItem[];
  durationBatch1: number;
  durationBatch2: number;
}

export class BatchItem {
  date: string | Date;
  duration: number;
}

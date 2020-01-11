import { Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

import { Batch } from '../../model/batch';
import { BatchService } from '../../services/batches/batches.service';

@Component({
  selector: 'app-batches',
  templateUrl: './batches.component.html',
  styleUrls: ['./batches.component.css']
})
export class BatchesComponent implements OnInit {
  batches: Batch[];
  @Input() showDetails: boolean;
  @Output() notifyDetailsClose = new EventEmitter<boolean>();
  @Input() editOn: boolean;
  selectedBatch: Batch;

  constructor(private batchService: BatchService) { }

  ngOnInit() {
    this.getBatches();
    this.editOn = false;
  }

  toggleEditOn(batch: Batch) {
    this.editOn = true;
    this.selectedBatch = batch;
  }

  toggleEditOff() {
    this.getBatches();
    this.editOn = false;
  }

  getBatches(): void {
    this.batchService.getBatches()
    .subscribe(batches => this.batches = batches);
  }

  add(id: string): void {
    id = id.trim();
    if (!id) { return; }
    this.batchService.addBatch({ id } as Batch)
      .subscribe(batch => {
        this.batches.push(batch);
      });
  }

  delete(batch: Batch): void {
    this.batches = this.batches.filter(b => b !== batch);
    this.batchService.deleteBatch(batch).subscribe();
  }

  close() {
    this.showDetails = false;
    this.notifyDetailsClose.emit(true);
  }

  save(): void {
    this.batchService.updateBatch(this.selectedBatch).subscribe();
    this.toggleEditOff();
  }

}

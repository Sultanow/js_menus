import { Component, OnInit, Input } from '@angular/core';
import { Batch } from '../../model/batch';
import { BatchService } from '../../services/batches/batches.service';

@Component({
  selector: 'app-batches',
  templateUrl: './batches.component.html',
  styleUrls: [ './batches.component.css' ]
})
export class BatchesComponent implements OnInit {
  batches: Batch[];
  @Input() showBatches: boolean;
  @Input() editOn: boolean;
  selectedBatch: Batch;
  @Input() editName: boolean;
  @Input() editDuration: boolean;
  @Input() editLetzterlauf: boolean;
  @Input() editErsterlauf: boolean;
  @Input() editFk_doku: boolean;
  @Input() editPeriodizitaet: boolean;

  constructor (private batchService: BatchService) { }

  ngOnInit() {
    this.getBatches();
    this.editOn = false;
    this.editName = false;
    this.editDuration = false;
    this.editLetzterlauf= false;
    this.editErsterlauf=false;
    this.editFk_doku=false;
    this.editPeriodizitaet=false;
    //console.log(`Init Batches: ${this.batches}`);
  }

  toggleEditOn(batch: Batch, editColumn: string) {
    this.editOn = true;
    this.selectedBatch = batch;
    this.isColumnEdit(editColumn);
  }

  toggleEditOff() {
    this.getBatches();
    this.editOn = false;
    this.editName = false;
    this.editDuration = false;
    this.editLetzterlauf= false;
    this.editErsterlauf=false;
    this.editFk_doku=false;
    this.editPeriodizitaet=false;
  }

  isColumnEdit(editColumn:string){
    if (editColumn=='name'){
        this.editName = true;
    }
    if (editColumn=='duration'){
      this.editDuration = true;
    }
    if (editColumn=='fk_doku'){
      this.editFk_doku = true;
    }
    if (editColumn=='periodizitaet'){
      this.editPeriodizitaet = true;
    }
    if (editColumn=='ersterlauf'){
      this.editErsterlauf = true;
    }
    if (editColumn=='letzterlauf'){
      this.editLetzterlauf = true;
    }
  }

  getBatches(): void {
    this.batchService.getBatches()
      .subscribe(batches => {console.log("batches", batches); 
      this.batches = batches
      this.testFunction();
    });
  }

  testFunction(): void {
    console.log("batches after get batches", this.batches);
    this.batches = [...this.batches];
    } 
 
  add(name: string): void {
    name = name.trim();
    if (!name) { return; }
    this.batchService.addBatch({ name } as Batch)
      .subscribe(batch => {
        this.batches.push(batch);
      });
  }

  delete(batch: Batch): void {
    this.batches = this.batches.filter(b => b !== batch);
    this.batchService.deleteBatch(batch).subscribe();
  }

  save(): void {
    this.batchService.updateBatch(this.selectedBatch).subscribe();
    this.toggleEditOff();
  }

}

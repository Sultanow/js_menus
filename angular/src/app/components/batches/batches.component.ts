import { Component, OnInit, Input, ViewEncapsulation } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { BatchService } from '../../services/batches/batches.service';
import { TableDialogComponent } from '../table-dialog/table-dialog.component';
import * as _ from "lodash";
import { MatFormField } from '@angular/material/form-field';
@Component({
  selector: 'app-batches',
  templateUrl: './batches.component.html',
  styleUrls: [ './batches.component.css' ],
  encapsulation: ViewEncapsulation.None 
})
export class BatchesComponent implements OnInit {
  batches: any[];
  @Input() showBatches: boolean;
   // used for updating the outline due to a bug: https://github.com/angular/components/issues/15027
  @Input() outlineRef: MatFormField; 
  selectedBatch: any;

  tableStructure = [];

  constructor (private batchService: BatchService, private dialog: MatDialog) { }

  ngOnInit() {
    this.getBatches();
    this.batchService.getTableStructure().subscribe(struct => {
      this.tableStructure = JSON.parse(struct);
    })
    console.log(`Init Batches: ${this.batches}`);
  }

  
  openDialog(batch, tableElem) {
    this.selectedBatch = batch;
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {
      tableElem: tableElem,
      batchData: batch
    }
    const dialogRef = this.dialog.open(TableDialogComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(
        updatedBatchData => {
          console.log("Dialog output:", updatedBatchData);

          if(updatedBatchData) {
            // this means the user clicked "speichern" and not "schließen"
            // this.selectedBatch[tableElem.Spaltenname] = updatedBatchData;
            let temp = _.cloneDeep(this.selectedBatch);
            temp[tableElem.Spaltenname] = updatedBatchData;
            this.save(temp);
            console.log("selectedbatch", this.selectedBatch);
            
          }
        }
    );    
}

  getBatchInfoPreview(batch:any, col) {
    let displayedField =this.tableStructure[col]["Felder"][0];
    let objectHierarchy = this.tableStructure[col]["Spaltenname"] + "." + displayedField;
    return (_.get(batch, objectHierarchy, "...")).slice(0, 20) // max length = 20
  }

  getBatches(): void {
    this.batchService.getBatches()
      .subscribe(batches => {console.log("batches", batches); 
      this.batches = batches
    });
  }
 
  add(name: string): void {
    name = name.trim();
    if (!name) { return; }
    console.log("adding batch " + name);
    let newBatch = new Object();
    let nameField =this.tableStructure[0]["Felder"][0];
    let objectHierarchy = this.tableStructure[0]["Spaltenname"] + "." + nameField;
    _.set(newBatch, objectHierarchy, name);
    _.set(newBatch, "id", name);
    this.batchService.addBatch(newBatch)
      .subscribe(resp => {
        // if user is unauthorized, resp will be undefined
        if (resp) this.batches.push(newBatch);
      });
  }

  delete(batch: any): void {
    this.batchService.deleteBatch(batch).subscribe(resp => { 
      if(resp) { // if user is unauthorized, resp will be undefined
        this.batches = this.batches.filter(b => b !== batch);
      }
    }); // we need to subscribe, otherwise this won't happen
  }

  save(updatedBatch): void {
    this.batchService.updateBatch(updatedBatch).subscribe(result => {
      if(result && result.id) {
        let ind = this.batches.findIndex(b => b.id == result.id);
        if (ind > -1) {
          this.batches[ind] = result;
        }
      }
    },error => {console.log(error);
    });
  }

}

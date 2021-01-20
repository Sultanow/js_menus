import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as _ from "lodash";
@Component({
  selector: 'app-batch-info-dialog',
  templateUrl: './batch-info-dialog.component.html',
  styleUrls: ['./batch-info-dialog.component.css']
})
export class BatchInfoDialogComponent implements OnInit {
  batch: any;
  tableStructure: any[];

  batchInfo: BatchInfoPresentation[] = [] as BatchInfoPresentation[];
  constructor(
      @Inject(MAT_DIALOG_DATA) data) {
      this.batch = data.batchData;
      this.tableStructure = data.tableStructure;
  }

  ngOnInit() {
    this.tableStructure.forEach(col => {
      let colName: string = col.Spaltenname;
      let fields: Field[] = [] as Field[];
      col.Felder.forEach(field => {
          fields.push({name: field, value:_.get(this.batch, colName+"."+field)} as Field);
      });
      this.batchInfo.push({colName: colName, fields: fields} as BatchInfoPresentation)
    });
  }
}

interface BatchInfoPresentation {
  colName: string;
  fields: Field[];
}
interface Field {
  name:string;
  value:string
}

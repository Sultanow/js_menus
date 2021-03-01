import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as _ from "lodash";
@Component({
  selector: 'app-table-dialog',
  templateUrl: './table-dialog.component.html',
  styleUrls: ['./table-dialog.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TableDialogComponent implements OnInit {


  form: FormGroup;
  description: string;
  batchName: string;
  fields: string[];
  colName: string;
  currentBatch: any;

  constructor(
    private dialogRef: MatDialogRef<TableDialogComponent>,
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) data) {

    //this.description = data.batchData.Batch.Name + ": " + data.tableElem.Spaltenname;
    this.batchName = data.batchData.Batch.Name;
    this.colName = data.tableElem.Spaltenname
    this.fields = data.tableElem.Felder;
    this.currentBatch = data.batchData;
  }

  ngOnInit() {
    this.form = this.fb.group(this.getGroup());
  }

  save() {
    this.dialogRef.close(this.form.value);
  }

  close() {
    this.dialogRef.close();
  }

  getGroup() {
    let group = {};
    this.fields.forEach(f => {
      let val = _.get(_.get(this.currentBatch, this.colName, ""), f, "");
      _.set(group, f, [val, []]);
    })
    return group;
  }
}

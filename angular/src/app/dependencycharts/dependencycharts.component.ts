import { Component, OnInit, Input, Output, EventEmitter, Inject } from '@angular/core';
import { DependencyChart } from '../model/dependencychart';
import { DependencyService } from '../services/dependencies/dependencies.service';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'app-dependencycharts',
  templateUrl: './dependencycharts.component.html',
  styleUrls: ['./dependencycharts.component.css']
})
export class DependencyChartsComponent implements OnInit {

  id: string;
  title: string;
  chartType: string;
  description: string;  // e.g. ""
  chartContent: string; // Dependency Chart in JSON

  @Input() showDependencyCharts: boolean;
  @Output() notifyDependencyChartsClose = new EventEmitter<boolean>();
  isEditing: boolean;
  dependencyCharts: DependencyChart[];
  selectedDependencyChart: DependencyChart;

  ngOnInit() {
    this.getDependencyCharts();
  }

  constructor(
    private dependenciesService: DependencyService,
    public dialog: MatDialog) {}

  getDependencyCharts(): void {
    this.dependenciesService.getDependencies()
    .subscribe(dependencies => this.dependencyCharts = dependencies);
  }

  close() {
    this.showDependencyCharts = false;
    this.notifyDependencyChartsClose.emit(true);
  }

  select(dependencyChart: DependencyChart) {
    this.isEditing = false;
    this.selectedDependencyChart = dependencyChart;
  }

  deselect() {
    this.isEditing = false;
  }

  edit(dependencyChart: DependencyChart) {
    this.isEditing = true;
  }

  addNew(dependencyChart: DependencyChart) {
  }

  cancelEditing() {
    this.isEditing = false;
  }

  delete(dependencyChart: DependencyChart) {
    this.isEditing = false;
  }

  save() {
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(DialogOverviewDialog, {
      width: '500px',
      data: {id: this.id,
        title: this.title,
        chartType: this.chartType,
        description: this.description,
        chartContent: this.chartContent}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }
}


@Component({
  selector: 'dialog-overview-dialog',
  templateUrl: 'dialog-overview-dialog.html',
})
export class DialogOverviewDialog {

  constructor(
    public dialogRef: MatDialogRef<DialogOverviewDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DependencyChart) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

}

import { Component, OnInit, Input, Inject } from '@angular/core';
import { DependencyChart } from '../../model/dependencychart';
import { DependencyService } from '../../services/dependencies/dependencies.service';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogConfig} from '@angular/material/dialog';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

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

  @Input() showDependency: boolean;
  dependencyCharts: DependencyChart[];
  selectedDependencyChart: DependencyChart;
  editedDependencyChart: DependencyChart;

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

  select(dependencyChart: DependencyChart) {
    this.selectedDependencyChart = dependencyChart;
  }

  addNew(dependencyChart: DependencyChart) {
    this.dependenciesService.addDependency(dependencyChart)
    .subscribe( chart => {
      this.dependencyCharts.push(chart);
    });
  }

  delete(dependencyChart: DependencyChart) {
    this.dependencyCharts = this.dependencyCharts.filter(dc => dc !== dependencyChart);
    this.dependenciesService.deleteDependency(dependencyChart);
    this.selectedDependencyChart = null;
  }

  openEditDialog(): void {
    this.editedDependencyChart = this.selectedDependencyChart;
    const dialogRef = this.dialog.open(EditDialog, {
      width: '500px',
      data: {id: this.id,
        title: this.title,
        chartType: this.chartType,
        description: this.description,
        chartContent: this.chartContent}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed:', result);
    });
  }

  openCreateDialog(): void {
    const dialogRef = this.dialog.open(EditDialog, {
      width: '500px',
      data: {id: this.id,
        title: this.title,
        chartType: this.chartType,
        description: this.description,
        chartContent: this.chartContent}
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed:', result);
      if (result) {
        this.addNew(result);
      }
    });
  }
}


@Component({
  selector: 'edit-dialog',
  templateUrl: 'edit-dialog.html',
})
export class EditDialog {

  form: FormGroup;
  
  constructor(
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<EditDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DependencyChart) {
  }

  ngOnInit() {
    this.form = this.formBuilder.group({
      id: ['', Validators.required],
      title: ['', Validators.required],
      chartType: ['', Validators.required],
      description: ['', Validators.required],
      chartContent: ['', Validators.required]
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}

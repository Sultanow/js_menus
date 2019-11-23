import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { DependencyChart } from '../model/dependencychart';
import { DependencyService } from '../services/dependencies/dependencies.service';

@Component({
  selector: 'app-dependencycharts',
  templateUrl: './dependencycharts.component.html',
  styleUrls: ['./dependencycharts.component.css']
})
export class DependencyChartsComponent implements OnInit {

  @Input() showDependencyCharts: boolean;
  @Output() notifyDependencyChartsClose = new EventEmitter<boolean>();
  isEditing: boolean;
  dependencyCharts: DependencyChart[];

  ngOnInit() {
    this.getDependencyCharts();
  }

  constructor(private dependenciesService: DependencyService) {}

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

}

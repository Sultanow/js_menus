import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-dependencies',
  templateUrl: './dependencies.component.html',
  styleUrls: ['./dependencies.component.css']
})
export class DependenciesComponent implements OnInit {

  @Input() showDependencies: boolean;
  @Output() notifyDependenciesClose = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit() {
  }

  close() {
    this.showDependencies = false;
    this.notifyDependenciesClose.emit(true);
  }

}

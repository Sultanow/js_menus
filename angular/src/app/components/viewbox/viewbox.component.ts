import { Component, OnInit, Output, Input, EventEmitter, SimpleChanges,  ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-viewbox',
  templateUrl: './viewbox.component.html',
  styleUrls: [ './viewbox.component.css' ]
})
export class ViewboxComponent implements OnInit {

  constructor (private ref: ChangeDetectorRef) { }

  ngOnInit() {
  }

  @Input() showViewBox: boolean;
  @Input() showView: string;
  // Notify parent (app) when details box should close
  @Output() notifyViewBoxClose = new EventEmitter<boolean>();

  showCompare: boolean = false;
  showStatistic: boolean = false;
  showBatches: boolean = false;
  showDependency: boolean = false;
  showNews: boolean = false;
  showWarnings: boolean = false;
  showConfigView: boolean = false;
  showRightManagement: boolean = false;
  showTracker: boolean = false;
  showSettings: boolean = false;
  title: string = "";

  close() {
    this.showViewBox = false;
    this.notifyViewBoxClose.emit(true);
    this.closeAllViews();

  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
    if (changes.showView) {
      this.openShowView(changes.showView.currentValue);
    }
  }

  viewType: string;
  openShowView(view: string) {
    this.viewType = view;
    this.closeAllViews();
    if (view === "compare") {
      this.showCompare = true;
    } else if (view === "statistic") {
      this.showStatistic = true;
    } else if (view === "batches") {
      this.showBatches = true;
    } else if (view === "dependency") {
      this.showDependency = true;
    } else if (view === "news") {
      this.showNews = true;
    } else if (view === "warning") {
      this.showWarnings = true;
    } else if (view === "serverconfig") {
      this.showConfigView = true;
    } else if (view === "batchconfig") {
      this.showConfigView = true;
    } else if (view === "test" || view === "prod" || view === "dev") {
      this.showRightManagement = true;
    } else if (view === "jira" || view === "bitbucket" || view === "confluence") {
      this.showTracker = true;
    } else if (view === "settings") {
      this.showSettings = true;
    }
  }

  closeAllViews() {
    this.showCompare = false;
    this.showStatistic = false;
    this.showBatches = false;
    this.showDependency = false;
    this.showNews = false;
    this.showWarnings = false;
    this.showConfigView = false;
    this.showRightManagement = false;
    this.showTracker = false;
    this.showSettings = false;
    this.title = "";
  }

  onNotifyTitle(title: string) {
    this.title = title;
    this.ref.detectChanges();
  }
}

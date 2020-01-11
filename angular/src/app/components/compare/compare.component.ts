import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CompareItem } from '../../model/compareItem';
import { CompareItemsService } from '../../services/compare/compare-items.service';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.css']
})
export class CompareComponent implements OnInit {

  constructor(private compareService: CompareItemsService) { }

  ngOnInit() {
  }
  dummyCompare : CompareItem[] = [];

  @Input() showCompare: boolean;
  // Notify parent (app) when details box should close
  @Output() notifyComparesClose = new EventEmitter<boolean>();



  ngOnChanges(changes) {
     if(this.showCompare){
       this.getConfigData();
     }
  }

  getConfigData(){
    this.dummyCompare = this.compareService.getDummyData();

  }

  close(){
    this.showCompare = false;
    this.notifyComparesClose.emit(true);
  }
}

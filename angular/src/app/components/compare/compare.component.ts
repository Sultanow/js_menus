import { Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import { CompareItem } from '../../model/compareItem';
import { CompareItemsService } from '../../services/compare/compare-items.service';
import { Node, Options } from '../treetable/treetable.module' 
import { ENVCONFIG } from 'src/app/model/evntreetable';
import { ConfigurationItemsService } from 'src/app/services/configuration/configuration-items.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.css']
})
export class CompareComponent implements OnInit {
  nodesSubscription : Subscription;
  constructor(private compareService: CompareItemsService, private configItemService: ConfigurationItemsService) { 
    this.nodesSubscription = this.configItemService.treeNodes.subscribe(e => {
      if(e) {
        this.nodesTree = e;
      }
    })
  }

  ngOnInit() {
  }
  dummyCompare : CompareItem[] = [];

  @Input() showCompare: boolean;
  // Notify parent (app) when details box should close
  @Output() notifyCompareClose = new EventEmitter<boolean>();

  notOk: string = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="none" d="M0 0h24v24H0V0z"/><path d="M18.3 5.71c-.39-.39-1.02-.39-1.41 0L12 10.59 7.11 5.7c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41L10.59 12 5.7 16.89c-.39.39-.39 1.02 0 1.41.39.39 1.02.39 1.41 0L12 13.41l4.89 4.89c.39.39 1.02.39 1.41 0 .39-.39.39-1.02 0-1.41L13.41 12l4.89-4.89c.38-.38.38-1.02 0-1.4z"/></svg>'


  ngOnChanges(changes) {
     if(this.showCompare){
       this.getConfigData();
       this.configItemService.createDummyDataTable();
       this.configItemService.generateTree(["dev1", "dev2"]);
     }
  }

  getConfigData(){

    this.dummyCompare = this.compareService.getDummyData();
    this.dummyCompare.forEach(element => {
      element.values.forEach(val => {
        val.checked = (val.value === element.ref)
      })
    });

  }

  nodesTree: Node<ENVCONFIG>[];

  treeOptions: Options<ENVCONFIG> = {
    capitalisedHeader: true,
  };

  logNode(node: Node<ENVCONFIG>) {
    console.log(node);
  }
}



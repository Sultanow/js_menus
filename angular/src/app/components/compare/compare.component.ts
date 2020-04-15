import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CompareItem } from '../../model/compareItem';
import { CompareItemsService } from '../../services/compare/compare-items.service';
import { Node, Options } from '../treetable/treetable.module';
import { ENVCONFIG } from 'src/app/model/evntreetable';
import { ConfigurationItemsService } from 'src/app/services/configuration/configuration-items.service';
import { ServerConfiguration } from 'src/config/ServerConfiguration';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: [ './compare.component.css' ]
})
export class CompareComponent implements OnInit {
  nodesSubscription: Subscription;
  constructor (private compareService: CompareItemsService, private configItemService: ConfigurationItemsService, private ref: ChangeDetectorRef) {
    this.nodesSubscription = this.configItemService.treeNodes.subscribe(e => {
      if (e) {
        this.nodesTree = e;
      }
    });
  }

  ngOnInit() {
  }
  dummyCompare: CompareItem[] = [];

  selectedValue: string = "";
  updateTime: Date = new Date();


  @Input() showCompare: boolean;
  @Output() notifyCompareClose = new EventEmitter<boolean>();
  @Output() notifyTitle = new EventEmitter<string>();
  notOk: string = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="none" d="M0 0h24v24H0V0z"/><path d="M18.3 5.71c-.39-.39-1.02-.39-1.41 0L12 10.59 7.11 5.7c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41L10.59 12 5.7 16.89c-.39.39-.39 1.02 0 1.41.39.39 1.02.39 1.41 0L12 13.41l4.89 4.89c.39.39 1.02.39 1.41 0 .39-.39.39-1.02 0-1.41L13.41 12l4.89-4.89c.38-.38.38-1.02 0-1.4z"/></svg>';

  envs: string[] = [ "dev1", "dev2" ];
  allHosts: string[] = [];
  selectedEnvs: string[] = null;
  otherEnvs: string[] = [];

  nodesTree: Node<ENVCONFIG>[];

  treeOptions: Options<ENVCONFIG> = {
    capitalisedHeader: true,
  };

  ngOnChanges(changes) {
    if (this.showCompare) {
      this.notifyTitle.emit("Konfigurationsübersicht");
      this.getServerConfiguration();
      this.configItemService.getHostlist().subscribe(data => {
        this.allHosts = [];
        data.forEach(item => {
          this.allHosts.push(item.host);
        });
        this.generateShowlists();
        this.ref.detectChanges();
      });

      this.configItemService.createDummyDataTable();
      this.generateShowlists();
      this.ref.detectChanges();
    }
  }
  generateShowlists() {
    if (this.selectedEnvs === null)
      this.selectedEnvs = [ "dev1", "dev2" ];
    this.otherEnvs = [];
    this.allHosts.forEach(e => {
      if (!this.selectedEnvs.includes(e))
        this.otherEnvs.push(e);
    });
  }

  getConfigData() {

    this.dummyCompare = this.compareService.getDummyData();
    this.dummyCompare.forEach(element => {
      element.values.forEach(val => {
        val.checked = (val.value === element.ref);
      });
    });

  }

  logNode(node: Node<ENVCONFIG>) {
    console.log(node);
  }

  onAddEnv(event) {
    if (!this.selectedEnvs.includes(this.selectedValue))
      this.selectedEnvs.push(this.selectedValue);
    this.generateShowlists();
    this.getServerConfiguration();

  }
  getServerConfiguration() {
    let envs = ServerConfiguration.ENV_LIST;
    if(this.selectedEnvs)
      envs = this.selectedEnvs;
    this.configItemService.getServerConfiguration(envs).subscribe(data => {
      let updateTime = this.configItemService.getUpdateTime(data);
      this.updateTime.setTime(updateTime*1000);
      this.configItemService.itemlist = this.configItemService.createServerConf(data);
      this.configItemService.generateTree(this.selectedEnvs);
    });
  }

  setSelectedValue(event) {
    this.selectedValue = event.value;
  }

  onDeleteEnv(event, env) {
    this.selectedEnvs = this.selectedEnvs.filter(e => e !== env);
    this.generateShowlists();
    this.ref.detectChanges();
    this.configItemService.generateTree(this.selectedEnvs);

  }
}



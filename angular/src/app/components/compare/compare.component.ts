import { Component, OnInit, Input, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { Node, Options } from '../treetable/treetable.module';
import { ENVCONFIG } from 'src/app/model/evntreetable';
import { ConfigurationItemsService } from 'src/app/services/configuration/configuration-items.service';
import { ServerConfiguration } from 'src/config/ServerConfiguration';
import { Subscription } from 'rxjs';
import { ElasticService } from 'src/app/services/elasticsearch/elastic.service';
import { ConfigurationItem } from 'src/app/model/configurationItem';
import { MatDialog } from '@angular/material/dialog';
import { AllkeysComponent } from './allkeys/allkeys.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HistorybetweentowdatesComponent } from './historybetweentowdates/historybetweentowdates.component';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.css']
})
export class CompareComponent implements OnInit {
  historyForm: FormGroup;
  nodesSubscription: Subscription;
  constructor( private configItemService: ConfigurationItemsService, private ref: ChangeDetectorRef,
    private elasticService: ElasticService,
    private dialog: MatDialog, private formBuilder: FormBuilder,
  ) {
    this.nodesSubscription = this.configItemService.treeNodes.subscribe(e => {
      if (e) {
        this.nodesTree = e;
      }
    });
    this.historyForm = this.formBuilder.group({
      firstDate: ['', [Validators.required]],
      secondDate: ['', [Validators.required]]
    })
  }
  selectedValue: string = "";
  lastUpdate: Date = new Date();

  @Input() showCompare: boolean;
  @Output() notifyCompareClose = new EventEmitter<boolean>();
  @Output() notifyTitle = new EventEmitter<string>();
  notOk: string = '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path fill="none" d="M0 0h24v24H0V0z"/><path d="M18.3 5.71c-.39-.39-1.02-.39-1.41 0L12 10.59 7.11 5.7c-.39-.39-1.02-.39-1.41 0-.39.39-.39 1.02 0 1.41L10.59 12 5.7 16.89c-.39.39-.39 1.02 0 1.41.39.39 1.02.39 1.41 0L12 13.41l4.89 4.89c.39.39 1.02.39 1.41 0 .39-.39.39-1.02 0-1.41L13.41 12l4.89-4.89c.38-.38.38-1.02 0-1.4z"/></svg>';

  envs: string[] = ["dev1", "dev2"];
  allHosts: string[] = [];
  selectedEnvs: string[] = null;
  otherEnvs: string[] = [];

  nodesTree: Node<ENVCONFIG>[];
  indexNames: string[] = [];
  historyRecords: string[] = [];
  selected: string;

  treeOptions: Options<ENVCONFIG> = {
    capitalisedHeader: true,
  };

  ngOnInit() {
    this.configItemService.createServerConf();
  }

  refreshItemlist(): void {
    this.configItemService.currentItemlist.subscribe(itemlist => {
      this.configItemService.itemlist = itemlist;
    });
  }

  ngOnChanges(changes): void {
    if (this.showCompare) {
      this.getIndexNames();
      this.notifyTitle.emit("Konfigurationsübersicht");
      this.getServerConfiguration();
      this.elasticService.getAllHostName().subscribe(data => {
        this.allHosts = data;
        this.generateCompareTable();
        this.ref.detectChanges();
      });

      this.generateCompareTable();
      this.ref.detectChanges();
    }
  }

  generateCompareTable(): void{
    if (this.selectedEnvs === null)
      this.selectedEnvs = ["dev1", "dev2"];
    this.otherEnvs = [];
    this.allHosts.forEach(e => {
      if (!this.selectedEnvs.includes(e))
        this.otherEnvs.push(e);
    });
  }

  logNode(node: Node<ENVCONFIG>): void {
    console.log(node);
  }

  onAddEnv(event): void {
    if (!this.selectedEnvs.includes(this.selectedValue) && (this.selectedValue != "")) {
      this.selectedEnvs.push(this.selectedValue);
    }
    this.generateCompareTable();
    this.getServerConfiguration();
  }
  
  getServerConfiguration(): void {
    let envs = ServerConfiguration.ENV_LIST;
    if (this.selectedEnvs)
      envs = this.selectedEnvs;
    this.elasticService.getHostinformationByNames(envs).subscribe(data => {
      let updateTime = this.configItemService.getUpdateTime(data);
      let temp = new Date();
      temp.setTime(updateTime * 1000);
      this.lastUpdate = temp;
      this.configItemService.createServerConf();
      this.generateTree();

    });
  }

  setSelectedValue(event): void {
    this.selectedValue = event.value;
  }

  onDeleteEnv(event, env): void {
    this.selectedEnvs = this.selectedEnvs.filter(e => e !== env);
    this.generateCompareTable();
    this.ref.detectChanges();
    this.configItemService.generateTree(this.selectedEnvs);

  }



  saveExpectedValue(hostName: string, key: string, actualValue: string, expectedValue: string): void {
    if (this.selectedEnvs) {
      this.elasticService.saveExpectedValue(hostName, key, expectedValue).subscribe(data => {
        let confItem: ConfigurationItem = this.configItemService.createItem(hostName, key, actualValue, expectedValue)

        this.configItemService.addConfigItem(confItem);
        this.refreshItemlist();
        this.generateTree();

      });
    }
    this.elasticService.saveHistoryExpectedValue(hostName, key, expectedValue).subscribe(data => {
    })
  }


  generateTree(): void {
    setTimeout(() => this.configItemService.generateTree(this.selectedEnvs), 2000);
  }

  displayAllkeys(): void {
    this.dialog.open(AllkeysComponent, {
      disableClose: false,
      width: "50%"
    });
  }

  getIndexNames(): void {
    this.elasticService.getHistoryindexnames().subscribe(data => {
      this.indexNames = data;
    })
  }

  sendDates(date1: Date, date2: Date): void {
    let unixtimestamp1 = (((new Date(date1)).getTime() / 1000)).toString();
    let unixtimestamp2 = (((new Date(date2)).getTime() / 1000)).toString();
    let selectedIndex: string = this.selected;
    this.elasticService.getHistoryBetweenTwoDates(unixtimestamp1, unixtimestamp2, selectedIndex).subscribe(data => {
      this.configItemService.historyRecords = data;
    });
  }

  displayHistoryBetweenTwoDatesDelay(): void {
    setTimeout(() => this.displayHistorybetweenTwoDates(), 2000); // 2000 is millisecond
  }

  displayHistorybetweenTwoDates(): void {
    this.dialog.open(HistorybetweentowdatesComponent, {
      disableClose: false,
      data: { records: this.configItemService.historyRecords }
    });
  }
}

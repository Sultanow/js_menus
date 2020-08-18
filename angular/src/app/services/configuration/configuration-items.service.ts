import { ConfigurationItem } from 'src/app/model/configurationItem';
import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ENVCONFIG, ENVVAL } from 'src/app/model/evntreetable';
import { Node } from 'src/app/components/treetable/treetable.module';
import { SettingsService } from '../settings/settings.service';
import { ElasticService } from '../elasticsearch/elastic.service';
@Injectable({
  providedIn: 'root'
})
export class ConfigurationItemsService implements OnDestroy {


  ngOnDestroy(): void {
  }


  treeNodes: BehaviorSubject<Node<ENVCONFIG>[]> = new BehaviorSubject<Node<ENVCONFIG>[]>([]);

  private _historyRecords;
  public get historyRecords() {
    return this._historyRecords;
  }

  public set historyRecords(value) {
    this._historyRecords = value;
  }

  private _itemlist: BehaviorSubject<ConfigurationItem[]> = new BehaviorSubject<ConfigurationItem[]>([]);
  currentItemlist = this._itemlist.asObservable();
  changeItemlist(itemlist: ConfigurationItem[]) {
    this._itemlist.next(itemlist);
  }
  public get itemlist(): ConfigurationItem[] {
    return this._itemlist.value;
  }
  public set itemlist(value: ConfigurationItem[]) {
    value.forEach(elem => {
      this.addConfigItem(elem);
    });
  }

  constructor(private settingsService: SettingsService, private elasticService: ElasticService) { }

  addConfiguration(list: ConfigurationItem[]): void {
    this.itemlist = list;
  }

  addConfigItem(elem: ConfigurationItem): void {
    if (elem.value === "")
      return;
    let update: boolean = false;
    this._itemlist.value.forEach(item => {
      if (item.key === elem.key && item.env === elem.env) {
        item.value = elem.value;
        item.soll = elem.soll;
        update = true;
        return;
      }
    });
    if (update === false)
      this._itemlist.value.push(elem);
  }

  createServerConf(): void{
    this.elasticService.getHostInformation().subscribe(data => {
      data.forEach(elem => {
        let host = elem.host;
        elem.items.forEach(item => {
          this.elasticService.getSollValue(host, item.key_).subscribe(sollValue => {
            this.addConfigItem(this.createItem(host, item.key_, item.lastvalue, sollValue))
          });
        });
      });
    });
  }

  getUpdateTime(data: any): number {
    let time: number = 0;
    data.forEach(elem => {
      elem.items.forEach(item => {
        if (item.lastclock !== '' && item.lastclock > time)
          time = item.lastclock;
      });
    });
    return time;
  }


  getDummyServerConfiguration(): ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("Dev1", "SW-Version", "20.02.00_5", ""));
    items.push(this.createItem("Dev1", "Silbentrennung", "an", ""));
    items.push(this.createItem("Dev1", "Text-Version", "V20.02.00_2", ""));
    items.push(this.createItem("Dev1", "Hilfsapplication-Version", "2.03.1", ""));
    items.push(this.createItem("Dev4", "SW-Version", "19.02.00_52", ""));
    return items;
  }

  getBatchConfiguration(): ConfigurationItem[] {
    return this.getDummyBatchConfig();
  }

  getDummyBatchConfig(): ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("A2145", "Parameter 1", "5", ""));
    items.push(this.createItem("A2145", "Parameter 2", "x", ""));
    items.push(this.createItem("TL244", "Parameter 1", "test", ""));
    return items;
  }

  getRightConfigurationTest(): ConfigurationItem[] {
    return this.getDummyRightConfiguration();
  }

  getRightConfigurationDev(): ConfigurationItem[] {
    return this.getDummyRightConfiguration();
  }

  getRightConfigurationProd(): ConfigurationItem[] {
    return this.getDummyRightConfiguration();
  }

  getDummyRightConfiguration(): ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("Dev1", "abc", "ja", ""));
    items.push(this.createItem("Dev1", "xyz", "nein", ""));
    return items;
  }
  createItem(env: string, key: string, val: string, soll: string): ConfigurationItem {
    return new ConfigurationItem(env, key, val, soll);
  }

  generateTree(env?: string[]): void {
    this.settingsService.getCompareServerConfig().subscribe(data => {
      let tree = data;
      if (env != null) {
        env.forEach(e => {
          tree = this.addEnvToTree(tree, e);
        });
      }
      if (tree != null) {
        this.fillValuesToArray(tree);
      }
      this.treeNodes.next(tree);
    });

  }

  addEnvToTree(tree: Node<ENVCONFIG>[], env: string): Node<ENVCONFIG>[] {
    tree.forEach(i => {
      this.addEnv(i, env);
    });
    return tree;
  }

  addEnv(node: Node<ENVCONFIG>, env: string): void {
    node.value[env] = "";
    if (node.children.length != 0) {
      node.children.forEach(c => {
        this.addEnv(c, env);
      });
    }
  }

  fillValuesToArray(tree: Node<ENVCONFIG>[]): void {
    tree.forEach(i => {
      this.fillValues(i);
    });
  }

  fillValues(node: Node<ENVCONFIG>): void {
    if (this.itemlist && this.itemlist.length != 0) {
      this.itemlist.forEach(i => {
        if (i.key == node.value.Konfigurationsparameter && node.value[i.env] === "") {
          if (i.soll === "") {
            i.soll = i.value
            i.identic = true;
          } else if (i.soll == i.value) {
            i.identic = true
          } else {
            i.identic = false;
          }
          let item: ENVVAL = {
            ist: i.value,
            soll: i.soll,
            identic: i.identic
          };
          node.value[i.env] = item;
          return;
        }
        if (node.children.length != 0) {
          node.children.forEach(c => {
            this.fillValues(c);
          });
        }
      });
    }
  }

  saveAllSollValue(hosts: string[]): void {
    if (hosts) {
      hosts.forEach(host => {
        this.itemlist.forEach(item => {
          if (item.env === host) {
            this.elasticService.saveSollwerte(item.env, item.key, item.soll).subscribe(data => {
              console.log(data);
            })
          }
        })
      });

    }
  }
}

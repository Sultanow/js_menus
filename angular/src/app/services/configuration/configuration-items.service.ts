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
        item.expected = elem.expected;
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
          this.elasticService.getExpectedValueByHostnameAndKey(host, item.key_).subscribe(expectedValue => {
            this.addConfigItem(this.createItem(host, item.key_, item.lastvalue, expectedValue))
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




  createItem(env: string, key: string, val: string, expected: string): ConfigurationItem {
    return new ConfigurationItem(env, key, val, expected);
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
        if (i.key == node.value.Konfigurationsparameter&& node.value[i.env] === "") {
          if (i.expected === "") {
            i.expected = i.value
            i.identic = true;
          } else if (i.expected == i.value) {
            i.identic = true
          } else {
            i.identic = false;
          }
          let item: ENVVAL = {
            actual: i.value,
            expected: i.expected,
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
}

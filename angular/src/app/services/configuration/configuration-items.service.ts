import { ConfigurationItem } from 'src/app/model/configurationItem';
import { Injectable, OnDestroy } from '@angular/core';
import { ServerConfiguration } from 'src/config/ServerConfiguration';
import { Observable,  BehaviorSubject } from 'rxjs';
import { ENVCONFIG, ENVVAL } from 'src/app/model/evntreetable';
import { Node } from 'src/app/components/treetable/treetable.module';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationItemsService implements OnDestroy {
	
  ngOnDestroy(): void {
  }

  private backendZabbixURL: string = '/backend/zabbixapi';

  zabbixResult: BehaviorSubject<JSON[]> = new BehaviorSubject<JSON[]>([]);
  treeNodes: BehaviorSubject<Node<ENVCONFIG>[]> = new BehaviorSubject<Node<ENVCONFIG>[]>([]);

  private _itemlist: ConfigurationItem[] = [];
  public get itemlist(): ConfigurationItem[] {
    return this._itemlist;
  }
  public set itemlist(value: ConfigurationItem[]) {
    this._itemlist = value;
  }

  constructor (private http: HttpClient) { }

  getServerConfiguration(environments: string[]): Observable<any> {
    let params = new HttpParams();
    environments.forEach(environment => {
      params = params.append('host', environment);
    })
    return this.http.get(`${this.backendZabbixURL}/getInformationForHosts`, {params: params});
  }

  addConfiguration(list: ConfigurationItem[]) {
		this.itemlist = list;
	}

  createServerConf(result: any[]): ConfigurationItem[] {
    let ret: ConfigurationItem[] = [];
    result.forEach(elem => {
      let host = elem.host;
      console.log(elem);
      elem.items.forEach(item => {
        if (ServerConfiguration.EVN_ITEMS.includes(item.key_)) {
          console.log("Item found " + item.key_);
          ret.push(this.createItem(host, item.name, item.lastvalue));
        }
      });
    });
    return ret;
  }



  getDummyServerConfiguration(): ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("Dev1", "SW-Version", "20.02.00_5"));
    items.push(this.createItem("Dev1", "Silbentrennung", "an"));
    items.push(this.createItem("Dev1", "Text-Version", "V20.02.00_2"));
    items.push(this.createItem("Dev1", "Hilfsapplication-Version", "2.03.1"));
    items.push(this.createItem("Dev4", "SW-Version", "19.02.00_52"));
    return items;
  }

  getBatchConfiguration(): ConfigurationItem[] {
    return this.getDummyBatchConfig();
  }

  getDummyBatchConfig(): ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("A2145", "Parameter 1", "5"));
    items.push(this.createItem("A2145", "Parameter 2", "x"));
    items.push(this.createItem("TL244", "Parameter 1", "test"));
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
    items.push(this.createItem("Dev1", "abc", "ja"));
    items.push(this.createItem("Dev1", "xyz", "nein"));
    return items;
  }
  createItem(env: string, key: string, val: string): ConfigurationItem {
    return new ConfigurationItem(env, key, val);
  }

  generateTree(env?: string[]): void {
    let tree = this.buildEmptyTree();
    if (env != null) {
      env.forEach(e => {
        tree = this.addEnvToTree(tree, e);
      });
    }
    this.setSollValues();
    if (tree != null) {
      this.fillValuesToArray(tree);
    }
    this.treeNodes.next(tree);
  }

  buildEmptyTree() {
    let tree: Node<ENVCONFIG>[] = ServerConfiguration.EMPTY_TREE;
    return tree;
  }

  addEnvToTree(tree: Node<ENVCONFIG>[], env: string): Node<ENVCONFIG>[] {
    tree.forEach(i => {
      this.addEnv(i, env);
    });
    return tree;
  }

  addEnv(node: Node<ENVCONFIG>, env: string): void {
    node.value[ env ] = "";
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
        if (i.key == node.value.Konfigurationsparameter && node.value[ i.env ] === "") {
          let item: ENVVAL = {
            ist: i.value,
            soll: i.soll,
            identic: i.identic
          };
          node.value[ i.env ] = item;
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

  createDummyDataTable(): void {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("dev1", "SW-Version", "20.02.00_5"));
    items.push(this.createItem("dev1", "Silbentrennung", "an"));
    items.push(this.createItem("dev1", "Text-Version", "V20.02.00_2"));
    items.push(this.createItem("dev1", "Hilfsapplication-Version", "2.03.1"));
    items.push(this.createItem("dev4", "SW-Version", "19.02.00_52"));
    items.push(this.createItem("dev1", "Top1", "dev1 - Top1"));
    items.push(this.createItem("dev2", "Top1", "dev2 - Top1"));
    items.push(this.createItem("dev1", "Level2 - 1", "dev1 - Level2 - 1"));
    items.push(this.createItem("dev2", "Level2 - 1", "dev2 - Level2 - 1"));
    items.push(this.createItem("dev1", "Level2 - 2", "dev1 - Level2 - 2"));
    items.push(this.createItem("dev2", "Level2 - 2", "dev2 - Level2 - 2"));
    items.push(this.createItem("dev1", "Level2 - 3", "dev1 - Level2 - 3"));
    items.push(this.createItem("dev2", "Level2 - 3", "dev2 - Level2 - 3"));
    items.push(this.createItem("dev1", "Level2 - 4", "dev1 - Level2 - 4"));
    items.push(this.createItem("dev2", "Level2 - 4", "dev2 - Level2 - 4"));
    items.push(this.createItem("dev1", "Level2 - 5", "dev1 - Level2 - 5"));
    items.push(this.createItem("dev2", "Level2 - 5", "dev2 - Level2 - 5"));
    items.push(this.createItem("dev1", "Level2 - 6", "dev1 - Level2 - 6"));
    items.push(this.createItem("dev2", "Level2 - 6", "dev2 - Level2 - 6"));
    items.push(this.createItem("dev2", "Top2", "dev2 - Top2"));
    items.push(this.createItem("dev1", "Top2", "dev1 - Top2"));
    items.push(this.createItem("dev2", "Top3", "dev2 - Top3"));
    items.push(this.createItem("dev1", "Top3", "dev1 - Top3"));

    items.forEach(x => {
      this.itemlist.push(x);
    });
  }
  setSollValues(): void {
    this.itemlist.forEach(element => {
    });
  }
}

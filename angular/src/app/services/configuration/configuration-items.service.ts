import { ConfigurationItem } from 'src/app/model/configurationItem';
import { Injectable, OnDestroy } from '@angular/core';
import { ZabbixClient } from "zabbix-client";
import { ServerConfiguration } from 'src/config/ServerConfiguration';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { ENVCONFIG } from 'src/app/model/evntreetable';
import { Node } from 'src/app/components/treetable/treetable.module' 

@Injectable({
  providedIn: 'root'
})
export class ConfigurationItemsService implements OnDestroy {
  ngOnDestroy(): void {
    this.client = null;
  }
  
  zabbixResult: BehaviorSubject<JSON[]> = new BehaviorSubject<JSON[]>([]);
  treeNodes: BehaviorSubject<Node<ENVCONFIG>[]> = new BehaviorSubject<Node<ENVCONFIG>[]>([]);

  client: ZabbixClient;
  itemlist: ConfigurationItem[];

  constructor() { }

  getItemlist(): ConfigurationItem[] {
    return this.itemlist;
  }

  async getServerConfiguration(): Promise<{}> {
    return new Promise((resolve, reject) => {
      this.zabbixLogin().then(api => {
        api.method("host.get")
          .call({
            'filter': {
              'host': ServerConfiguration.ENV_LIST
            },
            'output': 'extend',
            'selectGroups': 'extend',
            'selectHosts': 'extend',
            'selectItems': 'extend',
          }, false)
          .then(result => {
            this.zabbixResult.next(result as JSON[]);
            this.itemlist = this.createServerConf(result as JSON[]);
            resolve();
          })
          .catch(x => {
            console.log("Error", x)
            this.itemlist = this.getDummyServerConfiguration();
            resolve();
          });
      });
    });

  }

  zabbixLogin() {
    this.client = new ZabbixClient("/api_jsonrpc.php");
    return this.client.login("viewer", "viewer");
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
      })
    })
    return ret;
  }



  getDummyServerConfiguration(): ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("Dev1", "SW-Version", "20.02.00_5"))
    items.push(this.createItem("Dev1", "Silbentrennung", "an"))
    items.push(this.createItem("Dev1", "Text-Version", "V20.02.00_2"))
    items.push(this.createItem("Dev1", "Hilfsapplication-Version", "2.03.1"))
    items.push(this.createItem("Dev4", "SW-Version", "19.02.00_52"))
    return items;
  }

  getBatchConfiguration(): ConfigurationItem[] {
    return this.getDummyBatchConfig();
  }

  getDummyBatchConfig(): ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("A2145", "Parameter 1", "5"))
    items.push(this.createItem("A2145", "Parameter 2", "x"))
    items.push(this.createItem("TL244", "Parameter 1", "test"))
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
    if(env != null) {
      env.forEach(e => {
        tree = this.addEnvToTree(tree, e);
      })
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
    })
    return tree;
  }

  addEnv(node: Node<ENVCONFIG>, env: string) {
    node.value[env] = "";
    if(node.children.length != 0) {
      node.children.forEach(c => {
        this.addEnv(c, env);
      })
    }
  }
}

import { Injectable } from '@angular/core';
import { ConfigurationItem } from 'src/app/model/configurationItem';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationItemsService {

  constructor() { }

  getServerConfiguration() : ConfigurationItem[] {
    return this.getDummyServerConfiguration();
  }

  getDummyServerConfiguration() : ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("Dev1", "SW-Version", "20.02.00_5"))
    items.push(this.createItem("Dev1", "Silbentrennung", "an"))
    items.push(this.createItem("Dev1", "Text-Version", "V20.02.00_2"))
    items.push(this.createItem("Dev1", "Hilfsapplication-Version", "2.03.1"))
    items.push(this.createItem("Dev4", "SW-Version", "19.02.00_52"))
    return items;
  }

  getBatchConfiguration() : ConfigurationItem[] {
    return this.getDummyBatchConfig();
  }

  getDummyBatchConfig() : ConfigurationItem[] {
    let items: ConfigurationItem[] = [];
    items.push(this.createItem("A2145", "Parameter 1", "5"))
    items.push(this.createItem("A2145", "Parameter 2", "x"))
    items.push(this.createItem("TL244", "Parameter 1", "test"))
    return items;
  }

  createItem(env: string, key: string, val: string): ConfigurationItem {
    return new ConfigurationItem(env, key, val);
  }
}

import { Injectable } from '@angular/core';
import { CompareItem } from 'src/app/model/compareItem';

@Injectable({
  providedIn: 'root'
})
export class CompareItemsService {

  constructor () { }

  getDummyData(): CompareItem[] {
    let data: CompareItem[] = [];
    data.push(this.createItem("Silbentrennung", "an", "an", "aus", "aus", "aus", "aus"));
    data.push(this.createItem("Dritanbierterstrecken erreichbar", "ja", "nein", "ja", "nein", "ja", "ja"));
    data.push(this.createItem("Produktversion", "20.01.00.00.01", "20.01.00.00.01", "20.01.00.00.01", "19.01.00.00.01", "20.01.00.00.01_b", "20.01.00.02.01"));
    return data;
  }

  createItem(value: string, ref: string, val1: string, val2: string, val3: string, val4: string, val5: string) {
    let item = new CompareItem(value, ref);
    item.addItem("dev1", val1);
    item.addItem("dev2", val2);
    item.addItem("dev3", val3);
    item.addItem("dev4", val4);
    item.addItem("dev5", val5);
    return item;
  }
}

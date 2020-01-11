import { Injectable } from '@angular/core';
import { CompareItem } from 'src/app/model/compareItem';

@Injectable({
  providedIn: 'root'
})
export class CompareItemsService {

  constructor() {

       }

  getDummyData(): CompareItem[] {
    let data: CompareItem[] = [];
    let c  = new CompareItem("Silbentrennung", "an");
    c.addItem("dev1", "an");
    c.addItem("dev2", "an");
    c.addItem("dev3", "aus");
    c.addItem("dev4", "aus");
    c.addItem("dev5", "aus");
    data.push(c);
    return data;
  } 
}

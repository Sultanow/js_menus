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
    data.push(new CompareItem("Silbentrennung", "an", "an", "aus", "aus", "an"));
    return data;
  } 
}

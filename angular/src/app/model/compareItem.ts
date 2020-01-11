export class CompareItem {

    key: string;
    ref: string;
    values: Item[] = [];
    

    constructor(key:string, ref:string){
        this.key = key;
        this.ref = ref;
    }

    addItem(key: string, value: string) : void {
        this.values.push(new Item(key, value));
    }
}

export class Item {
    key: string;
    value: string;
    checked: boolean;

    constructor(key: string, value: string) {
        this.key = key;
        this.value = value;
    }
}
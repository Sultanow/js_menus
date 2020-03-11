export class Configuration {

    key: string;
    value: string;
    oldValue: string;
    name: string;


    constructor (key: string, value: string, name: string) {
        this.key = key;
        this.value = this.oldValue = value;
        this.name = name;
    }
}

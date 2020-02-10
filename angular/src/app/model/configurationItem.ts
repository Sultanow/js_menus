export class ConfigurationItem {

    env: string;
    key: string;
    value: string;
    soll: string;
    identic: boolean;

    constructor(env: string, key:string, value:string){
        this.env = env;
        this.key = key;
        this.value = value;
        this.soll = value;
        this.identic = true;
    }
}
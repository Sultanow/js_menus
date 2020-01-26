export class ConfigurationItem {

    env: string;
    key: string;
    value: string;

    constructor(env: string, key:string, value:string){
        this.env = env;
        this.key = key;
        this.value = value;
    }
}
export class ConfigurationItem {

    env: string;
    key: string;
    value: string;
    soll: string;
    identic: boolean;

    constructor(env: string, key: string, value: string, soll: string) {
        this.env = env;
        this.key = key;
        this.value = value;
        this.soll = soll;
        this.identic = (this.value ===  this.soll);
    }
}

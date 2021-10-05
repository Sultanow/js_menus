export class ConfigurationItem {

    env: string;
    key: string;
    value: string;
    expected: string;
    identic: boolean;

    constructor(env: string, key: string, value: string, expected: string) {
        this.env = env;
        this.key = key;
        this.value = value;
        this.expected = expected;
        this.identic = (this.value ===  this.expected);
    }
}

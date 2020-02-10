export type ENVCONFIG = {
    configname: string;
    [name: string]: ENVVAL | string;
}

export type ENVVAL = {
    ist: string;
    soll: string;
    identic: boolean;
}
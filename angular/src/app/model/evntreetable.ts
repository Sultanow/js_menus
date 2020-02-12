export type ENVCONFIG = {
    Konfigurationsparameter: string;
    [name: string]: ENVVAL | string;
}

export type ENVVAL = {
    ist: string;
    soll: string;
    identic: boolean;
}
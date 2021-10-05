export type ENVCONFIG = {
    Konfigurationsparameter: string;
    [ name: string ]: ENVVAL | string;
};

export type ENVVAL = {
    actual: string;
    expected: string;
    identic: boolean;
};

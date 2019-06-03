import { Injectable } from '@angular/core';
import { Http } from '@angular/http';


import { Configuration } from '../model/configuration';
import { ServerConfiguration } from 'src/config/ServerConfiguration';

@Injectable()
export class ConfigurationService {

    constructor(private http: Http) { }

    getConfiguration(key): Promise<Configuration> {
        return this.http.get( ServerConfiguration.SERVICE_URL + "/static/configuration" )
            .toPromise()
            .then( response => response.json() as Configuration )
    }    
}
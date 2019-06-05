import { Injectable } from '@angular/core';
import { Http } from '@angular/http';

import { Configuration } from '../model/configuration';
import { ServerConfiguration } from 'src/config/ServerConfiguration';

@Injectable()
export class ConfigurationService {

    constructor(private http: Http) { }

    getStaticConfiguration(): Promise<Configuration> {
        return this.http.get( ServerConfiguration.SERVICE_URL + "/static/configuration" )
            .toPromise()
            .then( response => response.json() as Configuration )
    }    

    getRedisConfiguration(...theArgs): Promise<Configuration[]> {
        let keys:string = "";
        // concat keys e.g. get?key=1&key=2...
        for (let i = 0; i < theArgs.length; i++) {
            let keyToAdd:string = theArgs[i] as string;
            if(i==0){
                keys += keyToAdd;
            }
            else{
                keys += "&key="+ keyToAdd;
            }
        }

        return this.http.get( ServerConfiguration.SERVICE_URL + "/redis/configuration/get?key=" + keys)
            .toPromise()
            .then( response => response.json() as Configuration[] )
    }  
}
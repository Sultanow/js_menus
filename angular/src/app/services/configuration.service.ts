import { Injectable } from '@angular/core';
import { Http, Response, RequestOptions, Headers } from '@angular/http';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';

import { Configuration } from '../model/configuration';

@Injectable()
export class ConfigurationService {

    getConfigurations(): Observable<string> {
        return this.http.get(  this.config.SERVER_URL + "/rs/resin/getall" ).map( res => res.json() );
    }

    getConfiguration(key): Promise<Configuration> {
        return this.http.get( SERVER_URL + "/rs/resin/get" )
            .toPromise()
            .then( response => response.json() as Configuration )
    }    
}
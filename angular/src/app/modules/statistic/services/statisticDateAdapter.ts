import { Injectable } from '@angular/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import * as moment from 'moment';
import { DateTimeService } from './date-time.service';

@Injectable()
export class StatisticDateAdapter extends MomentDateAdapter {
  constructor (private _dateTimeService: DateTimeService) {
    super(_dateTimeService.locale);
  }

  public format(date: moment.Moment, displayFormat: string): string {
    return date.locale(this._dateTimeService.locale).format(this._dateTimeService.format);
  }
}

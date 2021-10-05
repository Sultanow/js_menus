import { Injectable } from '@angular/core';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import * as moment from 'moment';
import { DateTimeService } from './date-time.service';
import { MatDateFormats } from '@angular/material/core';

@Injectable()
export class StatisticDateAdapter extends MomentDateAdapter {
  constructor (private _dateTimeService: DateTimeService) {
    super(_dateTimeService.locale);
  }

  public format(date: moment.Moment, displayFormat: string): string {
    return date.locale(this._dateTimeService.locale).format(displayFormat);
  }
}

export const APP_DATE_FORMATS: MatDateFormats = {
  parse: {
    dateInput: { month: 'short', year: 'numeric', day: 'numeric' },
  },
  display: {
    dateInput: 'LL',
    monthYearLabel: 'yyyy MMM',
    dateA11yLabel: 'yyyy-MMM-DD',
    monthYearA11yLabel: 'yyyy-MMM',
  }
};

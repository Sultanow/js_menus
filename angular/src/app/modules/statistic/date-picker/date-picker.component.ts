import { Component, OnInit, Input, ViewEncapsulation, OnChanges, SimpleChanges, EventEmitter, Output } from '@angular/core';
import { StatisticAccuracy } from '../model/statisticAccuracy';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import * as moment from 'moment';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-date-picker',
  templateUrl: './date-picker.component.html',
  styleUrls: [ './date-picker.component.css' ],
  encapsulation: ViewEncapsulation.None,
})
export class DatePickerComponent implements OnInit, OnChanges {
  @Input() title: string = "Datum auswählen";
  @Input() startDate: string;
  @Input() endDate: string;
  @Input() accuracy: StatisticAccuracy = StatisticAccuracy.DAY;
  @Input() availableDates: string[] = [];
  @Input() isMultiple: boolean = false;

  @Output() datepickerChangeEvent: EventEmitter<Map<string, string>> = new EventEmitter<Map<string, string>>();

  availableMomentDates: Set<moment.Moment> = new Set<moment.Moment>();
  momentStartDate: moment.Moment = null;

  startView: string = "month";

  dateFormControl = new FormControl({ value: moment(), disabled: true });

  pickerRangeGroup = new FormGroup({
    start: new FormControl({ value: moment(), disabled: true }),
    end: new FormControl({ value: moment(), disabled: true })
  });

  updateDates: boolean = false;

  constructor () { }

  ngOnChanges(changes: SimpleChanges): void {
    this.updateAvailableDates();
  }

  ngOnInit(): void {
    this.updateAvailableDates();
  }

  updateAvailableDates() {
    this.updateDates = true;
    if (this.availableDates !== null && this.availableDates.length !== 0) {
      this.availableMomentDates.clear();
      this.availableDates.forEach(strDate => {
        this.availableMomentDates.add(moment(strDate, this.accuracy));
      });
    }
    if (this.isMultiple) {
      if (this.startDate && this.endDate) {
        this.pickerRangeGroup.controls[ 'start' ].setValue(moment(this.startDate, this.accuracy));
        this.pickerRangeGroup.controls[ 'end' ].setValue(moment(this.endDate, this.accuracy));
      }
    } else {
      if (this.startDate) {
        this.dateFormControl.setValue(moment(this.startDate, this.accuracy));
      } else {
        this.dateFormControl.setValue(moment().format(this.accuracy));
      }
    }
    if (this.accuracy == StatisticAccuracy.MONTH) {
      this.startView = "year";
    } else if (this.accuracy == StatisticAccuracy.YEAR) {
      this.startView = "multi-year";
    }
    this.updateDates = false;
  }

  dateFilter = (d: moment.Moment | null): boolean => {
    let ret = false;
    setTimeout(() =>
      this.updateDayStyles()
    );
    this.availableMomentDates.forEach(item => {
      if (item.isSame(d)) {
        ret = true;
        return;
      }
    });
    return ret;
  };

  dateChangeEvent(event: MatDatepickerInputEvent<moment.Moment>) {
    if (event.value !== null && !this.updateDates) { // Value is null when end not selected.
      let result: Map<string, string> = new Map<string, string>();
      if (!this.isMultiple) {
        let date = event.value.format(this.accuracy);
        result.set("start", date);
      } else {
        let start: moment.Moment = this.pickerRangeGroup.controls[ 'start' ].value;
        let end: moment.Moment = this.pickerRangeGroup.controls[ 'end' ].value;
        result.set("start", start.format(this.accuracy));
        result.set("end", end.format(this.accuracy));
      }
      this.datepickerChangeEvent.emit(result);
    }
  }

  updateDayStyles() {
    // Appended from https://stackblitz.com/edit/mat-date-tooltip
    let elements = document.querySelectorAll(".mat-calendar-body");
    let x: any = elements.length > 0 ? elements[ 0 ].querySelectorAll('.mat-calendar-body-cell') : [];
    x.forEach(y => {
      let found = false;
      let ret = false;
      this.availableMomentDates.forEach(t => {
        let f = moment(y.getAttribute('aria-label'), "YYYY-MMM-DD", 'de');
        if (f.isSame(t)) {
          ret = true;
        }
      });
      if (!ret) {
        found = true;
        let div = y.querySelectorAll('div')[ 0 ];
        div.classList.add('tooltip');
        let spans = y.querySelectorAll('span');
        if (spans.length > 0) {
          let span = spans[ 0 ];
          span.innerHTML = "Keine Daten verfügbar";
        }
        else {
          let span = document.createElement('span');
          span.innerHTML = "Keine Daten verfügbar";
          span.classList.add('tooltiptext');
          div.appendChild(span);
        }
      }
      if (!found) {
        let div = y.querySelectorAll('div')[ 0 ];
        div.classList.remove('tooltip');
        let spans = y.querySelectorAll('span');
        if (spans.length > 0) {
          spans.forEach(span => {
            div.removeChild(span);
          });
        }
      }
    });
  }
}

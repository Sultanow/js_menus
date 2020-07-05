import { Component, OnInit, Input, ViewEncapsulation, OnChanges, SimpleChanges, EventEmitter, Output } from '@angular/core';
import { StatisticAccuracy } from '../model/statisticAccuracy';
import { MatCalendarCellCssClasses, MatDatepickerInputEvent } from '@angular/material/datepicker';
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
  format: string;
  momentStartDate: moment.Moment = null;

  dateFormControl = new FormControl({ value: moment(), disabled: true });

  pickerRangeGroup = new FormGroup({
    start: new FormControl({ value: moment(), disabled: true }),
    end: new FormControl({ value: moment(), disabled: true })
  });

  constructor () { }

  ngOnChanges(changes: SimpleChanges): void {
    this.updateAvailableDates();
  }

  ngOnInit(): void {
    this.updateAvailableDates();
  }

  updateAvailableDates() {
    if (this.availableDates !== null && this.availableDates.length !== 0) {
      this.availableMomentDates.clear();
      this.setFormat();
      this.availableDates.forEach(strDate => {
        this.availableMomentDates.add(moment(strDate, this.format));
      });
      console.log(this.availableMomentDates);
    }
    if (this.isMultiple) {
      if(this.startDate !== null && this.startDate != "" && this.endDate !== null && this.endDate !== "") {
        this.pickerRangeGroup.controls['start'].setValue(moment(this.startDate, this.format));
        this.pickerRangeGroup.controls['end'].setValue(moment(this.endDate, this.format));
      }
    } else {
      if (this.startDate !== null && this.startDate !== "") {
        this.dateFormControl.setValue(moment(this.startDate, this.format));
      } else {
        this.dateFormControl.setValue(moment().format(this.format));
      }
    }

  }

  /**
   * Set the format based on the accuracy input.
   */
  setFormat() {
    switch (this.accuracy) {
      case StatisticAccuracy.MONTH:
        this.format = "YYYY-MM";
        break;
      case StatisticAccuracy.YEAR:
        this.format = "YYYY";
        break;
      case StatisticAccuracy.WEEK:
        this.format = "YYYY-ww";
        break;
      case StatisticAccuracy.DAY:
      default:
        this.format = "YYYY-MM-DD";
    }
  }

  dateClass = (d: moment.Moment): MatCalendarCellCssClasses => {
    let ret = false;
    this.availableMomentDates.forEach(item => {
      if (item.isSame(d)) {
        ret = true;
        return;
      }
    });
    return ret ? 'activeDate' : 'inactiveDate';
  };

  dateChangeEvent(event: MatDatepickerInputEvent<moment.Moment>) {
    console.log(event);
    if (event.value !== null) { // Value is null when end not selected.
      let result: Map<string, string> = new Map<string, string>();
      if (!this.isMultiple) {
        let date = event.value.format(this.format);
        result.set("start", date);
      } else {
        let start: moment.Moment = this.pickerRangeGroup.controls[ 'start' ].value;
        let end: moment.Moment = this.pickerRangeGroup.controls[ 'end' ].value;
        console.log(start.format(this.format), ", ", end.format(this.format));
        result.set("start", start.format(this.format));
        result.set("end", end.format(this.format));
      }
      this.datepickerChangeEvent.emit(result);
    }
  }
}

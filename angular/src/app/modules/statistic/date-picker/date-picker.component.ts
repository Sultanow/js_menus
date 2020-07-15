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
      this.availableDates.forEach(strDate => {
        this.availableMomentDates.add(moment(strDate, this.accuracy));
      });
    }
    if (this.isMultiple) {
      if(this.startDate && this.endDate) {
        this.pickerRangeGroup.controls['start'].setValue(moment(this.startDate, this.accuracy));
        this.pickerRangeGroup.controls['end'].setValue(moment(this.endDate, this.accuracy));
      }
    } else {
      if (this.startDate !== null && this.startDate !== "") {
        this.dateFormControl.setValue(moment(this.startDate, this.accuracy));
      } else {
        this.dateFormControl.setValue(moment().format(this.accuracy));
      }
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
}

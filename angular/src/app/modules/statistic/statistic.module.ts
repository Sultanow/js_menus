import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatisticComponent } from './statistic.component';
import { GraphsComponent, } from './graphs/graphs.component';
import { CreateChartComponent } from './create-chart/create-chart.component';
import { MaterialModule } from 'src/app/material.module';
import { MAT_DATE_LOCALE, DateAdapter } from '@angular/material/core';
import { DragAndDropModule } from '../../components/drag-and-drop/drag-and-drop.module';
import { DatePickerComponent } from './date-picker/date-picker.component';
import { StatisticDateAdapter } from './services/statisticDateAdapter';
import { DialogDeleteChart } from './graphs/dialog-delete-chart.component'
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    StatisticComponent,
    GraphsComponent,
    CreateChartComponent,
    DialogDeleteChart,
    DatePickerComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    DragAndDropModule,
    ReactiveFormsModule
  ],
  exports: [
    StatisticComponent,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'de' },
    StatisticDateAdapter, // so we could inject services to 'CustomDateAdapter'
    { provide: DateAdapter, useClass: StatisticDateAdapter }, // Parse MatDatePicker Format
  ]
})
export class StatisticModule { }

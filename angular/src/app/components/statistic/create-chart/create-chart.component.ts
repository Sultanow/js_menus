import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, Validators, FormControl } from '@angular/forms';
import { StatisticService } from 'src/app/services/statistic/statistic.service';

@Component({
  selector: 'app-create-chart',
  templateUrl: './create-chart.component.html',
  styleUrls: [ './create-chart.component.css' ]
})
export class CreateChartComponent implements OnInit {
  @Input()
  showCreateChartContainer: boolean;

  @Output() notifyNewChartSubmitted = new EventEmitter<boolean>();

  scriptName: string = "";

  myForm = new FormGroup({
    name: new FormControl('', [ Validators.required, Validators.minLength(3) ]),
    file: new FormControl('', [ Validators.required ]),
    fileSource: new FormControl('', [ Validators.required ])
  });

  constructor (private statisticService: StatisticService) { }

  ngOnInit(): void {
  }

  onFileChange(event) {

    if (event.target.files.length > 0) {
      const file = event.target.files[ 0 ];
      this.myForm.patchValue({
        fileSource: file
      });
    }
  }

  get f() {
    return this.myForm.controls;
  }

  submit() {
    if (this.myForm.valid)
      this.statisticService.createChart(this.myForm.get('name').value, this.myForm.get('fileSource').value, "").subscribe(result => {
        console.log(result);
        this.notifyNewChartSubmitted.emit(true);
      });
  }

}

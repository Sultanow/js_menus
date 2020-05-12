import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { FormGroup, Validators, FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { StatisticService } from 'src/app/services/statistic/statistic.service';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
@Component({
  selector: 'app-create-chart',
  templateUrl: './create-chart.component.html',
  styleUrls: [ './create-chart.component.css' ]
})
export class CreateChartComponent implements OnInit, OnChanges {
  @Input()
  showCreateChartContainer: boolean;
  @Output() notifyNewChartSubmitted = new EventEmitter<boolean>();

  matcher = new MyErrorStateMatcher();

  filteredGroups: Observable<string[]>;
  groups: string[] = [];

  scriptName: string = "";

  myForm = new FormGroup({
    group: new FormControl(),
    name: new FormControl('', [ Validators.required, Validators.minLength(3) ]),
    file: new FormControl('', [ Validators.required ]),
    fileSource: new FormControl('', [ Validators.required ]),
    description: new FormControl(),
  });

  constructor (private statisticService: StatisticService, private snackBar: MatSnackBar) {
    this.filteredGroups = this.myForm.get("group").valueChanges
      .pipe(
        startWith(''),
        map(state => state ? this._filterStates(state) : this.groups.slice())
      );
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (this.showCreateChartContainer) {
      this.statisticService.getGroups().subscribe(result => {
        console.log(result);
        this.groups = result;
      });
    }
  }

  ngOnInit(): void {
  }

  onFileChange(event) {

    if (event.target.files.length > 0) {
      const file = event.target.files[ 0 ];
      this.myForm.patchValue({
        fileSource: file
      });
      this.scriptName = file.name;
    }
  }

  submit() {
    if (this.myForm.valid) {
      this.statisticService.createChart(
        this.myForm.get('name').value,
        this.myForm.get('group').value,
        this.myForm.get('fileSource').value,
        this.myForm.get('description').value)
        .subscribe(result => {
          this.notifyNewChartSubmitted.emit(true);
        });
    }
    else {
      this.snackBar.open('Es müssen Name und ein Skript hinterlegt sein.', '',
        {
          duration: 2000,
        });
    }
  }

  private _filterStates(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.groups.filter(group => group.toLowerCase().indexOf(filterValue) === 0);
  }


}

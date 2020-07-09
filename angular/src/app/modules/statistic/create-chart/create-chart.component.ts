import { Component, OnInit, Input, Output, EventEmitter, OnChanges, SimpleChanges, ViewChild } from '@angular/core';
import { FormGroup, Validators, FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SpinnerService } from 'src/app/services/spinner/spinner.service';
import { StatisticService } from '../services/statistic.service';

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

  fileUploadText: string = "Datei hochladen";

  @ViewChild('form') form: FormGroupDirective;

  matcher = new MyErrorStateMatcher();

  filteredGroups: Observable<string[]>;
  groups: string[] = [];

  scriptName: string = "";
  resetDragNDrop: boolean = false;

  newChartForm = new FormGroup({
    group: new FormControl(),
    name: new FormControl('', [ Validators.required, Validators.minLength(3) ]),
    fileSource: new FormControl('', [ Validators.required ]),
    description: new FormControl(),
  });

  constructor (private statisticService: StatisticService, private snackBar: MatSnackBar, private spinnerService: SpinnerService) {
    this.filteredGroups = this.newChartForm.get("group").valueChanges
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

  onFileAdded(file: File) {
    this.newChartForm.patchValue({
      fileSource: file
    });
  }

  onFileChange(event) {

    if (event.target.files.length > 0) {
      const file = event.target.files[ 0 ];
      this.newChartForm.patchValue({
        fileSource: file
      });
      this.scriptName = file.name;
    }
  }

  submit() {
    if (this.newChartForm.valid) {
      let group = this.newChartForm.get('group').value;
      if(null == group) {
        group = "";
      }
      this.spinnerService.show();
      this.statisticService.createChart(
        this.newChartForm.get('name').value,
        group,
        this.newChartForm.get('fileSource').value,
        this.newChartForm.get('description').value)
        .subscribe(result => {
          this.notifyNewChartSubmitted.emit(true);
          this.snackBar.open('Neues Chart wurde angelegt', '', { duration: 2000, });
          this.newChartForm.reset();
          this.scriptName = "";
          this.form.resetForm();
          this.spinnerService.hide();
          this.resetDragNDrop = true;
          
        }, e => {
          console.log(e);
          this.snackBar.open(e.statusText);
          this.spinnerService.hide();
        })
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

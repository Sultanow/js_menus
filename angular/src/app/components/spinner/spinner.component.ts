import { Component, OnInit } from '@angular/core';
import { SpinnerService } from 'src/app/services/spinner/spinner.service';

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.css']
})
export class SpinnerComponent implements OnInit {
  isShowing = false;

  constructor(private spinnerService: SpinnerService) {}

  get show(): boolean {
    return this.isShowing;
  }

  set show(val: boolean) {
    this.isShowing = val;
  }

  ngOnInit(): void {
    this.spinnerService._register(this);
  }
}

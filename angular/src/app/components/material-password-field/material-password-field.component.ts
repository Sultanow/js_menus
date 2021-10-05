import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-material-password-field',
  templateUrl: './material-password-field.component.html',
  styleUrls: ['./material-password-field.component.css']
})
export class MaterialPasswordFieldComponent implements OnInit {
  hidePassword: boolean = true;

  @Input() public formGroup: FormGroup;

  @Input() public label: string = "Password";

  @Input() public formControlName: string;

  constructor() { }

  ngOnInit(): void { }

}

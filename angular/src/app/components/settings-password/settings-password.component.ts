import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-settings-password',
  templateUrl: './settings-password.component.html',
  styleUrls: ['./settings-password.component.css']
})
export class SettingsPasswordComponent implements OnInit {
 
  showSettings : boolean;
  private password : string = "1234";
  loginForm: FormGroup;
  hide = true;
 
  constructor(
    private formBuilder: FormBuilder,public dialogRef: MatDialogRef<SettingsPasswordComponent>)
    { 
      this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
      })
    }
  cancel(){
    this.dialogRef.close(null);
  }
  checkPassword(password : string) {
   
    if (this.password === password){
      this.showSettings = true;
      console.log("ok");
    }else{
      this.showSettings = false;
      console.log("not ok");
    }
    this.dialogRef.close(this.showSettings);
  }
  ngOnInit() {
   
  }
}

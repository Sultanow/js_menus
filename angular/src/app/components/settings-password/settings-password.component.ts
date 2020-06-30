import { Component, OnInit, Output, EventEmitter} from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { MatDialogRef } from '@angular/material/dialog';
import { SettingsService } from 'src/app/services/settings/settings.service';

@Component({
  selector: 'app-settings-password',
  templateUrl: './settings-password.component.html',
  styleUrls: ['./settings-password.component.css']
})
export class SettingsPasswordComponent implements OnInit {
  @Output() authenticateMaessageEvent = new EventEmitter<boolean>(); 
  showSettings : boolean;
  loginForm: FormGroup;
  hide = true;

  constructor(
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<SettingsPasswordComponent>,
     private settingsService: SettingsService)
    { 
      this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
      })
    }

  cancel(){
    this.dialogRef.close(null);
  }

  checkPasswordBackend(password : string) {
    this.settingsService.getAuthData(password).subscribe(token => {
      if(token)
      {
        localStorage.setItem('token', token);
        this.dialogRef.close(this.showSettings);
        alert("Regeisteration ist erfolgreich");
        window.location.reload();
      }else{
        alert("Passwort ist Falsch");
      }
    });
  }
  ngOnInit() {

  }
}

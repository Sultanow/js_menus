import { Component, OnInit} from '@angular/core';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { MatDialogRef } from '@angular/material/dialog';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.css']
})
export class AuthenticationComponent implements OnInit {
 
  loginForm: FormGroup;
  hide = true;

  constructor(
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<AuthenticationComponent>,
     private authenticationService: AuthenticationService)
    { 
      this.loginForm = this.formBuilder.group({
      password: ['', [Validators.required]]
      })
    }

  cancel(){
    this.dialogRef.close(null);
  }

  checkPasswordBackend(password : string) {

    this.authenticationService.login(password).subscribe(token => {
      if(token)
      {
        this.authenticationService.setToken(token);
        this.dialogRef.close(true);
      }else{
        this.dialogRef.close(false);
      }
    });
  }
  
  ngOnInit() {
  }
}

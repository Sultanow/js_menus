import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';
import { AuthenticationService } from 'src/app/services/authentication/authentication.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  changePasswordForm: FormGroup;
  //hide is only used in HTML. Additionally typing here.
  hide = true;

  constructor(
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<ChangePasswordComponent>,
    private authenticationService: AuthenticationService) {
    this.changePasswordForm = this.formBuilder.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]]
    })
  }

  cancel() {
    this.dialogRef.close(null);
  }

  changePassword(oldPassword: string, newPassword: string, confirmPassword: string) {
    if (newPassword !== confirmPassword) {
      this.snackBar.open("Passwort und Passwortbestätigung stimmen nicht überein", "", { duration: 3000 });
      return;
    }
    if (oldPassword === newPassword) {
      this.snackBar.open("Bitte geben Sie ein neues Passwort ein", "", { duration: 3000 });
      return;
    }
    this.authenticationService.changePassword(oldPassword, newPassword).subscribe(data => {
      let isChanged = data.toLowerCase() == "true";
      if (isChanged) {
        this.authenticationService.login(newPassword).subscribe(token => {
          if (token) {
            this.authenticationService.setToken(token);
          }
        });
        this.dialogRef.close(true);
        this.snackBar.open("Ihre Änderung wurde bestätigt", "", { duration: 3000 });
      }
      else {
        this.snackBar.open("Bitte überprüfen Sie Ihre Eingabe, altes Passwort ist falsch", "", { duration: 3000 });
      }
    });
  }

  ngOnInit() {
  }
}

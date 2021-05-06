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

  hidePassword: boolean = true;

  constructor(
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<ChangePasswordComponent>,
    private authenticationService: AuthenticationService,
    formBuilder: FormBuilder,
  ) {
    this.changePasswordForm = formBuilder.group({
      oldPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]]
    })
  }

  cancel() {
    this.dialogRef.close(null);
  }

  changePasswordWithFormData(): void {
    let oldPassword: string = this.changePasswordForm.get("oldPassword").value;
    let newPassword: string = this.changePasswordForm.get("newPassword").value;
    let confirmPassword: string = this.changePasswordForm.get("confirmPassword").value;

    return this.changePassword(oldPassword, newPassword, confirmPassword);
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

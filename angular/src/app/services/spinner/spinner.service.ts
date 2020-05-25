import { Injectable } from '@angular/core';
import { SpinnerComponent } from 'src/app/components/spinner/spinner.component';

@Injectable({
  providedIn: 'root'
})
export class SpinnerService {
  private spinner: SpinnerComponent;

  _register(spinner: SpinnerComponent): void {
    this.spinner = spinner;
  }

  /**
   * Activate Spinner
   */
  show(): void {
    if (this.spinner) {
      this.spinner.show = true;
    }
  }

  /**
   * Deactivate Spinner
   */
  hide(): void {
    if (this.spinner) {
      this.spinner.show = false;
    }
  }

  /**
   * Returns status of Spinner
   */
  isSpinnerActive() {
    return this.spinner.show;
  }
}

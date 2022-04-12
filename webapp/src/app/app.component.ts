import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  /* Template: Maximize content
   * https://stackoverflow.com/questions/45642065/how-to-use-bootstrap-4-flexbox-to-fill-available-content
   */
  template: `
    <div class="d-flex flex-column h-100">
      <header class="mb-2">
        <nav-menu></nav-menu>
      </header>
      <div class="h-100 flex-grow">
        <router-outlet></router-outlet>
      </div>
      <footer>Footer</footer>
    </div>
  `,
})
export class AppComponent {}
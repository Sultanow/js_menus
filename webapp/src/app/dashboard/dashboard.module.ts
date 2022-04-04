import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DashboardComponent } from './dashboard.component';

export const routes = [{ path: '', component: DashboardComponent }];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  providers: [],
})
export class DashboardModule {}

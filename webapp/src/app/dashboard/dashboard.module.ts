import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DashboardComponent } from './dashboard.component';
import { MinicardComponent } from './minicard/minicard.component';

export const routes = [{ path: '', component: DashboardComponent }];

@NgModule({
  declarations: [DashboardComponent, MinicardComponent],
  imports: [CommonModule, RouterModule.forChild(routes)],
  providers: [],
})
export class DashboardModule {}

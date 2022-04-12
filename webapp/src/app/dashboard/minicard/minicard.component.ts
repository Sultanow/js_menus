import { Component, Input } from '@angular/core';

@Component({
  selector: 'minicard',
  templateUrl: './minicard.component.html',
})
export class MinicardComponent {
  @Input() title!: string;
}

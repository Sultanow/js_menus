import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'g[menu-icon]',
  templateUrl: './menu-icon.component.html',
  styleUrls: ['./menu-icon.component.css']
})
export class MenuIconComponent implements OnInit {
  // Set the Main Position of the Menu Item
  @Input() iconMainPositionX : number = 0;
  @Input() iconMainPositionY : number = 0;

  // Set the Position in the menu item
  @Input() iconPositionX : number = 36;
  @Input() iconPositionY : number = 20;

  // Set the Textposition in the menu item
  @Input() iconTextPositionX : number = 0;
  @Input() iconTextPositionY : number = 15;

  @Input() title: string;
  // Set the Icon name of the /assets/symbol-defs.svg
  @Input() icon: string = "dummy";
  // Set the Icon Text name of the /assets/symbol-defs.svg
  @Input() iconText: string;

  // Possibility to disable button
  @Input() buttonActive: boolean = true;
  constructor() { }

  @Input() buttonHighlight: boolean = false;

  ngOnInit(): void {
  }

}

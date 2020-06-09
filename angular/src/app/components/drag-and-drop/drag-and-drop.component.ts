import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-drag-and-drop',
  templateUrl: './drag-and-drop.component.html',
  styleUrls: [ './drag-and-drop.component.css' ]
})
export class DragAndDropComponent implements OnInit {
  @Output() fileEmitter = new EventEmitter();
  @Input() fileUploadText = "Drop File oder klicke um File hochzuladen";
  @Input() containerHeight = "3rem";
  @Input() showUploadAreaFile: boolean = true;

  constructor () { }


  ngOnInit(): void {
  }

  files: any = [];

  file: File = null;

  uploadFile(event) {
    this.file = event[ 0 ];
    this.fileEmitter.emit(this.file);
  }

  deleteFile() {
    this.file = null;
    this.fileEmitter.emit(null);
  }
}

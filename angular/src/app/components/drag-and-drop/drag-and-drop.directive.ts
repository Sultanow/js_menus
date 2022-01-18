import { Directive, Output, EventEmitter, HostBinding, HostListener } from '@angular/core';

@Directive({
  selector: '[appDragAndDrop]'
})
export class DragAndDropDirective {

  constructor () { }
  @Output() onFileDropped = new EventEmitter<any>();

  @HostBinding('style.background-color') private background = '#f4f4f4';
  @HostBinding('style.opacity') private opacity = '1';
  @HostBinding('style.color') private textColor = "black";
  @HostBinding('style.border-radius') private borderRadius = "10px";

  //Dragover listener
  @HostListener('dragover', [ '$event' ]) onDragOver(evt) {
    this.updateBackgroundAndOpacity(evt, '#8c8c8c', '0.8');
    
  }

  //Dragleave listener
  @HostListener('dragleave', [ '$event' ]) public onDragLeave(evt) {
    this.updateBackgroundAndOpacity(evt, '#f4f4f4', '1');
  }

  //Drop listener
  @HostListener('drop', [ '$event' ]) public ondrop(evt) {
    this.updateBackgroundAndOpacity(evt, '#f4f4f4', '1');
    let files = evt.dataTransfer.files;
    if (files.length > 0) {
      this.onFileDropped.emit(files);
    }
  }

  updateBackgroundAndOpacity(evt, background: string, opacity: string) {
    evt.preventDefault();
    evt.stopPropagation();
    this.opacity = opacity;
  }

  debugActualValues() {
    console.log(this.background, this.borderRadius, this.opacity, this.textColor);
  }
}

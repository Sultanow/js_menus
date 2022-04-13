import { Component, OnInit, Input, Output, EventEmitter, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { Release } from 'src/app/model/release';
import * as BpmnJS from 'bpmn-js/dist/bpmn-viewer.production.min.js';
import { CamundaApiConnectorService } from 'src/app/services/releasemanagement/camunda-api-connector.service';

@Component({
  selector: 'app-release-detail',
  templateUrl: './release-detail.component.html',
  styleUrls: ['./release-detail.component.css']
})
export class ReleaseDetailComponent implements OnInit, AfterViewInit {

  @ViewChild('bpmncontainer', { static: true }) private bpmnContainer: ElementRef;

  @Input() release: Release = null;
  @Output() emitRelease = new EventEmitter<Release>()

  constructor(private camunda: CamundaApiConnectorService) { }

  ngOnInit(): void {
    this.camunda.getReleaseTasksByInstanceId(this.release.processInstanceId)
      .subscribe(tasks => {
        this.release.tasks = tasks;
      })
  }

  ngAfterViewInit(): void {
    //Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    //Add 'implements AfterViewInit' to the class.
    this.loadBpmnDiagram()
  }

  loadBpmnDiagram() {
    this.camunda.getReleaseBpmnDiagram(this.release.processDefinitionId)
      .subscribe(xml => {

        let viewer = new BpmnJS({
          container: this.bpmnContainer.nativeElement
        })

        try {
          viewer.importXML(xml);
        } catch (error) {
          console.error(error)
        }
      })
  }

  clearRelease() {
    this.emitRelease.emit(null)
  }

}

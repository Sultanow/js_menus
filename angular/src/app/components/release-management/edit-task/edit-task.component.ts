import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CamundaTask } from 'src/app/model/camunda-task';
import { CamundaApiConnectorService } from 'src/app/services/releasemanagement/camunda-api-connector.service';

@Component({
  selector: 'app-edit-task',
  templateUrl: './edit-task.component.html',
  styleUrls: ['./edit-task.component.css']
})
export class EditTaskComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public task: CamundaTask, private camunda: CamundaApiConnectorService) { }

  ngOnInit(): void {
  }

}

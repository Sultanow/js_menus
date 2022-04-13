import { Component, OnInit } from '@angular/core';
import { VersionDataMockService } from '../../services/releasemanagement/version-data-mock.service'
// import { CamundaApiMockService } from '../../services/releasemanagement/camunda-api-mock.service'
import { CamundaApiConnectorService } from 'src/app/services/releasemanagement/camunda-api-connector.service';
import { Release } from 'src/app/model/release';

@Component({
  selector: 'app-release-management',
  templateUrl: './release-management.component.html',
  styleUrls: ['./release-management.component.css']
})
export class ReleaseManagementComponent implements OnInit {

  currentVersions = []
  currentReleases: Release[] = [];
  detailedRelease: Release = null;

  constructor(private versions: VersionDataMockService, private camunda: CamundaApiConnectorService) {   }

  ngOnInit() {
    this.currentVersions = this.versions.getVersions();
    this.loadReleaseData()
  }

  loadReleaseData() {
    this.currentReleases = []
    this.camunda.getAllReleases()
      .subscribe(releases => {
        //console.info("RECEIVED SERVICE RESPONSE: ", releases)
        this.currentReleases.push(releases)
    })
  }

  onReleaseDetailEmit(release: Release) {
    this.detailedRelease = release;
    console.log("set detailed release to " + release)
  }
}

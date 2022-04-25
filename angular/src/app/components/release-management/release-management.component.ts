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
  isLoading: boolean = false;

  constructor(private versions: VersionDataMockService, private camunda: CamundaApiConnectorService) {   }

  ngOnInit() {
    this.currentVersions = this.versions.getVersions();
    this.loadReleaseData();
  }

  loadReleaseData() {
    this.isLoading = true;
    this.currentReleases = [];
    this.camunda.getAllReleases()
      .subscribe(
        releases => {
        this.currentReleases = releases;
        this.isLoading = false;
      },
      error => {
        console.error(error)
        this.isLoading = false;
      })
  }

  onReleaseDetailEmit(release: Release) {
    this.detailedRelease = release;
    this.loadReleaseData();
  }
}

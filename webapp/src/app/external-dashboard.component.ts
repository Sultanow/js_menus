import { Component, Input, Pipe, PipeTransform, SecurityContext } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

// https://stackoverflow.com/a/38037914
@Pipe({ name: 'safe' })
export class SafePipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}
  transform(url: string) {
    var urlSanitized = this.sanitizer.sanitize(SecurityContext.URL, url);
    if (!urlSanitized) {
      throw new Error(`Sanitize failed: ${url}`)
    }

    return this.sanitizer.bypassSecurityTrustResourceUrl(urlSanitized);
  }
}

@Component({
  selector: 'external-website',
  template: `
    <div class="embed-responsive h-100">
      <iframe class="w-100 h-100 embed-responsive-item" allowfullscreen [src]="url | safe"></iframe>
    </div>
  `,
})
export class ExternalWebsite {
  @Input() url!: string;
}

// Quick fix: Pass url to component
@Component({
  template: `
  <external-website url="https://www.example.org"></external-website>
    `,
})
export class ExternalDashboard {}

import { Component, Input, Pipe, PipeTransform, SecurityContext } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

// https://stackoverflow.com/a/38037914
@Pipe({ name: 'safe' })
export class SafePipe implements PipeTransform {
	constructor(private sanitizer: DomSanitizer) {}
	transform(url: string) {
		url = this.sanitizer.sanitize(SecurityContext.URL, url);
		return this.sanitizer.bypassSecurityTrustResourceUrl(url);
	}
}

@Component({
	selector: 'external-website',
	template: `
		<iframe width="100%" height="100%" [src]="url | safe"></iframe>
	`,
})
export class ExternalWebsite {
	@Input() url: string;
}

// Quick fix: Pass url to component
@Component({
	template: `
		<external-website url="https://www.example.org"></external-website>
	`,
})
export class ExternalView {}

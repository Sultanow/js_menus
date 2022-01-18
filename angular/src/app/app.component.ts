import { Component, OnInit } from '@angular/core';
import { TimelineLite } from "gsap";
import { SettingsService } from './services/settings/settings.service';
import { Title } from '@angular/platform-browser';
import { MatDialog } from '@angular/material/dialog';

import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthenticationService } from './services/authentication/authentication.service';
import { AuthenticationComponent } from './components/authentication/authentication.component';

@Component({
	// Mark this component as obsolete for now.
	selector: 'app-root-legacy',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
	title = 'angular-radial-menu';
	// Animations
	animate_middle: TimelineLite = new TimelineLite({ paused: true });
	animate_outside: TimelineLite = new TimelineLite({ paused: true });

	//			(N)
	//			 |
	//	(W)--|--(E)
	//			 |
	//   		(S)
	animate_north: TimelineLite = new TimelineLite({ paused: true });
	animate_east: TimelineLite = new TimelineLite({ paused: true });
	animate_south: TimelineLite = new TimelineLite({ paused: true });
	animate_west: TimelineLite = new TimelineLite({ paused: true });

	// Open?
	menuOpen = false;
	outsideCircleOpen = false;

	northOpen = false;
	eastOpen = false;
	southOpen = false;
	westOpen = false;


	showStatusWarnings = true;

	showViewBox = false;
	showView = "";

	activeItems: string[] = [];

	newsOverlayOpen: boolean = false;

	ngOnInit() {
		this.clearLocalStorge();
		this.timerToClearStorge();
		this.initAnimations();
		this.initAnimationButton(this.animate_north, "north-menu");
		this.initAnimationButton(this.animate_east, "east-menu");
		this.initAnimationButton(this.animate_south, "south-menu");
		this.initAnimationButton(this.animate_west, "west-menu");
		this.settingsService.getActiveItems().subscribe(result => {
			if (result && result.activeItems && Array.isArray(result.activeItems))
				this.activeItems = result.activeItems;
		});
		this.updateTitle();
	}

	constructor(
		private authenticationService: AuthenticationService,
		private snackBar: MatSnackBar,
		private settingsService: SettingsService,
		private titleService: Title,
		public dialog: MatDialog
	) {
		if (/MSIE |Trident\//.test(window.navigator.userAgent)) { 
			window.location.replace("unsupported.html"); 
		}
	}
	updateTitle() {
		this.settingsService.getTitel().subscribe(title => {
			this.title = title;
			this.titleService.setTitle(this.title);
		});

	}


	onNotifyEventOpen(event) {
		console.log(event);
		this.showViewBox = true;
		this.showView = event;
	}

	onNotifyViewBoxClose() {
		this.showViewBox = false;
		this.showView = "";
	}

	initAnimations() {
		// Middle
		this.animate_middle
			.from(".middle-layer", 0.5, {
				transformOrigin: "70% 75%",
				scale: "0"
			}, 0)
			.from(".middle-layer .button-group", 0.75, {
				transformOrigin: "50% 50%",
				rotation: "-=135"
			}, 0)
			.from("middle-layer .button-group", 0.5, {
				opacity: "0"
			}, 0);

		// Outside
		this.animate_outside
			.from(".outside-layer", 0.5, {
				transformOrigin: "50% 50%",
				scale: "0"
			}, 0);
	}

	initAnimationButton(animate: TimelineLite, classSelector: string) {
		animate.staggerFrom("." + classSelector + " g", 0.5, {
			transformOrigin: "50% 50%",
			opacity: "0",
			scale: "0"
		}, 0.125, 0.25);
	}

	openMainMenu() {
		if (!this.menuOpen) {
			this.animate_middle.play();
			this.menuOpen = true;
		}
		else {
			this.animate_middle.reverse();
			this.menuOpen = false;

			if (this.outsideCircleOpen) {
				// reset outside
				this.animate_outside.reverse();
				this.outsideCircleOpen = false;
			}

		}

		// reset all
		this.northOpen = false;
		this.southOpen = false;
		this.westOpen = false;
		this.eastOpen = false;
	}

	openNorthMenu() {
		if (!this.northOpen) {
			if (!this.outsideCircleOpen) {
				this.animate_outside.play();
			}

			this.animate_north.play();
			this.outsideCircleOpen = true;
			this.northOpen = true;

			this.animate_west.reverse();
			this.animate_south.reverse();
			this.animate_east.reverse();
		}
		else {
			this.northOpen = false;
			this.outsideCircleOpen = false;
			this.animate_outside.reverse();
			this.animate_north.reverse();
		}

		this.westOpen = false;
		this.southOpen = false;
		this.eastOpen = false;
	}

	openEastMenu() {
		if (!this.eastOpen) {
			if (!this.outsideCircleOpen) {
				this.animate_outside.play();
			}

			this.animate_east.play();
			this.outsideCircleOpen = true;
			this.eastOpen = true;

			this.animate_north.reverse();
			this.animate_south.reverse();
			this.animate_west.reverse();
		}
		else {
			this.eastOpen = false;
			this.outsideCircleOpen = false;
			this.animate_outside.reverse();
			this.animate_east.reverse();
		}

		this.northOpen = false;
		this.southOpen = false;
		this.westOpen = false;
	}

	openSouthMenu() {
		if (!this.southOpen) {
			if (!this.outsideCircleOpen) {
				this.animate_outside.play();
			}

			this.animate_south.play();
			this.outsideCircleOpen = true;
			this.southOpen = true;

			this.animate_north.reverse();
			this.animate_east.reverse();
			this.animate_west.reverse();
		}
		else {
			this.southOpen = false;
			this.outsideCircleOpen = false;
			this.animate_outside.reverse();
			this.animate_south.reverse();
		}

		this.northOpen = false;
		this.eastOpen = false;
		this.westOpen = false;
	}

	openWestMenu() {
		if (!this.westOpen) {
			if (!this.outsideCircleOpen) {
				this.animate_outside.play();
			}

			this.animate_west.play();
			this.outsideCircleOpen = true;
			this.westOpen = true;

			this.animate_north.reverse();
			this.animate_east.reverse();
			this.animate_south.reverse();
		}
		else {
			this.westOpen = false;
			this.outsideCircleOpen = false;
			this.animate_outside.reverse();
			this.animate_west.reverse();
		}

		this.northOpen = false;
		this.eastOpen = false;
		this.southOpen = false;
	}


	openSettings() {
		if (this.showViewBox && this.showView === "settings") {
			this.showView = "";
			this.showViewBox = false;
		} else {
			this.showView = "settings";
			this.showViewBox = true;
		}
	}

	openPasswordDialog() {
		let isValid = false;
		this.authenticationService.getIsValid().subscribe(data => {
			isValid = data.toLowerCase() == "true";
			if (this.authenticationService.getToken() && isValid) {
				this.openSettings();
			}
			else {
				let dialogRef = this.dialog.open(AuthenticationComponent, {
					disableClose: true
				});
				dialogRef.afterClosed().subscribe(result => {
					if (result == null) {
						return;
					}
					if (result === true) {
						this.openSettings();
						this.snackBar.open("Anmeldung erfolgreich", "Done", { duration: 3000 });
					} else {
						this.snackBar.open("Password ist falsch", "", { duration: 3000 });
					}
				});
			}
		});
	}
	clearLocalStorge() {
		this.authenticationService.ClearLocalStorgeAfter12Hour();

	}
	timerToClearStorge() {
		setInterval(() => { this.clearLocalStorge() }, 1000 * 60 * 60 * 12);
	}

	openNewsOverlay() {
		this.newsOverlayOpen = !this.newsOverlayOpen;
	}
}

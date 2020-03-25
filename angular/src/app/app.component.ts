import { Component } from '@angular/core';
import { TimelineLite } from "gsap";
import { ConfigurationItemsService } from './services/configuration/configuration-items.service';
import { ServerConfiguration } from 'src/config/ServerConfiguration';
import { SettingsService } from './services/settings/settings.service';

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})

export class AppComponent {
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

	ngOnInit() {
		this.initAnimations();
		this.initAnimationButton(this.animate_north, "north-menu");
		this.initAnimationButton(this.animate_east, "east-menu");
		this.initAnimationButton(this.animate_south, "south-menu");
		this.initAnimationButton(this.animate_west, "west-menu");
		this.configService.getServerConfiguration(ServerConfiguration.ENV_LIST).subscribe(data => {
			this.configService.addConfiguration(this.configService.createServerConf(data));
		});
		this.settingsService.getTitel().subscribe(title => {
			this.title = title;
		})
	}

	constructor(private configService: ConfigurationItemsService, private settingsService: SettingsService) {
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
				transformOrigin: "50% 50%",
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
			this.showViewBox = false;
			this.showView = "";
		} else {
			this.showViewBox = true;
			this.showView = "settings";
		}
	}

}

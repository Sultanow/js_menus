import { Component } from '@angular/core';
import {TimelineLite} from "gsap";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'angular-radial-menu';

  // Animations
  animate_middle: TimelineLite = new TimelineLite({paused: true});
  animate_outside: TimelineLite = new TimelineLite({paused: true});
	
	//			(N)
	//			 |
	//	(W)--|--(E)
	//			 |
	//   		(S)
  animate_north: TimelineLite = new TimelineLite({paused: true});
  animate_east: TimelineLite = new TimelineLite({paused: true});
  animate_south: TimelineLite = new TimelineLite({paused: true});
  animate_west: TimelineLite = new TimelineLite({paused: true});

  // Open?
	menuOpen = false;
	outsideCircleOpen = false;
	
	northOpen = false;
	eastOpen = false;
	southOpen = false;
	westOpen = false;

	// Show Details?
	showDetails = false;

	showDependencyCharts = false;
	showCompare = false;

	showViewBox = false;

	showView = "";

  ngOnInit() { 
    this.initAnimations();
    this.initAnimationButton(this.animate_north, "north-menu");
    this.initAnimationButton(this.animate_east, "east-menu");
    this.initAnimationButton(this.animate_south, "south-menu");
    this.initAnimationButton(this.animate_west, "west-menu");    
	}
	
	onNotifyDetailsOpen(){
		this.showDetails = true;
	}

	onNotifyDetailsClose(){
		this.showDetails = false;
	}

	onNotifyDependencyChartsOpen() {
		this.showDependencyCharts = true;
	}

	onNotifyDependencyChartsClose() {
		this.showDependencyCharts = false;
	}

	onNotifyCompareClose() {
		this.showCompare = false;
	}
	
	onNotifyCompareOpen() {
		//this.showCompare = true;
		this.showViewBox = true;
		this.showView = "compare";
	}

	onNotifyStatisticOpen() {
		this.showViewBox = true;
		this.showView = "statistic";
	}

	onNotifyViewBoxClose() {
		this.showViewBox = false;
		this.showView = "";
	}

	onNotifyViewBoxOpen() {
		this.showViewBox = true;
	}

  initAnimations(){
      // Middle
      this.animate_middle
      .from(".middle-layer", 0.5, {
        transformOrigin:"50% 50%", 
        scale: "0"
      }, 0)
    
      .from(".middle-layer .button-group", 0.75, {
        transformOrigin: "50% 50%",
        rotation: "-=135"
      }, 0)
      
      .from("middle-layer .button-group", 0.5, {
        opacity: "0"
      }, 0)
    ;

    // Outside
    this.animate_outside
    .from(".outside-layer", 0.5, {
      transformOrigin:"50% 50%", 
      scale: "0"
    }, 0)
    ;
  }

  initAnimationButton(animate:TimelineLite, classSelector:String){
      animate.staggerFrom("." + classSelector +" g", 0.5, {
        transformOrigin:"50% 50%",
        opacity: "0",
        scale: "0"
      }, 0.125, 0.25)
  }

	openMainMenu(){
		if(!this.menuOpen){
			this.animate_middle.play();
			this.menuOpen = true;
		}
		else{
			this.animate_middle.reverse();
			this.menuOpen = false;

			if(this.outsideCircleOpen){
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

	openNorthMenu(){
		if(this.northOpen == false) {
			if(this.outsideCircleOpen == false) {
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

	openEastMenu(){
		if(this.eastOpen == false) {
			if(this.outsideCircleOpen == false) {
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

	openSouthMenu(){
		if(this.southOpen == false) {
			if(this.outsideCircleOpen == false) {
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

	openWestMenu(){
		if(this.westOpen == false) {
			if(this.outsideCircleOpen == false) {
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

}

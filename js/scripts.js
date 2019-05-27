// Ref: https://greensock.com/docs/TimelineLite

// Animations
// Middle layer - First layer of buttons
var animate_middle = new TimelineLite({paused: true});

animate_middle
	.from(".middle-layer", 0.5, {
		transformOrigin:"50% 50%", 
		scale: "0"
	}, 0, 0)

	.from(".middle-layer .button-group", 0.75, {
		transformOrigin: "50% 50%",
		rotation: "-=135"
	}, 0, 0)
	
	.from("middle-layer .button-group", 0.5, {
		opacity: "0"
	}, 0, 0)
;

// Outside layer - Outside layer menu background
var animate_outside = new TimelineLite({paused: true});

animate_outside
	.from(".outside-layer", 0.5, {
		transformOrigin:"50% 50%", 
		scale: "0"
	}, 0, 0)
;

// Environments Buttons
var animate_environments = new TimelineLite({paused: true});

animate_environments
	.staggerFrom(".environments-menu g", 0.5, {
		transformOrigin:"50% 50%",
		opacity: "0",
		scale: "0"
	}, 0.125, 0.25)
;

// Alerts Buttons
var animate_alert = new TimelineLite({paused: true});

animate_alert
	.staggerFrom(".alerts-menu g", 0.5, {
		transformOrigin:"50% 50%",
		opacity: "0",
		scale: "0"
	}, 0.125, 0.25)
;

// Summary Buttons
var animate_summary = new TimelineLite({paused: true});

animate_summary
	.staggerFrom(".summary-menu g", 0.5, {
		transformOrigin:"50% 50%",
		opacity: "0",
		scale: "0"
	}, 0.125, 0.25)
;

// Platforms Buttons
var animate_platforms = new TimelineLite({paused: true});

animate_platforms
	.staggerFrom(".platforms-menu g", 0.5, {
		transformOrigin:"50% 50%",
		opacity: "0",
		scale: "0"
	}, 0.125, 0.25)
;

// Interaction

// Variables
var menuOpen = false;
var outsideCircleOpen = false;
var environmentsOpen = false;
var platformsOpen = false;
var summaryOpen = false;
var alertsOpen = false;

// Main (Center) Button
$('.main-menu').click(function() {
	
	if(menuOpen == false) {
		
		$('.main-menu').addClass('is-active');
		animate_middle.play();
		menuOpen = true;
	
	} else {
		
		$('.middle-layer [class*="-button"]').removeClass('is-active');
		$('.outside-layer [class*="-button"]').removeClass('is-active');

		setTimeout(function() {
			$('.main-menu').removeClass('is-active');
		}, 750);
		
		animate_middle.reverse();
		animate_outside.reverse();

		animate_environments.reverse();
		animate_platforms.reverse();
		animate_summary.reverse();
		animate_alert.reverse();
		
		// reset all variables
		menuOpen = false;
		outsideCircleOpen = false;
		environmentsOpen = false;
		platformsOpen = false;
		summaryOpen = false;
		alertsOpen = false;
	
	}
	
});

// Environments Button
$('.environments-button').click(function() {
	
	if(environmentsOpen == false) {
		
		if(outsideCircleOpen == false) {
			animate_outside.play();
		}
		
		animate_environments.play();

		outsideCircleOpen = true;
		environmentsOpen = true;

		animate_platforms.reverse();
		animate_summary.reverse();
		animate_alert.reverse();

		$(this).addClass('is-active');
		
	} else {
		environmentsOpen = false;
		outsideCircleOpen = false;
		animate_outside.reverse();
		animate_environments.reverse();
	}

	platformsOpen = false;
	summaryOpen = false;
	alertsOpen = false;
	
});

// Platforms Button
$('.platforms-button').click(function() {

	if(platformsOpen == false) {
		
		if(outsideCircleOpen == false) {
			animate_outside.play();
		}

		animate_platforms.play()
		
		outsideCircleOpen = true;
		platformsOpen = true;

		animate_environments.reverse();
		animate_summary.reverse();
		animate_alert.reverse();

		$(this).addClass('is-active');
		
	} else {
		platformsOpen = false;
		outsideCircleOpen = false;
		animate_outside.reverse();
		animate_platforms.reverse();
	}
	
	environmentsOpen = false;
	summaryOpen = false;
	alertsOpen = false;

});

// Summary Button
$('.summary-button').click(function() {

	if(summaryOpen == false) {
		
		if(outsideCircleOpen == false) {
			animate_outside.play();
		}
		
		animate_summary.play();

		outsideCircleOpen = true;
		summaryOpen = true;
		
		animate_platforms.reverse();
		animate_environments.reverse();
		animate_alert.reverse();

		$(this).addClass('is-active');

	} else {
		summaryOpen = false;
		outsideCircleOpen = false;
		animate_outside.reverse();
		animate_summary.reverse();
	}
	
	environmentsOpen = false;
	platformsOpen = false;
	alertsOpen = false;
	
});

// Alerts Button
$('.alerts-button').click(function() {

	if(alertsOpen == false) {
		
		if(outsideCircleOpen == false) {
			animate_outside.play();
		}

		animate_alert.play();
		
		outsideCircleOpen = true;
		alertsOpen = true;

		animate_platforms.reverse();
		animate_environments.reverse();
		animate_summary.reverse();

		$(this).addClass('is-active');
		
	} else {
		alertsOpen = false;
		outsideCircleOpen = false;
		animate_outside.reverse();
		animate_alert.reverse();
	}
	
	environmentsOpen = false;
	summaryOpen = false;
	platformsOpen = false;
	
});

// Test Button
$('.test-button').click(function() {
	$('div#content div#content-body').html("Alle Batches sind durchgelaufen");
	$('div#content').show();
});

// Middle Layer Active States
$('.middle-layer [class*="-button"]').click(function(){

	$('.middle-layer [class*="-button"]').removeClass('is-active');
	$('.outside-layer [class*="-button"]').removeClass('is-active');
	$(this).addClass('is-active');

});

//Outside Layer Active States
$('.outside-layer [class*="-button"]').click(function(){

	$('.outside-layer [class*="-button"]').removeClass('is-active');
	$(this).addClass('is-active');
});

$(document).mouseup(function (e) { 
	if ($(e.target).closest("div#content").length === 0) { 
		if ($('div#content').is(":visible")) {
			$('div#content').hide();
		} 
	} 
}); 



/**
 * @author Hakim El Hattab
 */
var Radar = (function(){
		BEAT_VELOCITY = 0.01,
		BEAT_FREQUENCY = 2,
		BEAT_LIMIT = 10,

		// Distance threshold between active node and beat
		ACTIVATION_DISTANCE = 20,

		// Number of neighboring nodes to push aside on impact
		WAVE_RADIUS = 3;

	// The world dimensions
	var world = { 
		width: 400, //window.innerWidth,
		height: 400, //window.innerHeight,
		center: new Point( 200, 200 )
	};
	
	var id = 0,
		container,
		canvas,
		context,

		currentBeat = null,
		defaultBeats = [
			[ 'a', 'min' ],
            [ 'a', 'min' ]
		],

		nodes = [],
		beats = [];
	
	// Generate some scales (a, d & e)
	// Frequencies from http://www.seventhstring.com/resources/notefrequencies.html
	// Delta ratios are musical harmonies, like http://modularscale.com/
	var notes = {};
	notes.a = {
		min: [ 220.0,246.9,261.6,293.7,329.6,349.2,415.3,440.0,493.9,523.3 ],
		maj: [ 220.0,246.9,277.2,293.7,329.6,370.0,415.3,440.0,493.9,554.4 ],
		minColor: 'hsl(180, 90%, 50%)',
		majColor: 'hsl(160, 90%, 50%)'
	};

	notes.d = {
		min: generateScaleFrom( notes.a.min, 4/3 ),
		maj: generateScaleFrom( notes.a.maj, 4/3 ),
		minColor: 'hsl(140, 90%, 50%)',
		majColor: 'hsl(120, 90%, 50%)'
	};

	notes.e = {
		min: generateScaleFrom( notes.a.min, 3/2 ),
		maj: generateScaleFrom( notes.a.maj, 3/2 ),
		minColor: 'hsl(100, 90%, 50%)',
		majColor: 'hsl(80, 90%, 50%)'
	};
	
	/**
	 * 
	 */
	function initialize() {
		// Run selectors and cache element references
		container = document.getElementById( 'radar');
		canvas = document.querySelector( '#radar canvas' );

		if ( canvas && canvas.getContext ) {
			context = canvas.getContext('2d');
			context.globalCompositeOperation = 'lighter';

			addEventListeners();
			
			// Force an initial layout
			onWindowResize();
			load();
			update();
		}
		else {
			alert( 'Doesn\'t seem like your browser supports the HTML5 canvas element :(' );
		}

	}

	function addEventListeners() {
		window.addEventListener('resize', onWindowResize, false);
	}


	function load() {
        for( var i = 0, len = defaultBeats.length; i < len; i++ ) {
            addBeat( defaultBeats[i][0], defaultBeats[i][1] );
        }
	}


	function addBeat() {
		var beat = new Beat(
			world.center.x,
			world.center.y,
			beats.length
		);
		beats.push( beat );

		return beat;
	}

	function removeBeat( index ) {
		var beat = beats[ index ];

		if( beat ) {
			if( beat === currentBeat ) {
				currentBeat = null;
			}

			beats.splice( beat.index, 1 );
			beat.destroy();
		}

		updateBeats();
	}

	function updateBeats() {
		// Update indices of all beats
		for( var i = 0, len = beats.length; i < len; i++ ) {
			beats[i].changeIndex( i );
		};
	}
	
	function update() {
		clear();
		render();

		requestAnimFrame( update );
	}
	
	function clear() {
		context.clearRect( 0, 0, world.width, world.height );
	}
	
	function render() {
		// Render beats
		context.save();

		var activeBeats = 0,
			firstActiveBeatStrength = 0;

		for( var i = 0, len = beats.length; i < len; i++ ) {
			var beat = beats[i];

			updateBeat( beat );
			renderBeat( beat );

			if( beat.active ) {
				activeBeats += 1;

				if( firstActiveBeatStrength === 0 ) {
					firstActiveBeatStrength = beat.strength;
				}
			}
		}

		context.restore();

		// Trigger a new beat when needed
		if( beats.length ) {
			var nextBeat = currentBeat ? beats[ ( currentBeat.index + 1 ) % beats.length ] : null;

			if( !currentBeat ) {
				currentBeat = beats[0];
				currentBeat.activate();
			}
			else if( !nextBeat.active && activeBeats < BEAT_FREQUENCY && currentBeat.strength > 1 / BEAT_FREQUENCY ) {
				currentBeat = nextBeat;
				currentBeat.activate();
			}
		}
	}

	function updateBeat( beat ) {
		if( beat.active ) {
			beat.strength += BEAT_VELOCITY;
		}

		// Remove used up beats
		if( beat.strength > 1 ) {
			beat.deactivate();
		}
		else if( beat.active ) {
			// Check for collision with nodes
			for( var j = 0, len = nodes.length; j < len; j++ ) {
				var node = nodes[j];

				if( node.active && node.collisionLevel < beat.level ) {
					// Distance between the beat wave and node
					var distance = Math.abs( node.distanceTo( beat.x, beat.y ) - ( beat.size * beat.strength ) );

					if( distance < ACTIVATION_DISTANCE ) {
						node.collisionLevel = beat.level;
						node.play( beat.key, beat.scale );
						node.highlight( 100 );
					}
				}
			}
		}
	}

	function renderBeat( beat ) {
		if( beat.active && beat.strength > 0 ) {
			context.beginPath();
			context.arc( beat.x, beat.y, Math.max( (beat.size * beat.strength)-2, 0 ), 0, Math.PI * 2, true );
			context.lineWidth = 8;
			context.globalAlpha = 0.2 * ( 1 - beat.strength );
			context.strokeStyle = beat.color;
			context.stroke();

			context.beginPath();
			context.arc( beat.x, beat.y, beat.size * beat.strength, 0, Math.PI * 2, true );
			context.lineWidth = 2;
			context.globalAlpha = 0.8 * ( 1 - beat.strength );
			context.strokeStyle = beat.color;
			context.stroke();
		}
	}

	function generateScaleFrom(originalScale, delta) {
		var newScale = [];
		originalScale.forEach(function (freq) {
			newScale.push(freq * delta);
		});
		return newScale;
	}
	
	function onWindowResize() {
		var containerWidth = world.width + 20;

		// Resize the container
		container.style.width = containerWidth + 'px';
		container.style.height = world.height + 'px';
		container.style.left = ( window.innerWidth - world.width ) / 2 + 'px';
		container.style.top = ( window.innerHeight - world.height ) / 2 + 'px';
		
		// Resize the canvas
		canvas.width = world.width;
		canvas.height = world.height;
	}

	/**
	 * Represents a beatwave that triggers nodes.
	 */
	function Beat( x, y, index ) {
		// invoke super
		this.constructor.apply( this, arguments );
		this.changeIndex( index );
		this.level = ++id;
		this.size = Math.max( world.width, world.height ) * 0.65;
		this.active = false;
		this.strength = 0;
	};
	Beat.prototype = new Point();
	Beat.prototype.changeIndex = function( index ) {
		this.index = index;
	};
	Beat.prototype.generate = function( key, scale ) {
		this.key = key;
		this.scale = scale;

		this.color = notes[ this.key ][ scale + 'Color' ];
	};
	Beat.prototype.activate = function() {
		this.level = ++id;
		this.active = true;
		this.strength = 0;
	};
	Beat.prototype.deactivate = function() {
		this.active = false;
	};
	
	initialize();
	
})();

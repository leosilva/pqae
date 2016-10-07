// Create a custom element.
// ------------------------

joint.shapes.html = {};
joint.shapes.html.Element = joint.shapes.basic.Rect.extend({
    defaults: joint.util.deepSupplement({
        type: 'html.Element'
    }, joint.shapes.basic.Rect.prototype.defaults)
});

// Create a custom view for that element that displays an HTML div above it.
// -------------------------------------------------------------------------

joint.shapes.html.ElementView = joint.dia.ElementView.extend({

    template: [
        '<div class="html-element">',
        '<span class="timeSpan"></span>', '<br/>',
        '<p></p>',
        '<span class="infoSpan"><i class="fa fa-ellipsis-h fa-lg" aria-hidden="true"></i></span>',
        '</div>'
    ].join(''),

    initialize: function() {
        _.bindAll(this, 'updateBox');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.$box = $(_.template(this.template)());
        // Update the box position whenever the underlying model changes.
        this.model.on('change', this.updateBox, this);
        $('#zoomInButton').bind('after-click', this.updateBox);
        $('#zoomOutButton').bind('after-click', this.updateBox);
        $('#zoomToFitButton').bind('after-click', this.updateBox);

        this.updateBox();
    },
    render: function() {
        joint.dia.ElementView.prototype.render.apply(this, arguments);
        this.paper.$el.prepend(this.$box);
        this.updateBox();
        return this;
    },
    updateBox: function() {
    	var scale = V(paper.viewport).scale().sx
        // Set the position and dimension of the box so that it covers the JointJS element.
        var bbox = this.model.getBBox();
        // Example of updating the HTML with a data stored in the cell model.
        this.$box.find('.timeSpan').text(this.model.get('select'));
        //this.$box.css({ width: 100, height: 20, left: bbox.x + (bbox.width - 100), top: bbox.y, 'text-align': 'right', transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'  });
        if (scale <= 0.7) {
        	this.$box.find('.timeSpan').css({visibility : 'hidden'});	
        	this.$box.find('.infoSpan').css({visibility : 'hidden'});	
        } else {
        	this.$box.find('.timeSpan').css({visibility : 'visible'});	
        	this.$box.find('.infoSpan').css({visibility : 'visible'});
        }
        this.$box.css({ width: bbox.width * scale, height: bbox.height * scale, left: bbox.x * scale, top: bbox.y * scale, transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)' });
    }
});

function createHTMLElement(width, height, node, memberToShow) {
	var fillRect = "#FFF4DF";
	if (node.deviation == "improvement") {
		fillRect = "#99CC99";
	} else if (node.deviation == "degradation") {
		fillRect = "#FFCCCC";
	}
	
	var nodeTime = ""
	if (node.hasDeviation == false) {
		var nodeTime = ""
	} else if (node.timeVariation == null) {
		nodeTime = node.time + " ms "
	} else {
		nodeTime = node.time + " ms " + "(" + node.timeVariationSignal + " " + node.timeVariation + " ms)"
	}
	
	// Create JointJS elements and add them to the graph as usual.
	// -----------------------------------------------------------
	
	var element = new joint.shapes.html.Element({
			size: { width: width, height: height },
			select: nodeTime,
			attrs: {
	        	id: node.id,
	        	rect: { fill: fillRect },
	        	text: { text: memberToShow, fill: 'black' },
	        	root: node.node.id == null ? true : false
			}
		});
	return element;
}
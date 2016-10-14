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
        
        var popoverContent = ""
        	
        popoverContent += mountPopoverContentPackageDetails(this.model)
        popoverContent += mountPopoverContentExecutionTimeDetails(this.model)
        popoverContent += mountPopoverContentPotenciallyCausedDeviation(this.model)
        popoverContent += mountPopoverContentAddedNodes(this.model)
        popoverContent += mountPopoverContentParametersDetails(this.model)
        
        if (popoverContent == "") {
        	popoverContent = "No details."
        }
        
        this.$box.find('.infoSpan').popover({
            title: 'Details',
            trigger: 'manual',
            placement: 'auto',
            container: 'body',
            html: true,
            content: popoverContent
        }).on("mouseenter", function () {
            var _this = this;
            $(this).popover("show");
            $(".popover").on("mouseleave", function () {
                $(_this).popover('hide');
            });
        }).on("mouseleave", function () {
            var _this = this;
            setTimeout(function () {
                if (!$(".popover:hover").length) {
                    $(_this).popover("hide");
                }
            }, 100);
        });
        
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
        var bbox = this.model.getBBox();
        this.$box.find('.timeSpan').text(this.model.get('select'));
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

/**
 * Função que cria o elemento HTML que ficará por cima do nó.
 * @param width
 * @param height
 * @param node
 * @param memberToShow
 * @returns {joint.shapes.html.Element}
 */
function createHTMLElement(width, height, node, memberToShow) {
	var fillRect = defineNodeColor(node)
	var nodeTime = defineNodeTime(node)
	var element = new joint.shapes.html.Element({
			size: { width: width, height: height },
			select: nodeTime,
			node: node,
			attrs: {
	        	id: node.id,
	        	rect: { fill: fillRect },
	        	text: { text: memberToShow, fill: 'black' },
	        	root: node.isRootNode
			}
		});
	return element;
}

/**
 * Função que determina os detalhes dos nós adicionados.
 * @param popoverContent
 * @returns
 */
function mountPopoverContentAddedNodes(model) {
	var content = ""
	if (model.get('node').addedNodes.length > 0) {
		content += "<p><span class='text-bold'>Added nodes (" + model.get('node').addedNodes.length + "):</span></p>"
		content += "<ul>"
		for (var n in model.get('node').addedNodes) {
			content += "<li>" + removeMethodParams(model.get('node').addedNodes[n]) + "</li>"
		}
		content += "</ul>"
	}
	return content
}

function mountPopoverContentPackageDetails(model) {
	var content = ""
	if (model.get('node').isGroupedNode == false) {
		var node = model.get('node')
		var memberToShow = node.member;
	    if (node.member != "[...]") {
	    	var parameters = node.member.substring(node.member.indexOf('(') + 1, node.member.indexOf(')'));
	    	memberToShow = memberToShow.replace("(" + parameters + ")", '');
	    	var splitted = memberToShow.split('\.');
	    	var param = ""
	    		if (parameters != null && parameters.trim() != "") {
	    			param = "..."
	    		}
	    	// retira elementos do vetor até sobrar apenas o nome dos pacotes.
	    	for (var s in splitted) {
	    		var char = splitted.pop().charAt(0)
	    		if (char === char.toUpperCase() && char !== char.toLowerCase()) {
	    			break
	    		}
			}
	    	memberToShow = splitted.join('.')
	    }
		content = "<p><span class='text-bold'>Package:</span> " + memberToShow + "</p>"
	}
	return content
}

function mountPopoverContentParametersDetails(model) {
    var memberToShow = "";
    var node = model.get('node');
    if (node.member != "[...]") {
    	var params = node.member.substring(node.member.indexOf('(') + 1, node.member.indexOf(')')).split(",");
    	if (params.length > 0 && params != "") {
    		memberToShow += "<p><span class='text-bold'>Parameters (" + params.length + "):</span> </p>"
    		memberToShow += "<ul>"
			for (var p in params) {
				memberToShow += "<li>" + params[p] + "</li>"
			}
    		memberToShow += "</ul>"
    	} else {
    		memberToShow += "<p><span class='text-bold'>No parameters.</span></p>"
    	}
    }
    return memberToShow
}

function mountPopoverContentPotenciallyCausedDeviation(model) {
	var node = model.get('node');
	var content = ""
	if (node.hasDeviation == true && node.isAddedNode == true) {
		content += "<p><span style='color: red; font-style: italic; font-weight: bold;'>This node potentially caused the performance deviation.<span></p>"
	}
	return content
}

function mountPopoverContentExecutionTimeDetails(model) {
	var node = model.get('node');
	var content = ""
	if (node.hasDeviation == true) {
		content += "<p>"
		if (node.previousExecutionTime == null) {
			content += "<span class='text-bold'>Previous version time:</span> 0 ms<br/>"
		} else {
			content += "<span class='text-bold'>Previous version time:</span> " + node.previousExecutionTime + " ms<br/>"
		}
		content += "<span class='text-bold'>Next version time:</span> " + node.nextExecutionTime + " ms<br/>"
		content += "<span class='text-bold'>Deviation:</span> " + node.timeVariationSignal + node.timeVariation + " ms"
		content += "<p>"
	}
	return content
}

/**
 * Função que determina a cor do nó.
 * @param node
 * @returns {String}
 */
function defineNodeColor(node) {
	var fillRect = "#FFF4DF";
	if (node.deviation == "optimization") {
		fillRect = "#99CC99";
	} else if (node.deviation == "degradation") {
		fillRect = "#FFCCCC";
	}
	if (node.addedNodes.length > 0 || node.isAddedNode) {
		fillRect = "orange";
	}
	return fillRect
}

/**
 * Função que determina o tempo do nó que será apresentado em tela.
 * @param node
 */
function defineNodeTime(node) {
	var nodeTime = ""
	if (node.hasDeviation == false && node.isGroupedNode == false) {
		nodeTime = node.time + " ms"
	} else if (node.timeVariation == null && node.isGroupedNode == false) {
		nodeTime = node.time + " ms "
	} else if (node.hasDeviation == true) {
		nodeTime = node.nextExecutionTime + " ms " + "(" + node.timeVariationSignal + " " + node.timeVariation + " ms)"
	}
	return nodeTime
}
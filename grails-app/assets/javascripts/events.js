function bindZoomButtons() {
	$('#zoomInButton').on('click', function() {
		doZoom(0.1)
	    $('#zoomInButton').trigger('after-click');
	});
	$('#zoomOutButton').on('click', function() {
		doZoom(-0.1)
		$('#zoomOutButton').trigger('after-click');
	});
	$('#zoomToFitButton').on('click', function() {
		doZoom(1.0)
		$('#zoomToFitButton').trigger('after-click');
	});
}

function doZoom(z) {
	var newScale
	
	newScale = V(paper.viewport).scale().sx + z; // the current paper scale changed by delta
	if (z == 1.0) {
		newScale = z
	}
	
    if (newScale > 0.4 && newScale <= 1.5) {
        paper.scale(newScale, newScale);
        paper.setOrigin(0, 0); // reset the previous viewport translation
    }
    
    if (newScale > 1.0) {
    	calculatePaperDimensionsByScale(z)
    } else if (newScale == 1.0) {
    	calculatePaperDimensions()
    }
}

function bindOnDoubleClick(element) {
	element.on("dblclick", function() {
		var id = element[0].attributes[1].nodeValue
		var svgElmt = $("[model-id=" + id + "]")
		var nodesToHighlight = []
		$.each(graph.getElements(), function(i, e) {
			if (e.id == id) {
				nodesToHighlight = addNodesToHighlight(nodesToHighlight, e.attributes.attrs.id)
			}
		});
		var modelIdsToHightLight = []
		var dataIdsToHightLight = []
		var linksToHighlight = []
		$.each(nodesToHighlight, function(i, e) {
			modelIdsToHightLight.push("[model-id=" + e.id +"]")
			dataIdsToHightLight.push("[data-id=" + e.id +"]")
			$.each(graph.getCells(), function(ind, c) {
				if (c.attributes.type == "link") {
					if (c.attributes.source.id == e.id && c.attributes.target.id == nodesToHighlight[i+1].id) {
						linksToHighlight.push(c)
					}
				}
			});
		});
		resetHighlight()
		doHighlight(modelIdsToHightLight, linksToHighlight, dataIdsToHightLight)
	})
}

function addNodesToHighlight(nodesToHighlight, id) {
	$.each(graph.getElements(), function(i, e) {
		if (e.attributes.attrs.id == id) {
			nodesToHighlight.push(e)
			addNodesToHighlight(nodesToHighlight, e.attributes.node.node.id)
		}
	});
	return nodesToHighlight
}

function resetHighlight() {
	$("#paperNextVersion").find("svg").children().children().css("opacity", "")
	$("#paperNextVersion").children().not("svg").css("opacity", "")
}

function doHighlight(modelIdsToHightLight, linksToHighlight, dataIdsToHightLight) {
	$("#paperNextVersion").find("svg").children().children().not(modelIdsToHightLight.join(",")).css("opacity", "0.2")
	$.each(linksToHighlight, function(i, e) {
		$("[model-id=" + e.id + "]").css("opacity", "1.0")
	});
	$("#paperNextVersion").children().not("svg").not(dataIdsToHightLight.join(",")).css("opacity", "0.2")	
}

function bindClearHighlight(element) {
	element.on("dblclick", function(){
		resetHighlight();
	})	
}
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
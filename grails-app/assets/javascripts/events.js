function bindZoomButtons(paperScroller) {
	$('#centerButton').on('click', _.bind(paperScroller.center, paperScroller));
	$('#centerContentButton').on('click', _.bind(paperScroller.centerContent, paperScroller));

	$('#zoomInButton').on('click', function() {
		paperScroller.zoom(0.1, { max: 1 });
	    $('#zoomInButton').trigger('after-click');
	});
	$('#zoomOutButton').on('click', function() {
		paperScroller.zoom(-0.1, { min: 0.1 });
		$('#zoomOutButton').trigger('after-click');
	});
	$('#zoomToFitButton').on('click', function() {
		paperScroller.zoomToFit({
			minScale: 0.1,
			maxScale: 1
		});
		$('#zoomToFitButton').trigger('after-click');
	});
	
}

function bindTipoExibicao() {
	$('#tipoExibicao').on("change", function(evt) {
		removeAttributesAndMethodsFromElements()
		addAttributesOrMethodsToElements(evt.currentTarget.value)
		directedGraphLayout()
	});
}
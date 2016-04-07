function bindZoomButtons() {
	$('#centerButton').on('click', _.bind(paperScroller.center, paperScroller));
	$('#centerContentButton').on('click', _.bind(paperScroller.centerContent, paperScroller));

	$('#zoomInButton').on('click', function() {
		paperScroller.zoom(0.2, { max: 2 });
	});
	$('#zoomOutButton').on('click', function() {
		paperScroller.zoom(-0.2, { min: 0.2 });
	});
	$('#zoomToFitButton').on('click', function() {
		paperScroller.zoomToFit({
			minScale: 0.2,
			maxScale: 2
		});
	});
	
}

function bindTipoExibicao() {
	$('#tipoExibicao').on("change", function(evt) {
		removeAttributesAndMethodsFromElements()
		addAttributesOrMethodsToElements(evt.currentTarget.value)
		directedGraphLayout()
	});
}
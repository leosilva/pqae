/**
 * 
 */

var classes = {}
var relations = []

$(document).ready(function() {
	var value = $.parseJSON($("#mapClasses").val())
	drawPaper()
	$.each(value.classes, function (key, val) {
		classes[val.content.pack + "." + val.content.name] = drawElement(val);
	});
	addElementsToPaper(classes)
	$.each(value.relations, function (key, val) {
		relations.push(drawRelation(val[key]));
	});
	addElementsToPaper(relations)

	var cells = graph.getCells();
	graph.resetCells(cells);

	//treeLayout(graph, cells);
	directedGraphLayout(graph);

	bindButtons();

});

function bindButtons() {
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
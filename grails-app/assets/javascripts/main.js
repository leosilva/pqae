/**
 * 
 */

var classes = {}
var relations = []

$(document).ready(function() {
	clearGraphAndPaper()
	drawPaper()
	
	drawClasses()
	drawRelations()

	//treeLayout(graph, cells);
	directedGraphLayout();
	bindEvents()
});

function drawClasses() {
	var value = $.parseJSON($("#mapClasses").val())
	$.each(value.classes, function (key, val) {
		classes[val.content.pack + "." + val.content.name] = drawElement(val);
	});
	addElementsToPaper(classes)
}

function drawRelations() {
	var value = $.parseJSON($("#mapClasses").val())
	$.each(value.relations, function (key, val) {
		relations.push(drawRelation(val[key]));
	});
	addElementsToPaper(relations)
}

function bindEvents() {
	bindTipoExibicao()
	bindZoomButtons()
}
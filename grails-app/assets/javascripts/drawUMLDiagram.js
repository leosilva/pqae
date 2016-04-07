/**
 * Arquivo responsável por preparar a área de desenho para o diagrama de classes UML.
 */

var graph
var paper
var uml
var paperScroller

function drawPaper() {
    graph = new joint.dia.Graph();

    paper = new joint.dia.Paper({
        width: $("body").width() + "px",
        height: "600px",
        gridSize: 1,
        model: graph
    });

    uml = joint.shapes.uml;

    paperScroller = new joint.ui.PaperScroller({
        autoResizePaper: true,
        padding: 50,
        paper: paper
    });

    // Initiate panning when the user grabs the blank area of the paper.
    paper.on('blank:pointerdown', paperScroller.startPanning);

    $("#paper").append(paperScroller.render().el);

    // Example of centering the paper.
    paperScroller.center();
}

function addElementsToPaper(elements) {
    _.each(elements, function(c) {
        graph.addCell(c);
    });
}

function clearGraphAndPaper() {
	if (graph) {
		graph.get('cells').forEach(function (cell) {
			var view = paper.findViewByModel(cell)
			view.remove();
		});
		
		graph.clear()
		paper.remove();
		paper = null;
		$(".paper-scroller").remove()
	}
}

function addAttributesToElements() {
	graph.get('cells').forEach(function (cell) {
		// se existe um nome, então é uma classe...
		if (cell.attributes.name) {
			var value = $.parseJSON($("#mapClasses").val())
			$.each(value.classes, function (keyClazz, clazz) {
				// compara o nome das classes do JSON e das células do diagrama
				if (cell.attributes.name == clazz.content.name) {
					$.each(clazz.content.attributes, function (keyAttr, attr) {
						cell.attributes.attributes.push(attr.name + ": " + attr.type)
				    })
				}
			});
			cell.updateRectangles()
			cell.trigger('uml-update')
		}
	});
}
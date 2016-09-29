/**
 * 
 */

var fullNodes = {
		0 : nodesPreviousVersion = [],
		1 :	nodesNextVersion = []
}
//var mapScenarios = ['mapPreviousVersionNodes', 'mapAffectedNodes']
//var divIds = ['paperPreviousVersion' , 'paperNextVersion']
var graph
var paper
var paperScroller

$(document).ready(function() {
	//clearGraphAndPaper()
	drawPaper('paperNextVersion')
	
	drawCallGraph('mapAffectedNodes', fullNodes[1])
	
	addElementsToGraph(fullNodes[1], graph)
	
	//treeLayout(graph);
	directedGraphLayout(graph);
	centerPaperToRootNode(graph, paperScroller);

	// remove divs soltas criadas para colocar o tempo de execucao de cada no
	$("div[class='html-element'][style*='left: 0px; top: 0px;'").remove();
	
});

function drawPaper(divId) {
//	console.log("Entrou drawPaper()");
	var startTime = new Date();

	graph = new joint.dia.Graph();

    paper = new joint.dia.Paper({
        width: $("body").width() + "px",
        height: "500px",
        gridSize: 1,
        model: graph
    });

    paperScroller = new joint.ui.PaperScroller({
        autoResizePaper: true,
        paper: paper
    });

    // Initiate panning when the user grabs the blank area of the paper.
    paper.on('blank:pointerdown', paperScroller.startPanning);

    $("#" + divId).append(paperScroller.render().el);

    // Example of centering the paper.
    //paperScroller.center();
    
//    graphs.push(graph);
//    papers.push(paper);
    //paperScroller.zoom(-0.4, { min: 0.2 });
    //paperScrollers.push(paperScroller);
//    console.log("Saiu drawPaper()");
    var duration = (new Date() - startTime) / 1000;
//    console.log("Duração: " + duration + 's');
}

/**
 * Função que adiciona elementos (diagramas e associações) ao paper
 * @param elements
 */
function addElementsToGraph(elements, graph) {
//	console.log("Entrou addElementsToGraph()");
	var startTime = new Date();
	graph.resetCells(elements);
//	console.log("Saiu addElementsToGraph()");
    var duration = (new Date() - startTime) / 1000;
//    console.log("Duração: " + duration + 's');
}

function drawCallGraph(idMapScenario, nodes) {
//	console.log("Entrou drawCallGraph()");
	var startTime = new Date();
	var value = $.parseJSON($("#" + idMapScenario).val())
	$.each(value.nodes, function (key, val) {
		drawCallGraphNode(val, nodes)
	});
	$.each(value.nodes, function (key, val) {
		drawCallGraphLinks(val, nodes)
	});
//	console.log("Saiu drawCallGraph()");
    var duration = (new Date() - startTime) / 1000;
//    console.log("Duração: " + duration + 's');
}

function drawCallGraphNode(node, nodes) {
//	console.log("Entrou drawCallGraphNode()");
	var startTime = new Date();
	/*
     * As proximas linhas montam a string que será exibida dentro do box da visualização.
     * O formato é: <nome da classe>.<metodo>
     * Foi retirado o nome do pacote da exibição. Se o usuário quiser saber o nome completo da classe
     * deverá acessar o tooltip.
     */
    var memberToShow = node.member;
    if (node.member != "[...]") {
    	var parameters = node.member.substring(node.member.indexOf('(') + 1, node.member.indexOf(')'));
    	memberToShow = memberToShow.replace("(" + parameters + ")", '');
    	var splitted = memberToShow.split('\.');
    	var param = ""
    		if (parameters != null && parameters.trim() != "") {
    			param = "..."
    		}
    	memberToShow = splitted[splitted.length - 2] + "." + splitted[splitted.length - 1] + "(" + param + ")";
    }
	
	var maxLineLength = _.max(memberToShow.split('\n'), function(l) { return l.length; }).length;

    // Compute width/height of the rectangle based on the number 
    // of lines in the label and the letter size. 0.6 * letterSize is
    // an approximation of the monospace font letter width.
    var letterSize = 8;
    var width = 2.3 * (letterSize * (0.45 * maxLineLength + 1));
    var height = 3.3 * ((memberToShow.split('x').length + 1) * letterSize);
    
    if ((node.id == null || node.id == "") && (node.tempId != null || node.tempId != "")) {
    	node.id = node.tempId
    }
    
    var rect = createHTMLElement(width, height, node, memberToShow);
    
	nodes.push(rect);
//	console.log("Saiu drawCallGraphNode()");
    var duration = (new Date() - startTime) / 1000;
//    console.log("Duração: " + duration + 's');
}

function drawCallGraphLinks(node, nodes) {
//	console.log("Entrou drawCallGraphLinks()");
	var startTime = new Date();
	// acha o nó raiz da iteração na lista de objetos do JointJS
	var rootNodeRect
	$.each(nodes, function (k, nRect) {
		if (nRect.attributes.type == "html.Element") {
			if (node.id == nRect.attributes.attrs.id) {
				rootNodeRect = nRect
			}
		}
	});
	
	var nodesClone = nodes
	
	// acha cada nó que faz ligação com o nó raiz na lista de objetos do JointJS e cria o link 
	$.each(node.nodes, function (key, nJson) {
		$.each(nodesClone, function (k, nRect) {
			if (nRect.attributes.type == "html.Element") {
				if (nJson.id == nRect.attributes.attrs.id) {
					var link = new joint.dia.Link({
						source: { id: nRect.id },
						target: { id: rootNodeRect.id },
						attrs: {
							'.marker-source': { fill: 'black', d: 'M 10 0 L 0 5 L 10 10 z' }
						}
					});
					nodes.push(link)
				}
			}
		});
	});
//	console.log("Saiu drawCallGraphLinks()");
    var duration = (new Date() - startTime) / 1000;
//    console.log("Duração: " + duration + 's');
}

function centerPaperToRootNode(graph, paperScroller) {
//	console.log("Entrou centerPaperToRootNode()");
	var startTime = new Date();
	if (graph) {
		graph.get('cells').forEach(function (cell) {
			if (cell.attributes.type == "html.Element" && cell.attributes.attrs.root == true) {
				paperScroller.scrollToElement(cell);
			}
		});
	}
//	console.log("Saiu centerPaperToRootNode()");
    var duration = (new Date() - startTime) / 1000;
//    console.log("Duração: " + duration + 's');
}
/**
 * 
 */


var nodesNextVersion = []
var graph
var paper
//var paperScroller
var width
var height
var padding

$(document).ready(function() {
	width = ($("body").width()) - (($("body").width() * 10) / 100)
	height = ($("body").height()) - (($("body").height() * 38) / 100)
	padding = parseInt(findProperty('.evolve-paper-graph', 'padding').replace("px", ""));
	drawPaper('paperNextVersion')
	
	drawCallGraph('mapAffectedNodes', nodesNextVersion)
	
	addElementsToGraph(nodesNextVersion, graph)
	
	//treeLayout(graph);
	directedGraphLayout(graph);
	
	//centerPaperToRootNode(graph, paperScroller);
	
	// realiza o bind dos eventos do zoom
	bindZoomButtons();

	// remove divs soltas criadas para colocar o tempo de execucao de cada no
	//$("div[class='html-element'][style*='left: 0px; top: 0px;'").remove();
	
	// desabilita interação com os elementos. Isso evita que o usuario apague links sem querer ou altere o layout.
	//paper.$el.css('pointer-events', 'none');
	
	// coloca o cursor normal do mouse
	paper.$el.css('cursor', 'auto');
	
	$("#evolvePaper").css("height", height)
	
});

$(window).load(function() {
    // this code will run after all other $(document).ready() scripts
    // have completely finished, AND all page elements are fully loaded.
	$("[data-toggle='offcanvas']").click()
	calculatePaperDimensions()
});

function drawPaper(divId) {
	graph = new joint.dia.Graph();
	
    paper = new joint.dia.Paper({
    	el: $("#" + divId),
        gridSize: 1,
        model: graph
    });
    
//    paperScroller = new joint.ui.PaperScroller({
//        autoResizePaper: true,
//        paper: paper
//    });

    // Initiate panning when the user grabs the blank area of the paper.
    //paper.on('blank:pointerdown', paperScroller.startPanning);

    //$("#" + divId).append(paperScroller.render().el);
}

/**
 * Função que adiciona elementos (diagramas e associações) ao paper
 * @param elements
 */
function addElementsToGraph(elements, graph) {
	graph.resetCells(elements);
}

function drawCallGraph(idMapScenario, nodes) {
	var value = $.parseJSON($("#" + idMapScenario).val())
	$.each(value.nodes, function (key, val) {
		drawCallGraphNode(val, nodes)
	});
	$.each(value.nodes, function (key, val) {
		drawCallGraphLinks(val, nodes)
	});
}

function drawCallGraphNode(node, nodes) {
	var memberToShow = removeMethodParams(node)

	var maxLineLength = _.max(memberToShow.split('\n'), function(l) { return l.length; }).length;

    // Compute width/height of the rectangle based on the number 
    // of lines in the label and the letter size. 0.6 * letterSize is
    // an approximation of the monospace font letter width.
    var letterSize = 8;
    var width = 2.3 * (letterSize * (0.45 * maxLineLength + 1));
    var height = 3.3 * ((memberToShow.split('x').length + 1) * letterSize);
    
    var rect = createHTMLElement(width, height, node, memberToShow);
    
	nodes.push(rect);
}

function drawCallGraphLinks(node, nodes) {
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
							'.marker-source': { fill: 'black', d: 'M 10 0 L 0 5 L 10 10 z' },
							'.marker-vertices': { display : 'none' },
				            '.marker-arrowheads': { display: 'none' },
				            '.connection-wrap': { display: 'none' },
				            '.link-tools': { display : 'none' }
						}
						//smooth: true
					});
					nodes.push(link)
				}
			}
		});
	});
}

function centerPaperToRootNode(graph, paperScroller) {
	if (graph) {
		graph.get('cells').forEach(function (cell) {
			if (cell.attributes.type == "html.Element" && cell.attributes.attrs.root == true) {
				paperScroller.scrollToElement(cell);
			}
		});
	}
}

function removeMethodParams(node) {
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
    	var splitted = memberToShow.split(".");
    	aux = []
    	memberToShow = ""
    	var param = ""
		if (parameters != null && parameters.trim() != "") {
			param = "..."
		}
    	for (var s in splitted) {
    		var popped = splitted.pop()
    		if (popped != null) {
    			var char = popped.charAt(0)
    			if (char === char.toUpperCase() && char !== char.toLowerCase()) {
    				aux.push(popped)
    				break
    			}
    			aux.push(popped)
    		}
		}
    	aux.reverse()
    	for (var m in aux) {
    		memberToShow += aux[m]
    		memberToShow += "."
    	}
    	memberToShow = memberToShow.slice(0, -1)
    	memberToShow += "(" + param + ")";
    }
    return memberToShow
}

function calculatePaperDimensions() {
	var maiorY = 0
	var maiorX = 0
	var elementHeight = 0
	var elementWidth = 0
	$.each(graph.getElements(), function(i, e) {
		if (e.attributes.position.y > maiorY) {
			maiorY = e.attributes.position.y
			elementHeight = e.attributes.size.height
		}
		if (e.attributes.position.x > maiorX) {
			maiorX = e.attributes.position.x
			elementWidth = e.attributes.size.width
		}
	})
	$(paper.svg).attr("height", maiorY + elementHeight)
	$(paper.svg).attr("width", maiorX + elementWidth + padding)
	paper.setDimensions(width + elementWidth + padding, maiorY + elementHeight)
}

function calculatePaperDimensionsByScale(scale) {
	$(paper.svg).attr("height", $(paper.svg).height() + ($(paper.svg).height() * scale))
	$(paper.svg).attr("width", $(paper.svg).width() + ($(paper.svg).width() * scale))
	paper.setDimensions($(paper.svg).width() + ($(paper.svg).width() * scale) + padding, $(paper.svg).height() + ($(paper.svg).height() * scale))
}
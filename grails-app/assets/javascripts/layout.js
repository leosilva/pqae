/**
 * Função que organiza os elementos no layout de árvore.
 * @param graph
 * @param cell
 */

var graphLayout

function treeLayout(graph) {
	var cells = graph.getCells();
	graph.resetCells(cells);

    graphLayout = new joint.layout.TreeLayout({
        graph: graph,
        direction: "T"
    });

    // root position stays the same after the layout
    var root
    graph.getElements().forEach(function (e) {
		if (e.attributes.type == "html.Element" && e.attributes.attrs.root == true) {
			console.log(e);
			root = e.position(600, 200);
		}
	});
//    var root = graph.getElements()[0].position(500, 500);

    graphLayout.layout();
};

/**
 * Função que organiza os elementos no layout de grafo direcionado.
 * @param graph
 */
function directedGraphLayout(graph) {
	var cells = graph.getCells();
	graph.resetCells(cells);
    joint.layout.DirectedGraph.layout(graph, { rankDir: "BT", setLinkVertices: false });
}
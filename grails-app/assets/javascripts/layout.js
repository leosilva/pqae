/**
 * Função que organiza os elementos no layout de árvore.
 * @param graph
 * @param cell
 */
function treeLayout() {
	var cells = graph.getCells();
	//graph.resetCells(cells);

    var graphLayout = new joint.layout.TreeLayout({
        graph: graph,
        gap: 50,
        siblingGap: 50,
        direction: "T"
    });

    // root position stays the same after the layout
    var root = cells[0].position(200, 200);

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
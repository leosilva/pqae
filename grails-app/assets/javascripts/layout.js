/**
 * Função que organiza os elementos no layout de árvore.
 * @param graph
 * @param cell
 */
function treeLayout() {
	var cells = graph.getCells();
	graph.resetCells(cells);

    var graphLayout = new joint.layout.TreeLayout({
        graph: graph,
        gap: 30,
        siblingGap: 30,
        direction: "T"
    });

    // root position stays the same after the layout
    var root = cells[0].position(50, 100);

    graphLayout.layout();

};

/**
 * Função que organiza os elementos no layout de grafo direcionado.
 * @param graph
 */
function directedGraphLayout() {
	var cells = graph.getCells();
	graph.resetCells(cells);
    joint.layout.DirectedGraph.layout(graph, { setLinkVertices: false });
}
package architecturevisualization

import grails.transaction.Transactional

import comparator.NodeComparator

@Transactional
class ProjectService {

	/**
	 * Este método busca os métodos que tiveram variação de desempenho. São consideradas aqui quaisquer variações.
	 * @param nodesPV
	 * @param nodesNV
	 * @param nodesToVisualization
	 * @return
	 */
    def searchMethodsWithDeviation(List<Node> nodesPV, List<Node> nodesNV, HashSet<Node> nodesToVisualization) {
		Comparator comp = new NodeComparator();
		Collections.sort(nodesNV, comp)
		for (Node pv : nodesPV) {
			int index = Collections.binarySearch(nodesNV, pv, comp);
			if (index > 0) {
				Node node = nodesNV.get(index)
				if (node.time < pv.time) {
					node.deviation = "improvement"
					node.timeVariation = pv.time - node.time
					node.timeVariationSignal = "-"
					node.hasDeviation = true
					nodesToVisualization << node
				} else if (node.time > pv.time) {
					node.deviation = "degradation"
					node.timeVariation = node.time - pv.time
					node.timeVariationSignal = "+"
					node.hasDeviation = true
					nodesToVisualization << node
				}
			}
		}
		nodesToVisualization
	}
	
	/**
	 * Método que busca pelo nó raiz na lista de nós. Se houver visualização, o nó raiz sempre aparecerá.
	 * @param nodesNV
	 * @param nodesToVisualization
	 * @return
	 */
	def searchRootNode(nodesNV, nodesToVisualization) {
		nodesToVisualization << nodesNV.find { it?.node == null }
		nodesToVisualization
	}
	
	/**
	 * Método que determina os nós pai dos nós a serem mostrados, mas apenas para aqueles que não possuem pais. São criados nós de agrupamento para serem os pais.
	 * @param nodesWithoutParent
	 * @param nodesToVisualization
	 * @param groupedNodes
	 * @return
	 */
	def defineGrupedBlocksToParents(HashSet<Node> nodesWithoutParent, HashSet<Node> nodesToVisualization, HashSet<Node> groupedNodes) {
		nodesWithoutParent.each { nwp ->
			def tempNode = nwp
			while (tempNode?.node != null) {
				nodesToVisualization.each { an ->
					if (tempNode?.node?.id == an.id) {
						def node = new Node(member : "[...]", nodes : [])
						node.node = an
						node.hasDeviation = false
						node.isGroupedNode = true
						an.nodes << node
						node.id = (9999999 + 99999999*Math.random()).round()
						node.nodes << nwp
						nwp.node = node
						groupedNodes << node
						tempNode = null
						return
					}
				}
				tempNode = tempNode?.node
			}
		}
		groupedNodes
	}
	
	/**
	 * Método que cria os agrupamentos para os nós filhos que não são mostrados na apresentação.
	 * @param nodesToVisualization
	 * @param groupedNodes
	 * @return
	 */
	def defineGrupedBlocksToChildren(HashSet<Node> nodesToVisualization, HashSet<Node> groupedNodes) {
		for (Node n : nodesToVisualization) {
			// verifica se tem filhos sem variacao
			def hasChildToVisualization = false
			hasChildToVisualization = n.nodes.any { it.hasDeviation == true || it.isGroupedNode == true }
			if (!hasChildToVisualization) {
				def node = new Node(member : "[...]", nodes : [])
				node.id = (9999999 + 99999999*Math.random()).round()
				node.node = n
				node.hasDeviation = false
				node.isGroupedNode = true
				n.nodes << node
				groupedNodes << node
			}
		}
		groupedNodes
	}
	
	/**
	 * Método que calcula o tempo total do cenário.
	 * @param nodesToVisualization
	 * @return
	 */
	def calculateScenarioTime(HashSet<Node> nodesToVisualization) {
		def time = 0
		for (Node n : nodesToVisualization) {
			time = time + ((n.timeVariationSignal + n.timeVariation) as Long)
		}
		time
	}
	
	/**
	 * Método que coleta informações sobre os nós adicionados na nova versão do sistema.
	 * @param groupedNodes
	 * @param addedNodes
	 * @return
	 */
	def collectInfoAddedNodes(HashSet<Node> groupedNodes, HashSet<Node> addedNodes, HashSet<Node> nodesToVisualization) {
		addedNodes.each { an ->
			def tempNode = an
			while (tempNode != null) {
				def siblingNode = groupedNodes.find { tempNode?.node?.id == it?.node?.id }
				if (siblingNode) {
					siblingNode?.addedNodes << an
					return
				} else {
					siblingNode = nodesToVisualization.find { tempNode?.node?.id == it?.node?.id }
					if (siblingNode) {
						def groupedNode = siblingNode.nodes.find { it.isGroupedNode }
						if (groupedNode) {
							groupedNode.addedNodes << an
							return
						} else {
							def node = new Node(member : "[...]", nodes : [])
							node.node = siblingNode
							node.hasDeviation = false
							node.isGroupedNode = true
							node.id = (9999999 + 99999999*Math.random()).round()
							node.addedNodes << an
							siblingNode.nodes << node
							groupedNodes << node
							return
						}
					}
				}
				tempNode = tempNode?.node
			}
		}
		groupedNodes
	}
	
	/**
	 * Método que determina os nós que foram adicionados na versão mais recente do sistema.
	 * @param addedNodes
	 * @param nodesPV
	 * @param nodesNV
	 * @return
	 */
	def determineAddedNodes(addedNodes, nodesPV, nodesNV) {
		Comparator comp = new NodeComparator();
		Collections.sort(nodesPV, comp)
		
		for (Node nv : nodesNV) {
			int index = Collections.binarySearch(nodesPV, nv, comp);
			if (index <= 0) {
				if (!addedNodes.find { it.member == nv.member }) {
					addedNodes << nv
				}
			}
		}
		addedNodes
	}
	
	/**
	 * Método que determina os nós que foram removidos na versão mais recente do sistema.
	 * @param removedNodes
	 * @param nodesPV
	 * @param nodesNV
	 * @return
	 */
	def determineRemovedNodes(removedNodes, nodesPV, nodesNV) {
		Comparator comp = new NodeComparator();
		Collections.sort(nodesNV, comp)
		
		for (Node pv : nodesPV) {
			int index = Collections.binarySearch(nodesNV, pv, comp);
			if (index <= 0) {
				if (!removedNodes.find { it.member == pv.member }) {
					removedNodes << pv
				}
			}
		}
		removedNodes
	}
}

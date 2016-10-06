package architecturevisualization

import java.util.HashSet;
import java.util.List;

import grails.transaction.Transactional

@Transactional
class ProjectService {

	/**
	 * Este método busca os métodos que tiveram variação de desempenho. São consideradas aqui quaisquer variações.
	 * @param nodesPV
	 * @param nodesNV
	 * @param nodesToVisualization
	 * @return
	 */
    def searchMethodsWithDeviation(nodesPV, nodesNV, nodesToVisualization) {
		nodesPV.each { pv ->
			def node = nodesNV.find { nv ->
				nv.member == pv.member
			}
			if (node) {
				if (node.time < pv.time) {
					node.deviation = "improvement"
					node.timeVariation = pv.time - node.time
					node.timeVariationSignal = "-"
					nodesToVisualization << node
				} else if (node.time > pv.time) {
					node.deviation = "degradation"
					node.timeVariation = node.time - pv.time
					node.timeVariationSignal = "+"
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
	def defineGrupedBlocksToParents(HashSet nodesWithoutParent, HashSet nodesToVisualization, List groupedNodes) {
		nodesWithoutParent.each { nwp ->
			def tempNode = nwp
			while (tempNode?.node != null) {
				nodesToVisualization.each { an ->
					if (tempNode?.node?.id == an.id) {
						def node = new Node(member : "[...]", nodes : [])
						node.node = an
						an.nodes << node
						node.tempId = (9999999 + 99999999*Math.random()).round()
						node.id = node.tempId
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
	def defineGrupedBlocksToChildren(HashSet nodesToVisualization, List groupedNodes) {
		nodesToVisualization.each { n ->
			// verifica se tem filhos sem variacao
			def hasChildToVisualization = false
			n.nodes.each { child ->
				if (nodesToVisualization.contains(child)) {
					hasChildToVisualization = true
				}
			}
			if (!hasChildToVisualization) {
				def node = new Node(member : "[...]", nodes : [])
				node.tempId = (9999999 + 99999999*Math.random()).round()
				node.id = node.tempId
				node.node = n
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
	def calculateScenarioTime(nodesToVisualization) {
		def time = 0
		nodesToVisualization.each {
			time = time + ((it.timeVariationSignal + it.timeVariation) as Long)
		}
		time
	}
}

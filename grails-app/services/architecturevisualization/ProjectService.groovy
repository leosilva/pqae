package architecturevisualization

import java.util.HashSet;
import java.util.List;

import grails.transaction.Transactional

@Transactional
class ProjectService {

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
	
	def searchRootNode(nodesNV, nodesToVisualization) {
		nodesToVisualization << nodesNV.find { it?.node == null }
		nodesToVisualization
	}
	
	def defineGrupedBlocksToParents(HashSet nodesWithoutParent, HashSet nodesToVisualization, List agruppedNodes) {
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
						agruppedNodes << node
						tempNode = null
						return
					}
				}
				tempNode = tempNode?.node
			}
		}
		agruppedNodes
	}
	
	def defineGrupedBlocksToChildren(HashSet nodesToVisualization, List agruppedNodes) {
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
				agruppedNodes << node
			}
		}
		agruppedNodes
	}
	
	def calculateScenarioTime(nodesToVisualization) {
		def time = 0
		nodesToVisualization.each {
			time = time + ((it.timeVariationSignal + it.timeVariation) as Long)
		}
		time
	}
}

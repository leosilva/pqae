package architecturevisualization

import grails.transaction.Transactional
import groovy.time.TimeCategory

import java.math.RoundingMode

import org.springframework.transaction.annotation.Propagation

import comparator.NodeComparator

import domain.BlamedScenario

@Transactional
class CallGraphVisualizationService {

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
					node.deviation = "optimization"
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
	 * Método que determina os nós com variação de desempenho baseados no arquivo interpretado.
	 * @param nodesToVisualization
	 * @param fileBlamed
	 * @param nodesNV
	 * @param scenario
	 * @param addedNodes
	 * @return
	 */
	def searchMethodsWithDeviation(HashSet<Node> nodesToVisualization, BlamedScenario blamedScenario, List<Node> nodesNV, HashSet<Node> addedNodes) {
		blamedScenario.methods.each { m ->
			def node = nodesNV.find { it.member == m.methodSignature }
			if (node) {
				def isAddedNode = addedNodes?.contains(node) ?: false
				def timeVariationSignal = m.previousExecutionTime > m.nextExecutionTime ? '-' : '+' 
				node.deviation = m.previousExecutionTime > m.nextExecutionTime ? 'optimization' : 'degradation'
				node.timeVariation = m.executionTimeDifference
				node.timeVariationSignal = timeVariationSignal
				node.previousExecutionTime = m.previousExecutionTime
				node.nextExecutionTime = m.nextExecutionTime
				node.qtdExecutedPreviousVersion = m.qtdExecutedPreviousVersion
				node.qtdExecutedNextVersion = m.qtdExecutedNextVersion
				node.hasDeviation = true
				node.isAddedNode = isAddedNode
				nodesToVisualization << node
				nodesToVisualization << node.node
				nodesToVisualization << node.node.node
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
		def rootNode = nodesNV.find { it?.node == null }
		rootNode.isRootNode = true
		nodesToVisualization << rootNode
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
	 * Método que calcula o tempo total do cenário baseado nos nós a serem exibidos.
	 * @param nodesToVisualization
	 * @return
	 */
	def calculateScenarioTime(HashSet<Node> nodesToVisualization) {
		def time = 0
		for (Node n : nodesToVisualization) {
			if (n.hasDeviation) {
				time = time + ((n.timeVariationSignal + n.timeVariation) as BigDecimal)
			}
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
	
	/**
	 * Método que salva no banco de dados o processamento da visualização para posterior consulta.
	 * Funciona como um cache para melhoria de desempenho. Nas próximas consultas pra esse mesmo cenário, na mesma versão,
	 * a aplicação recuperará do banco de dados ao invés de processar tudo novamente.
	 * @param info
	 * @param analysisDuration
	 * @param affectedNodesJSON
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	def saveAnalyzedSystem(info, analysisDuration, affectedNodesJSON) {
		def ansys = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersion(info.system, info.versionFrom, info.versionTo)
		if (!ansys) {
			ansys = new AnalyzedSystem(systemName: info.system, previousVersion: info.versionFrom, nextVersion: info.versionTo)
		}
		AnalyzedScenario ansce = new AnalyzedScenario(totalNodes: info.totalNodes as Integer,
			name: info.scenarioName,
			qtdAddedNodes: info.addedNodes as Integer,
			qtdRemovedNodes: info.removedNodes as Integer,
			qtdDeviationNodes: info.deviationNodes as Integer,
			qtdShowingNodes: info.showingNodes as Integer,
			previousTime: (info.scenarioPreviousTime as BigDecimal).setScale(2, RoundingMode.DOWN),
			nextTime: (info.scenarioNextTime as BigDecimal).setScale(2, RoundingMode.DOWN),
			jsonNodesToVisualization: affectedNodesJSON as String,
			analyzedSystem: ansys,
			date: new Date(),
			analysisDuration: analysisDuration as BigDecimal,
			isDegraded: info.isDegraded)
		
		ansys.addToAnalyzedScenarios(ansce)
		
		try {
			ansys.av.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}
	
	/**
	 * Este método remove dos nós agrupados os nós adicionados, mas somente se eles forem os únicos responsáveis pela degradação/melhoria.
	 * @param nodesToVisualization
	 * @param addedNodes
	 * @return
	 */
	def removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes) {
		def aux = nodesToVisualization
		nodesToVisualization.each { n ->
			if (n.isRootNode && !n.hasDeviation) {
				aux = aux - n
			}
		}
		def ntvAddedNodes = aux.findAll { it.hasDeviation && it.isAddedNode }
		if (ntvAddedNodes) {
			groupedNodes.each { gn ->
				if (ntvAddedNodes.any { gn.addedNodes.contains(it) }) {
					gn.addedNodes = gn.addedNodes - ntvAddedNodes
				}
			}
		}
		nodesToVisualization
	}
	
	/**
	 * Método que calcula os tempos médios dos nós a serem apresentados no call graph.
	 * @param nodesToVisualization
	 * @param scenarioNV
	 * @return
	 */
	def calculateAverageNormalNodeTime(nodesToVisualization, scenarioNV, scenarioPV) {
		def memberNames = nodesToVisualization.collect { if (!it.isGroupedNode) {it.member} } - null
		//def avgTimesPV = NodeScenario.msrPreviousVersion.executeQuery("select n.member as member, avg(n.time) as time, avg(n.realTime) as realTime from NodeScenario ns inner join ns.node n inner join ns.scenario s where n.member in (:members) and s.id in (select s1.id from Scenario s1 where s1.name = :scenarioName) group by n.member", [members: memberNames, scenarioName: scenarioNV.name])
		def avgTimesPV = NodeScenario.msrPreviousVersion.executeQuery("select n.member as member, avg(n.time) as time, avg(n.realTime) as realTime from NodeScenario ns inner join ns.node n where n.member in (:members) and ns.scenario.id = :idScenario group by n.member", [members: memberNames, idScenario: scenarioPV.id])
		//def avgTimesNV = NodeScenario.msrNextVersion.executeQuery("select n.member as member, avg(n.time) as time, avg(n.realTime) as realTime from NodeScenario ns inner join ns.node n inner join ns.scenario s where n.member in (:members) and s.id in (select s1.id from Scenario s1 where s1.name = :scenarioName) group by n.member", [members: memberNames, scenarioName: scenarioNV.name])
		def avgTimesNV = NodeScenario.msrNextVersion.executeQuery("select n.member as member, avg(n.time) as time, avg(n.realTime) as realTime from NodeScenario ns inner join ns.node n inner join ns.scenario s where n.member in (:members) and ns.scenario.id = :idScenario group by n.member", [members: memberNames, idScenario: scenarioNV.id])
		nodesToVisualization.each { n ->
			if (!n.isGroupedNode) {
				def mNV = avgTimesNV.find { it[0] == n.member }
				def mPV = avgTimesPV.find { it[0] == n.member }
				n.nextExecutionTime = (mNV[1] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
				n.nextExecutionRealTime = (mNV[2] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
				if (mPV && mPV[1] && mPV[2]) {
					n.previousExecutionTime = (mPV[1] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
					n.previousExecutionRealTime = (mPV[2] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
				} else {
					n.previousExecutionTime = null
					n.previousExecutionRealTime = null
				}
			}
		}
		nodesToVisualization
	}
	
	/**
	 * Método que calcula o tempo dos nós agrupados.
	 * @param nodesToVisualization
	 * @param groupedNodes
	 * @return
	 */
	def calculateGroupedNodeTime(nodesToVisualization, groupedNodes) {
		groupedNodes.each { n ->
			def siblingNodes = nodesToVisualization.findAll { (it.node?.id == n.node?.id) }
			def siblingNodesTime = siblingNodes?.sum { it.nextExecutionTime }
			n.nextExecutionTime = ((n?.node?.nextExecutionTime - n?.node?.nextExecutionRealTime - (siblingNodesTime ?: 0)) as BigDecimal)?.setScale(2, RoundingMode.DOWN)
		}
		groupedNodes
	}
	
}

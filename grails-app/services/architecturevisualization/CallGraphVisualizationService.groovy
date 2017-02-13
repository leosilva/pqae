package architecturevisualization

import grails.transaction.Transactional

import java.math.RoundingMode

import org.springframework.transaction.annotation.Propagation

import domain.BlamedScenario

@Transactional
class CallGraphVisualizationService {
	
	/**
	 * Método que determina os nós com variação de desempenho baseados no arquivo interpretado.
	 * @param nodesToVisualization
	 * @param fileBlamed
	 * @param nodesNV
	 * @param scenario
	 * @param addedNodes
	 * @return
	 */
	def searchMethodsWithDeviation(List<Node> nodesToVisualization, BlamedScenario blamedScenario, List<Node> nodesNV) {
		def methods = blamedScenario.modifiedMethods + blamedScenario.addedMethods
		methods.each { m ->
			def nodes = nodesNV.findAll { it.member == m.methodSignature }
			nodes?.each { node ->
				if (node) {
					def isAddedNode = blamedScenario.addedMethods?.contains(m) ?: false
					def timeVariationSignal = m.avgExecutionTimePreviousVersion > m.avgExecutionTimeNextVersion ? '-' : '+' 
					node.deviation = m.avgExecutionTimePreviousVersion > m.avgExecutionTimeNextVersion ? 'optimization' : 'degradation'
					node.timeVariation = m.executionTimeDifference
					node.timeVariationSignal = timeVariationSignal
					node.previousExecutionTime = m.avgExecutionTimePreviousVersion
					node.nextExecutionTime = m.avgExecutionTimeNextVersion
					node.qtdExecutedPreviousVersion = m.qtdExecutedPreviousVersion
					node.qtdExecutedNextVersion = m.qtdExecutedNextVersion
					node.hasDeviation = true
					node.isAddedNode = isAddedNode
					addToNodesToVisualization(nodesToVisualization, node)
					//nodesToVisualization.any {node?.id == it?.id} ?: nodesToVisualization << node  
					(node?.node != null) ? addToNodesToVisualization(nodesToVisualization, node?.node) : null
					(node?.node?.node != null) ? addToNodesToVisualization(nodesToVisualization, node?.node?.node) : null 
					(node?.node?.node?.node != null) ? addToNodesToVisualization(nodesToVisualization, node?.node?.node?.node) : null 
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
		def rootNode = nodesNV.find { it?.node == null }
		rootNode.isRootNode = true
		addToNodesToVisualization(nodesToVisualization, rootNode)
		//nodesToVisualization << rootNode
		nodesToVisualization
	}
	
	/**
	 * Método que determina os nós pai dos nós a serem mostrados, mas apenas para aqueles que não possuem pais. São criados nós de agrupamento para serem os pais.
	 * @param nodesWithoutParent
	 * @param nodesToVisualization
	 * @param groupedNodes
	 * @return
	 */
	def defineGrupedBlocksToParents(List<Node> nodesWithoutParent, List<Node> nodesToVisualization, HashSet<Node> groupedNodes) {
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
	def defineGrupedBlocksToChildren(List<Node> nodesToVisualization, HashSet<Node> groupedNodes) {
		for (Node n : nodesToVisualization) {
			// verifica se tem filhos sem variacao
			def hasChildToVisualization = false
			hasChildToVisualization = n.nodes.any { it.hasDeviation == true || it.isGroupedNode == true || !nodesToVisualization.contains(it) }
			if (!hasChildToVisualization) {
				def node = new Node(member : "[...]", nodes : [])
				node.id = (9999999 + 99999999*Math.random()).round()
				node.node = n
				node.hasDeviation = false
				node.isGroupedNode = true
				n.nodes << node
				groupedNodes << node
			} else {
				def nnv = n.nodes.findAll { !nodesToVisualization.contains(it) }
				if (nnv?.size() > 0 && !n.nodes.any { it.isGroupedNode == true }) {
					def node = new Node(member : "[...]", nodes : [])
					node.id = (9999999 + 99999999*Math.random()).round()
					node.node = n
					node.hasDeviation = false
					node.isGroupedNode = true
					n.nodes << node
					groupedNodes << node
				}
			}
		}
		nodesToVisualization.each { n ->
			def gn = n?.nodes?.findAll { it.isGroupedNode == true }
			if (gn?.size() > 0) {
				def isDisplayed = n?.nodes?.every { nodesToVisualization.contains(it) || groupedNodes.contains(it) }
				if (isDisplayed == true) {
					groupedNodes.removeAll(gn)
				}
			}
		}
		groupedNodes
	}
	
	/**
	 * Método que determina os nós irmão dos nós adicionados, criando nós agrupados se necessário.
	 * @param groupedNodes
	 * @param addedNodes
	 * @return
	 */
	def determineSiblingsForAddedNodes(HashSet<Node> groupedNodes, HashSet<Node> addedNodes, List<Node> nodesToVisualization) {
		addedNodes.each { an ->
			def tempNode = an
			while (tempNode != null) {
				def siblingNode = groupedNodes.find { tempNode?.node?.id == it?.node?.id }
				if (siblingNode) {
					siblingNode?.addedNodes << an
					return
				} else {
					def siblingNodes = nodesToVisualization.findAll { tempNode?.node?.id == it?.node?.id }
					siblingNodes?.each { sb ->
						if (sb) {
							def groupedNode = sb.nodes.find { it.isGroupedNode }
							if (groupedNode) {
								groupedNode.addedNodes << an
								return
							} else {
								def node = new Node(member : "[...]", nodes : [])
								node.node = sb
								node.hasDeviation = false
								node.isGroupedNode = true
								node.id = (9999999 + 99999999*Math.random()).round()
								node.addedNodes << an
								sb.nodes << node
								groupedNodes << node
								return
							}
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
	 * @param fileScenario
	 * @param nodesNV
	 * @return
	 */
	def determineAddedNodes(addedNodes, fileScenario, nodesNV) {
		fileScenario.addedMethods?.each { am ->
			def node = nodesNV.find { it.member == am.methodSignature }
			if (node) {
				addedNodes << node
			}
		}
		addedNodes
	}
	
	/**
	 * Método que determina os nós que foram removidos na versão mais recente do sistema.
	 * @param removedNodes
	 * @param fileScenario
	 * @param nodesPV
	 * @return
	 */
	def determineRemovedNodes(removedNodes, fileScenario, nodesPV) {
		fileScenario.removedMethods?.each { am ->
			def node = nodesPV.find { it.member == am.methodSignature }
			if (node) {
				removedNodes << node
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
	def updateAnalyzedSystem(info, analysisDuration, affectedNodesJSON, Set responsibleMethods) {
		def ansys = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersion(info.system, info.versionFrom, info.versionTo)
		AnalyzedScenario ansce = new AnalyzedScenario(totalNodes: info.totalNodes as Integer,
			name: info.scenarioName,
			qtdOptimizedNodes: info.qtdOptimizedNodes as Integer,
			qtdDegradedNodes: info.qtdDegradedNodes as Integer,
			qtdAddedNodes: info.qtdAddedNodes as Integer,
			qtdRemovedNodes: info.qtdRemovedNodes as Integer,
			qtdDeviationNodes: info.deviationNodes as Integer,
			qtdShowingNodes: info.showingNodes as Integer,
			previousTime: (info.scenarioPreviousTime as BigDecimal).setScale(2, RoundingMode.DOWN),
			nextTime: (info.scenarioNextTime as BigDecimal).setScale(2, RoundingMode.DOWN),
			jsonNodesToVisualization: affectedNodesJSON as String,
			analyzedSystem: ansys,
			date: new Date(),
			analysisDuration: analysisDuration as BigDecimal,
			isDegraded: info.isDegraded)
		
		responsibleMethods.each {
			def rm = new ResponsibleMethod(methodSignature: it.methodSignature)
			ansce.addToResponsibleMethods(rm)
		}
		
		ansys.addToAnalyzedScenarios(ansce)
		
		try {
			ansys.av.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	def preSaveAnalyzedSystem(systemName, previousVersion, nextVersion) {
		def ansys = new AnalyzedSystem(systemName: systemName, previousVersion: previousVersion, nextVersion: nextVersion, analyzedSystemStatus: AnalyzedSystemStatus.PENDING)
		try {
			ansys.av.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	def updateAnalyzedSystem(systemName, previousVersion, nextVersion, status) {
		def ansys = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersion(systemName, previousVersion, nextVersion)
		ansys.analyzedSystemStatus = status
		try {
			ansys.av.merge()
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
		def query = "select n.member as member, avg(n.time) as time, avg(n.realTime) as realTime from NodeScenario ns inner join ns.node n where n.member in (:members) and ns.scenario.id in (select s.id from Scenario s where s.name = :scenarioName) group by n.member"
		def avgTimesPV = NodeScenario.msrPreviousVersion.executeQuery(query, [members: memberNames, scenarioName: scenarioPV.name])
		def avgTimesNV = NodeScenario.msrNextVersion.executeQuery(query, [members: memberNames, scenarioName: scenarioNV.name])
		nodesToVisualization.each { n ->
			if (!n.isGroupedNode) {
				def mNV = avgTimesNV.find { it[0] == n.member }
				def mPV = avgTimesPV.find { it[0] == n.member }
				n.nextExecutionTime = (mNV[1] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
				n.nextExecutionRealTime = (mNV[2] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
				if (mPV && mPV[1]) {
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
	
	def addToNodesToVisualization(nodesToVisualization, node) {
		def result = nodesToVisualization.any {it.id == node.id}
		if (!result) {
			nodesToVisualization << node
		}
		nodesToVisualization
	}
	
}

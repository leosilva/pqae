package architecturevisualization

import grails.transaction.Transactional
import grails.converters.JSON

import java.math.RoundingMode

import org.springframework.transaction.annotation.Propagation

import domain.BlamedScenario

import groovy.json.JsonSlurper;

@Transactional
class CallGraphVisualizationService {

	def methodNodes = []
	def packageNodes = []
	
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
					//nodesToVisualization.any {node?.id == it?.id} ?: nodesToVisualization << node
					if (!hasNodesAndParents(nodesToVisualization, node)) {
						addToNodesToVisualization(nodesToVisualization, node)
						(node?.node != null) ? addToNodesToVisualization(nodesToVisualization, node?.node) : null
						(node?.node?.node != null) ? addToNodesToVisualization(nodesToVisualization, node?.node?.node) : null
						(node?.node?.node?.node != null) ? addToNodesToVisualization(nodesToVisualization, node?.node?.node?.node) : null 
					} else {
						incrementNodesCounter(nodesToVisualization, node)
					}
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
		if (rootNode) {
			rootNode.isRootNode = true
		} else {
			def n = nodesNV[0]
			while (n.node) {
				n = n.node
			}
			rootNode = n
		}
		addToNodesToVisualization(nodesToVisualization, rootNode)
		nodesToVisualization
	}
	
	/**
	 * Método que busca pelo nó raiz na lista de nós. Se houver visualização, o nó raiz sempre aparecerá.
	 * @param scenarioNV
	 * @return
	 */
	def searchRootNode(List nodesToVisualization, Scenario scenarioNV) {
		def n = scenarioNV.node
		n.isRootNode = true
		
		addToNodesToVisualization(nodesToVisualization, n)
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
	 * Método que define os nós por pacote.
	 *
	 * @param affectedNodesJSON
	 * @return
	 */
	def defineGrupedBlocksByPackage(affectedNodesJSON) {
		def listMap = new JsonSlurper().parseText(affectedNodesJSON)
		methodNodes = listMap.nodes

		methodNodes.clone().each {
			groupMethodsPointsNodes(it)
		}

		methodNodes.clone().each {
			if (it.nodes.isEmpty()) checkNodes(it)
		}
		
		def rootNode = getRootNode()
		rootNode.id = (9999999 + 99999999*Math.random()).round()
		rootNode['package'] = getPackageNameByNode(rootNode)
		rootNode['isPackageNode'] = true

		packageNodes = []
		removePackageRepetitions(rootNode)
		
		packageNodes.clone().each {
			addLostParents(it)
		}

		packageNodes.clone().each {
			groupPointsNodes(it)
		}

		listMap.nodes = packageNodes
		
		return listMap as JSON
	}

	/**
	 * Método que pecorre os nós do grafo recursivamente para remover
	 * a repetição de pacotes.
	 *
	 * @param node
	 * @return
	 */
	private def removePackageRepetitions(node){
		if(node){

			// Adiciona o nó ao conjunto de nós pacotes, se não tiver sido adicionado
			def nodeAux = getPackageNodeById(node.id)
			if(!nodeAux) packageNodes.add(node) 
			
			if(node.nodes){
				def nodesAux = []
				node.nodes.clone().each {					
					def childNode = getNodeById(it.id)
					if(childNode){			
						// Atribui o pacote ao nó e procura um nó pacote já existente 
						childNode['package'] = getPackageNameByNode(childNode)
						def packageNode = getNodeByPackage(childNode.package)
							
						/* Para cada nó filho, se já existir nó pacote com o mesmo 
						pacote que do meu filho, eu vou apontar para ele e unir o nós */
						if(packageNode && !packageNode.isGroupedNode){
							nodesAux.add([id: packageNode.id])
							groupNodes(childNode, packageNode)
							removePackageRepetitions(packageNode)
						} else{
						/* Se não existir nó pacote com o mesmo pacote que do meu filho 
						e depois é gerado um novo id */
							nodeAux = getPackageNodeById(childNode.id)
							
							if(!nodeAux){
								childNode.oldNode = childNode.node
								childNode.oldId = childNode.id
							}
							
							childNode.node = [id: node.id]
							childNode.id = (9999999 + 99999999*Math.random()).round()
							childNode['isPackageNode'] = true
							nodesAux.add([id: childNode.id])
							removePackageRepetitions(childNode)
						}
						nodesAux.remove(it)
					}
					node.nodes = nodesAux
				}
			}
		}
	}

	/**
	 * Método "work around" para retomar pais perdidos.
	 *
	 * @param node
	 * @return
	 */
	private def addLostParents(node){
		if(node && !node.isRootNode){
			def parentNode = getPackageNodeById(node.node.id)
			
			/* Caso o nó pai não tenha sido adicionado, buscar um novo nó pai
			com base no nome do pacote para */
			if(!parentNode){
				parentNode = getNodeById(node.node.id)
				parentNode = getNodeByPackage(getPackageNameByNode(parentNode))
			}

			/* Se meu pai (novo ou não) existir será alterado os campos referenciados */ 
			if(parentNode) {
				parentNode.nodes.add([id: node.id])
				node.node = [id: parentNode.id]
			} 

			node.nodes.remove([id: node.id])
		}
	}
	
	/**
	 * Método que pecorre os nós do grafo recursivamente para encontrar
	 * nós pais com mesmo pacote dos nós filhos.
	 *
	 * @param node
	 * @return
	 */
	private def checkNodes(node){
		String myPackage = getPackageNameByNode(node)
		if(node){
			def parentNode = getNodeById(node.node.id)
			String parentPackage = getPackageNameByNode(parentNode)
			
			// Verifica se algum nó filho está no mesmo pacote que o pai
			if(node.nodes){
				node.nodes.clone().each {
					def childNode = getNodeById(it.id)
					String childPackage = getPackageNameByNode(childNode)
					if (childPackage == myPackage){
						groupNodes(childNode, node)
					} 
				}
			}

			// Verifica se o nó pai está no mesmo pacote que o filho
			if (myPackage == parentPackage){
				groupNodes(node, parentNode)
			} 
				
			// Chama recursivamente essa função para o nó pai
			if(node.node){
				checkNodes(getNodeById(node.node.id))
			}
		}	
	}

	/**
	 * Método que unir nós filhos com "[...]"
	 *
	 * @param nodes
	 * @param node
	 * @return
	 */
	 private void groupPointsNodes(node){
		if(node.nodes && node.nodes.size > 1){
			
			def nodeToBeContinued = null
			def nextExecutionTime = 0

			// Busca primeiro id de nó filho agrupado
			node.nodes.clone().each {	
				def nodeAux = getNodeById(it.id)
				if(nodeAux && nodeAux.isGroupedNode) { nodeToBeContinued = nodeAux }
			}

			// Pecorre a lista de filhos para agrupa os tempos de filhos agrupados,
			// Caso algum filho agrupado tenha filhos, ele será mantido.
			node.nodes.clone().each {	
				def nodeAux = getNodeById(it.id)
				if(nodeAux){
					if(nodeAux.nodes && nodeAux.isGroupedNode) { nodeToBeContinued = nodeAux }
					if(nodeAux.isGroupedNode) { nextExecutionTime += nodeAux.nextExecutionTime }
				}
			}
			
			// Preenche o filho escolhido com a soma dos tempos
			if(nodeToBeContinued) { nodeToBeContinued.nextExecutionTime = nextExecutionTime }

			// Remove os nós filhos que foram agrupados
			node.nodes.clone().each {	
				def nodeToBeRemoved = getNodeById(it.id)
				if(nodeToBeRemoved && nodeToBeContinued &&
				   nodeToBeRemoved.id != nodeToBeContinued.id && 
				   nodeToBeRemoved.isGroupedNode) {
					packageNodes.remove(nodeToBeRemoved)
					node.nodes.remove(it)
				}
			}				
		}
		
	}

	/**
	 * Método "work around" que unir nós filhos  "[...]" com mesmo tempo
	 *
	 * @param nodes
	 * @param node
	 * @return
	 */
	 private void groupMethodsPointsNodes(node){
		if(node.nodes && node.nodes.size > 1){
			
			def nodeToBeContinued = null

			// Busca primeiro id de nó filho agrupado
			node.nodes.clone().each {	
				def nodeAux = getNodeById(it.id)
				if(nodeAux && nodeAux.isGroupedNode) { nodeToBeContinued = nodeAux }
			}

			// Pecorre a lista de filhos para agrupa os tempos de filhos agrupados,
			// Caso algum filho agrupado tenha filhos, ele será mantido.
			node.nodes.clone().each {	
				def nodeAux = getNodeById(it.id)
				if(nodeAux && nodeToBeContinued &&
				   nodeToBeContinued.id != nodeAux.id && 
				   nodeToBeContinued.nextExecutionTime == nodeAux.nextExecutionTime){
					nodeToBeContinued.nodes.addAll(nodeAux.nodes)
					methodNodes.remove(nodeAux)
				}
			}			
		}
	}

	/**
	 * Método que unir as características de um nó pai com um nó filho
	 *
	 * @param nodes
	 * @param node
	 * @param parentNode
	 * @return
	 */
	 private def groupNodes(node, parentNode){
		// Adiciona metódos em chave dos nós unidos para o pacote final
		if(!parentNode.methods){
			parentNode['methods'] = [parentNode.member]
		}

		if (node.methods){
			parentNode.methods.addAll(node.methods)
		} else{
			parentNode.methods.add(node.member)
		}
		
		// União das informações do nós
		parentNode.nodes.addAll(node.nodes)

		// Caso o nó filho tenha um desvio, pegue alguns informações
		if(node.deviation){
			parentNode.member = node.member
			parentNode.timeVariationSignal = node.timeVariationSignal
			parentNode.timeVariation = 	node.timeVariation
			parentNode.hasDeviation = node.hasDeviation
			parentNode.isGroupedNode = node.isGroupedNode
			parentNode.isAddedNode = node.isAddedNode
			parentNode.isRemovedNode = node.isRemovedNode
			parentNode.isRootNode = node.isRootNode
			parentNode.addedNodes = node.addedNodes
			parentNode.removedNodes = node.removedNodes
			parentNode.deviation = node.deviation
			parentNode.loopTimes += node.loopTimes
			parentNode.previousExecutionTime += node.previousExecutionTime
			parentNode.previousExecutionRealTime += node.previousExecutionRealTime
			parentNode.nextExecutionTime +=  node.nextExecutionTime 
			parentNode.nextExecutionRealTime += node.nextExecutionRealTime
			parentNode.commits.addAll(node.commits)
			parentNode.commits = parentNode.commits.toSet()
		} else if (!node.hasDeviation && !node.isGroupedNode) {
			parentNode.nextExecutionTime += node.nextExecutionTime 
			parentNode.nextExecutionRealTime += node.nextExecutionRealTime
		} else if (node.timeVariation == null && !node.isGroupedNode && !node.hasDeviation) {
			parentNode.nextExecutionTime += node.nextExecutionTime 
		} else if (node.hasDeviation && !node.isAddedNode && !node.isRemovedNode) {
			parentNode.nextExecutionTime += node.nextExecutionTime 
			parentNode.nextExecutionRealTime += node.nextExecutionRealTime
			parentNode.timeVariation = 	node.timeVariation
		} else if (node.isGroupedNode) {
			parentNode.nextExecutionTime += node.nextExecutionTime 
		} else if (node.isAddedNode) {
			parentNode.nextExecutionTime += node.nextExecutionTime 
			parentNode.nextExecutionRealTime += node.nextExecutionRealTime
		} else if (node.isRemovedNode) {
			parentNode.previousExecutionTime += node.previousExecutionTime
			parentNode.previousExecutionRealTime += node.previousExecutionRealTime
		}
		methodNodes.remove(node)
	 }

	/**
	 * Método que gera o nome do pacote dado um nó.
	 *
	 * @param node
	 * @return
	 */
	private def getPackageNameByNode(node){
		if (node != null){
			return node.member.split("(?=\\p{Upper})")[0][0..-2]
		} else{
			""
		}
	}

	/**
	 * Método que encontrar o nó pelo id em um lista de nós.
	 *
	 * @param id
	 * @return
	 */
	private def getNodeById(id){
		return methodNodes.find { it.id == id }
	}

	/**
	 * Método que encontrar o nó pelo id em um lista de nós.
	 *
	 * @param id
	 * @return
	 */
	private def getPackageNodeById(id){
		return packageNodes.find { it.id == id }
	}

	/**
	 * Método que encontrar o nó raiz em um lista de nós.
	 *
	 * @param id
	 * @return
	 */
	private def getRootNode(){
		return methodNodes.find { it.isRootNode == true }
	}

	/**
	 * Método que encontrar o nó pelo pacote.
	 *
	 * @param packageName
	 * @return
	 */
	private def getNodeByPackage(packageName){
		return packageNodes.find { it.package == packageName }
	}

	/**
	 * Método que encontrar o nó pelo id e alterar seus campos.
	 *
	 * @param id
	 * @param nodeChanged
	 */
	private void changeNodeById(id, nodeChanged){
		def nodeFound = methodNodes.find { it.id == id }
		nodeFound.keySet().each{
			if(it != "id") nodeFound[it] = nodeChanged[it]
		}
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
			qtdAddedMethods: info.qtdAddedMethods as Integer,
			qtdRemovedMethods: info.qtdRemovedMethods as Integer,
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
			it.commits.each { c ->
				def commit = new ResponsibleCommit(commitHash: c.commitHash)
				rm.addToResponsibleCommits(commit)
			}
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
		println systemName
		println previousVersion
		println nextVersion
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
				if (!n.isRemovedNode && mNV) {
					n.nextExecutionTime = (mNV[1] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
					n.nextExecutionRealTime = (mNV[2] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
				}
				if (mPV && mPV[1]) {
					n.previousExecutionTime = (mPV[1] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
					n.previousExecutionRealTime = (mPV[2] as BigDecimal)?.setScale(2, RoundingMode.DOWN)
				} else {
					n.previousExecutionTime = 0
					n.previousExecutionRealTime = 0
				}
				
				if (!mNV && !mPV) {
					n.nextExecutionTime = 0
					n.nextExecutionRealTime = 0
					n.previousExecutionTime = 0
					n.previousExecutionRealTime = 0
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
			def siblingNodesTime = siblingNodes?.sum { it.nextExecutionTime ?: 0 }
			def parentNode = nodesToVisualization.find { it.id == n.node?.id }
			if (!parentNode?.isRemovedNode) {
				println "inside calculateGroupedNodeTime() ..."
				nodesToVisualization.each {
					if (it.member == n?.node?.member) {
						n?.node?.nextExecutionTime = it.time
						n?.node?.nextExecutionRealTime = it.realTime
					}
				}
				n.nextExecutionTime = ((n?.node?.nextExecutionTime - n?.node?.nextExecutionRealTime - (siblingNodesTime ?: 0)) as BigDecimal)?.setScale(2, RoundingMode.DOWN)
			} else {
				n.nextExecutionTime = ((n?.node?.previousExecutionTime - n?.node?.previousExecutionRealTime - (siblingNodesTime ?: 0)) as BigDecimal)?.setScale(2, RoundingMode.DOWN)
			}
		}
		groupedNodes
	}
	
	def addToNodesToVisualization(nodesToVisualization, node) {
		def result = nodesToVisualization.any { it.id == node.id }
		if (!result) {
			nodesToVisualization << node
		}
		nodesToVisualization
	}
	
	def hasNodesAndParents(nodesToVisualization, node) {
		def hasNode, hasParent, hasGrandParent, hasGreatGrandParent = false
		if (nodesToVisualization.any { it.member == node.member }) {
			hasNode = true
		}
		if (nodesToVisualization.any { it.node?.member == node.node?.member }) {
			hasParent = true
		}
		if (nodesToVisualization.any { it.node?.node?.member == node.node?.node?.member }) {
			hasGrandParent = true
		}
		if (nodesToVisualization.any { it.node?.node?.node?.member == node.node?.node?.node?.member }) {
			hasGreatGrandParent = true
		}
		
		if (hasNode && hasParent && hasGrandParent && hasGreatGrandParent) {
			return true
		} else {
			return false
		}
	}
	
	def incrementNodesCounter(nodesToVisualization, node) {
		def hasNode/*, hasParent, hasGrandParent, hasGreatGrandParent*/ = false
		if (nodesToVisualization.any { it.member == node.member }) {
			hasNode = true
		}
//		if (nodesToVisualization.any { it.node?.member == node.node?.member }) {
//			hasParent = true
//		}
//		if (nodesToVisualization.any { it.node?.node?.member == node.node?.node?.member }) {
//			hasGrandParent = true
//		}
//		if (nodesToVisualization.any { it.node?.node?.node?.member == node.node?.node?.node?.member }) {
//			hasGreatGrandParent = true
//		}
		
		if (hasNode/* && hasParent && hasGrandParent && hasGreatGrandParent*/) {
			def n = nodesToVisualization.find {
				it.member == node.member &&
				it.node?.member == node.node?.member &&
				it.node?.node?.member == node.node?.node?.member &&
				it.node?.node?.node?.member == node.node?.node?.node?.member
				}
			if (n) {
				n?.loopTimes++
				/*if (n?.node) n?.node?.loopTimes++
				if (n?.node?.node) n?.node?.node?.loopTimes++
				if (n?.node?.node?.node) n?.node?.node?.node?.loopTimes++*/
			}
		}
	}
	
	
	/**
	 * Método que determina os commits responsáveis por afetar os nós a serem visualizados.
	 * @param nodesToVisualization
	 * @param scenario
	 * @return
	 */
	def determineCommits(nodesToVisualization, scenario) {
		def methods = scenario.modifiedMethods + scenario.addedMethods + scenario.removedMethods
		methods.each { m ->
			def nodes = nodesToVisualization.findAll { it.member == m.methodSignature }
			if (nodes) {
				nodes.each { n ->
					n.commits = m.commits
				}
			}
		}
		nodesToVisualization
	}
	
	def searchRemovedNodes(nodesToVisualization, removedNodes, scenario) {
		scenario.removedMethods?.each { rm ->
			def nodes = removedNodes.findAll { it.member == rm.methodSignature }
			if (nodes) {
				nodes.each { node ->
					node.previousExecutionTime = rm.avgExecutionTimePreviousVersion
					node.previousExecutionTime = rm.avgExecutionTimePreviousVersion
					node.qtdExecutedPreviousVersion = rm.qtdExecutedPreviousVersion
					node.hasDeviation = true
					node.isRemovedNode = true
					addToNodesToVisualization(nodesToVisualization, node)
				}
			}
		}
		nodesToVisualization
	}
	
	def determineParentsForRemovedNodes(nodesToVisualization, removedNodes, scenario) {
		scenario.removedMethods?.each { rm ->
			def nodes = removedNodes.findAll { it.member == rm.methodSignature }
			if (nodes) {
				nodes.each { node ->
					createParentsForRemovedNodes(nodesToVisualization, node)
				}
			}
		}
		nodesToVisualization
	}
	
	private def createParentsForRemovedNodes(nodesToVisualization, node) {
		def tempNode = node
		def parent = nodesToVisualization.find { it.member == tempNode?.node?.member }
		if (parent) {
			tempNode?.node = parent
			tempNode?.node?.removedNodes << node
			parent.nodes << node
		} else {
			tempNode?.node ? tempNode?.node?.nodes << node : null 
			tempNode?.node ? addToNodesToVisualization(nodesToVisualization, tempNode?.node) : null
			if (tempNode.node) {
				createParentsForRemovedNodes(nodesToVisualization, tempNode?.node)
			}
		}
	}

	def searchResultByAnalyzedScenario(params){
		return AnalyzedScenario.executeQuery("select distinct an from AnalyzedScenario an inner join an.analyzedSystem asy where an.name = :name and asy.systemName = :systemName and asy.previousVersion = :previousVersion and asy.nextVersion = :nextVersion", [name : params.scenarioName, systemName: params.systemName, previousVersion : params.previousVersion, nextVersion : params.nextVersion]) 
	}

	def searchPreviousVersionNode(scenarioPV){
		return NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
	}

	def searchNextVersionNode(scenarioNV){
		return NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])
	}

	def searchPreviousVersionScenario(params){
		return Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
	}

	def searchNextVersionScenario(params){
		return Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
	}
	
}

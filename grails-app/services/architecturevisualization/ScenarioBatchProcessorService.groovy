package architecturevisualization

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory

@Transactional
class ScenarioBatchProcessorService {
	
	def perfMinerIntegrationFilesService
	def callGraphVisualizationService

	/**
	 * Método que executa o processamento em batch de todos os cenários afetados para um sistema, dentre duas versões.
	 * @param systemName
	 * @param previousVersion
	 * @param nextVersion
	 * @return
	 */
    def doBatchProcess(systemName, previousVersion, nextVersion) {
		println "Begin batch processing..."
		def dataInicial = new Date();
		def files = []
		
		files = perfMinerIntegrationFilesService.readBlamedMethodsScenariosFile(systemName, previousVersion, nextVersion)
		
		AnalyzedSystem ansys = new AnalyzedSystem(systemName: systemName, previousVersion: previousVersion, nextVersion: nextVersion)
		
		files.each { fileBlamed ->
			fileBlamed.scenarios.each { scenario ->
				def d1 = new Date();
				def nodesToVisualization = new HashSet()
				def nodesWithoutParent = new HashSet()
				def groupedNodes = new HashSet()
				
				def scenarioPV = Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: scenario.scenarioName], [max: 1]).first()
				def scenarioNV = Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: scenario.scenarioName], [max: 1]).first()
				def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
				def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])

				def addedNodes = new HashSet()
				addedNodes = callGraphVisualizationService.determineAddedNodes(addedNodes, nodesPV, nodesNV)
				def removedNodes = new HashSet()
				removedNodes = callGraphVisualizationService.determineRemovedNodes(removedNodes, nodesPV, nodesNV)

				nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesNV, nodesToVisualization)
				nodesToVisualization = callGraphVisualizationService.searchMethodsWithDeviation(nodesToVisualization, scenario, nodesNV, addedNodes)
				
				nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
				
				groupedNodes = callGraphVisualizationService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
				groupedNodes = callGraphVisualizationService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
				groupedNodes = callGraphVisualizationService.collectInfoAddedNodes(groupedNodes, addedNodes, nodesToVisualization)
				
				def scenarioTime = callGraphVisualizationService.calculateScenarioTime(nodesToVisualization)
				def qtdDeviationNodes = nodesToVisualization.findAll { it.hasDeviation == true }.size()
				
				nodesToVisualization = callGraphVisualizationService.removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes)
				
				nodesToVisualization.addAll(groupedNodes)
				
				def affectedNodes = [
				 "nodes" : nodesToVisualization
				]
											 
				def info = [
					"totalNodes" : nodesNV.size(),
					"deviationNodes" : qtdDeviationNodes,
					"scenarioName" : scenarioNV.name,
					"system" : scenarioNV.execution.systemName,
					"versionFrom" : scenarioPV.execution.systemVersion,
					"versionTo" : scenarioNV.execution.systemVersion,
					"broadScenarioTime" : scenarioTime,
					"addedNodes" : addedNodes.size(),
					"removedNodes" : removedNodes.size(),
					"showingNodes" : nodesToVisualization.size()
				]
				
				def affectedNodesJSON = (affectedNodes as JSON)
				
				def d2 = new Date();
				def analysisDuration = TimeCategory.minus(d2, d1).toString()
				
				AnalyzedScenario ansce = new AnalyzedScenario(totalNodes: info.totalNodes as Integer,
					name: info.scenarioName,
					qtdAddedNodes: info.addedNodes as int,
					qtdRemovedNodes: info.removedNodes as int,
					qtdDeviationNodes: info.deviationNodes as int,
					qtdShowingNodes: info.showingNodes as int,
					broadTime: info.broadScenarioTime as BigInteger,
					jsonNodesToVisualization: affectedNodesJSON as String,
					analyzedSystem: ansys,
					date: new Date(),
					analysisDuration: analysisDuration,
					isDegraded: scenario.isDegraded)
				
				ansys.addToAnalyzedScenarios(ansce)
			}
		}
		
		try {
			ansys.av.save(flush: true)
		} catch (Exception e) {
			e.printStackTrace()
		}
		
		def dataFinal = new Date();
		def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
		println "Duração: ${analysisDuration}"
		println "End batch processing..."
    }
}

package architecturevisualization

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory

@Transactional(readOnly = true)
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
    def doBatchProcess(systemName, previousVersion, nextVersion, fileDegradedScenarios, fileOptimizedScenarios) {
		println "Begin batch processing..."
		def dataInicial = new Date();
		def files = []
		
		files = perfMinerIntegrationFilesService.readBlamedMethodsScenariosFile(systemName, fileDegradedScenarios, fileOptimizedScenarios)
		
		AnalyzedSystem ansys = new AnalyzedSystem(systemName: systemName, previousVersion: previousVersion, nextVersion: nextVersion)
		
		files.each { fileBlamed ->
			fileBlamed.scenarios.each { scenario ->
				def d1 = new Date();
				def nodesToVisualization = []
				def nodesWithoutParent = []
				def groupedNodes = new HashSet()
				
				def scenarioPV = Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: scenario.scenarioName], [max: 1]).first()
				def scenarioNV = Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: scenario.scenarioName], [max: 1]).first()
				def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
				def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])

				def addedNodes = new HashSet()
				addedNodes = callGraphVisualizationService.determineAddedNodes(addedNodes, scenario, nodesNV)
				def removedNodes = new HashSet()
				removedNodes = callGraphVisualizationService.determineRemovedNodes(removedNodes, scenario, nodesPV)

				nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesNV, nodesToVisualization)
				nodesToVisualization = callGraphVisualizationService.searchMethodsWithDeviation(nodesToVisualization, scenario, nodesNV)
				
				nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
				
				groupedNodes = callGraphVisualizationService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
				groupedNodes = callGraphVisualizationService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
				groupedNodes = callGraphVisualizationService.determineSiblingsForAddedNodes(groupedNodes, addedNodes, nodesToVisualization)
				
				def qtdDeviationNodes = scenario.modifiedMethods?.size()
				
				nodesToVisualization = callGraphVisualizationService.removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes)
				
				nodesToVisualization = callGraphVisualizationService.calculateAverageNormalNodeTime(nodesToVisualization, scenarioNV, scenarioPV)
			
				groupedNodes = callGraphVisualizationService.calculateGroupedNodeTime(nodesToVisualization, groupedNodes)
				
				def qtdOptimizedNodes = nodesToVisualization.count { it.deviation == "optimization" && it.isAddedNode == false }
				def qtdDegradedNodes = nodesToVisualization.count { it.deviation == "degradation" && it.isAddedNode == false }
				
				nodesToVisualization.addAll(groupedNodes)
				
				Set responsibleMethods = scenario.modifiedMethods + scenario.addedMethods + scenario.removedMethods
				
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
					"scenarioPreviousTime" : scenario.avgExecutionTimePreviousVersion,
					"scenarioNextTime" : scenario.avgExecutionTimeNextVersion,
					"qtdOptimizedNodes" : qtdOptimizedNodes,
					"qtdDegradedNodes" : qtdDegradedNodes,
					"qtdAddedNodes" : addedNodes.size(),
					"qtdRemovedNodes" : removedNodes.size(),
					"showingNodes" : nodesToVisualization.size(),
					"isDegraded" : scenario.isDegraded
				]
				
				def affectedNodesJSON = (affectedNodes as JSON)
				
				def d2 = new Date();
				def analysisDuration = TimeCategory.minus(d2, d1).toMilliseconds() / 1000
				
				//callGraphVisualizationService.updateAnalyzedSystem(info, analysisDuration, affectedNodesJSON)
				callGraphVisualizationService.updateAnalyzedSystem(info, analysisDuration, affectedNodesJSON, responsibleMethods)
			}
		}
		
		callGraphVisualizationService.updateAnalyzedSystem(systemName, previousVersion, nextVersion, AnalyzedSystemStatus.COMPLETED)
		
		def dataFinal = new Date();
		def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
		println "Duração: ${analysisDuration}"
		println "End batch processing..."
    }
	
	def preSaveAnalyzedSystem(systemName, previousVersion, nextVersion) {
		callGraphVisualizationService.preSaveAnalyzedSystem(systemName, previousVersion, nextVersion)
	}
}

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
				println "SCENARIO: ${scenario.scenarioName}"
				def d1 = new Date();
				def nodesToVisualization = []
				def nodesWithoutParent = []
				def groupedNodes = new HashSet()
				
				def scenarioPV = Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: scenario.scenarioName], [max: 1]).first()
				def scenarioNV = Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: scenario.scenarioName], [max: 1]).first()
				def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
				def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])

				def addedMethods = new HashSet()
				addedMethods = callGraphVisualizationService.determineAddedNodes(addedMethods, scenario, nodesNV)
				def removedMethods = new HashSet()
				removedMethods = callGraphVisualizationService.determineRemovedNodes(removedMethods, scenario, nodesPV)
				
				nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesNV, nodesToVisualization)

				println "after searchRootNode() ..."
				nodesToVisualization.each {
					if (it.member == "org.apache.wicket.markup.parser.filter.HtmlHeaderSectionHandler.onComponentTag(org.apache.wicket.markup.ComponentTag)") {
						println it.member
						println it.time
						println it.realTime
					}
				}

				//nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesToVisualization, scenarioNV)
				nodesToVisualization = callGraphVisualizationService.searchMethodsWithDeviation(nodesToVisualization, scenario, nodesNV)

				println "after searchMethodsWithDeviation() ..."
				nodesToVisualization.each {
					if (it.member == "org.apache.wicket.markup.parser.filter.HtmlHeaderSectionHandler.onComponentTag(org.apache.wicket.markup.ComponentTag)") {
						println it.member
						println it.time
						println it.realTime
					}
				}
				
				nodesToVisualization = callGraphVisualizationService.searchRemovedNodes(nodesToVisualization, removedMethods, scenario)

				println "after searchRemovedNodes() ..."
				nodesToVisualization.each {
					if (it.member == "org.apache.wicket.markup.parser.filter.HtmlHeaderSectionHandler.onComponentTag(org.apache.wicket.markup.ComponentTag)") {
						println it.member
						println it.time
						println it.realTime
					}
				}

				nodesToVisualization = callGraphVisualizationService.determineParentsForRemovedNodes(nodesToVisualization, removedMethods, scenario)
				println "after determineParentsForRemovedNodes() ..."
				nodesToVisualization.each {
					if (it.member == "org.apache.wicket.markup.parser.filter.HtmlHeaderSectionHandler.onComponentTag(org.apache.wicket.markup.ComponentTag)") {
						println it.member
						println it.time
						println it.realTime
					}
				}


				nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
				
				groupedNodes = callGraphVisualizationService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
				groupedNodes = callGraphVisualizationService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
				groupedNodes = callGraphVisualizationService.determineSiblingsForAddedNodes(groupedNodes, addedMethods, nodesToVisualization)
				
				def qtdDeviationNodes = scenario.modifiedMethods?.size()
				
				nodesToVisualization = callGraphVisualizationService.removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes)
				println "after removeAddedNodesFromVisualization() ..."
				nodesToVisualization.each {
					if (it.member == "org.apache.wicket.markup.parser.filter.HtmlHeaderSectionHandler.onComponentTag(org.apache.wicket.markup.ComponentTag)") {
						println it.member
						println it.time
						println it.realTime
					}
				}
				
				nodesToVisualization = callGraphVisualizationService.calculateAverageNormalNodeTime(nodesToVisualization, scenarioNV, scenarioPV)
				println "after calculateAverageNormalNodeTime() ..."
				nodesToVisualization.each {
					if (it.member == "org.apache.wicket.markup.parser.filter.HtmlHeaderSectionHandler.onComponentTag(org.apache.wicket.markup.ComponentTag)") {
						println it.id
						println it.member
						println it.time
						println it.realTime
					}
				}

				groupedNodes = callGraphVisualizationService.calculateGroupedNodeTime(nodesToVisualization, groupedNodes)
				
				def qtdOptimizedNodes = nodesToVisualization.count { it.deviation == "optimization" && it.isAddedNode == false }
				def qtdDegradedNodes = nodesToVisualization.count { it.deviation == "degradation" && it.isAddedNode == false }
				
				nodesToVisualization = callGraphVisualizationService.determineCommits(nodesToVisualization, scenario)
				
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
					"qtdAddedMethods" : addedMethods.size(),
					"qtdRemovedMethods" : removedMethods.size(),
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

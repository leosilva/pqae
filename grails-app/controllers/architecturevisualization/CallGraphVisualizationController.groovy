package architecturevisualization

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.Sql
import groovy.time.TimeCategory

@Transactional(readOnly = true)
class CallGraphVisualizationController {
	
	def callGraphVisualizationService
	def perfMinerIntegrationFilesService
	
    def callGraphVisualization() {
		def force = params.force
		def an = null
		
		if (params.scenarioName && params.systemName && params.previousVersion && params.nextVersion) {
			def result = AnalyzedScenario.executeQuery("select distinct an from AnalyzedScenario an inner join an.analyzedSystem asy where an.name = :name and asy.systemName = :systemName and asy.previousVersion = :previousVersion and asy.nextVersion = :nextVersion", [name : params.scenarioName, systemName: params.systemName, previousVersion : params.previousVersion, nextVersion : params.nextVersion]) 
			if (result) {
				an = result.first()
			} 
		}
		if (force || !an) {
			def dataInicial = new Date();
			def nodesToVisualization = []
			def nodesWithoutParent = []
			def groupedNodes = new HashSet()
			
			def files = perfMinerIntegrationFilesService.readBlamedMethodsScenariosFile(params.systemName, params.previousVersion, params.nextVersion)
			
			def scenarioPV = Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
			def scenarioNV = Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
			
			def blamedScenario = files*.scenarios.flatten().find { it.scenarioName == scenarioNV.name }
			
			def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
			def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])

			def addedNodes = new HashSet()
			addedNodes = callGraphVisualizationService.determineAddedNodes(addedNodes, blamedScenario, nodesNV)
			
			def removedNodes = new HashSet()
			removedNodes = callGraphVisualizationService.determineRemovedNodes(removedNodes, blamedScenario, nodesPV)
			
			nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesNV, nodesToVisualization)
			nodesToVisualization = callGraphVisualizationService.searchMethodsWithDeviation(nodesToVisualization, blamedScenario, nodesNV)

			nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
			
			groupedNodes = callGraphVisualizationService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
			groupedNodes = callGraphVisualizationService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
			groupedNodes = callGraphVisualizationService.determineSiblingsForAddedNodes(groupedNodes, addedNodes, nodesToVisualization)

			def qtdDeviationNodes = blamedScenario.modifiedMethods?.size()
			
			nodesToVisualization = callGraphVisualizationService.removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes)

			nodesToVisualization = callGraphVisualizationService.calculateAverageNormalNodeTime(nodesToVisualization, scenarioNV, scenarioPV)
			
			groupedNodes = callGraphVisualizationService.calculateGroupedNodeTime(nodesToVisualization, groupedNodes)
			
			def qtdOptimizedNodes = nodesToVisualization.count { it.deviation == "optimization" && it.isAddedNode == false }
			def qtdDegradedNodes = nodesToVisualization.count { it.deviation == "degradation" && it.isAddedNode == false }
			
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
				"scenarioPreviousTime" : blamedScenario.avgExecutionTimePreviousVersion,
				"scenarioNextTime" : blamedScenario.avgExecutionTimeNextVersion,
				"qtdOptimizedNodes" : qtdOptimizedNodes,
				"qtdDegradedNodes" : qtdDegradedNodes,
				"qtdAddedNodes" : addedNodes.size(),
				"qtdRemovedNodes" : removedNodes.size(),
				"showingNodes" : nodesToVisualization.size(),
				"isDegraded" : blamedScenario.isDegraded
			]
			
			Set responsibleMethods = blamedScenario.modifiedMethods + blamedScenario.addedMethods + blamedScenario.removedMethods
			
			def affectedNodesJSON = (affectedNodes as JSON)
			
			def dataForHistory = getDataForHistory(an.analyzedSystem.systemName, an.name)
    		            		 
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toMilliseconds() / 1000
			println "Duração: ${analysisDuration}"

			callGraphVisualizationService.updateAnalyzedSystem(info, analysisDuration, affectedNodesJSON, responsibleMethods)
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info, backPage : params.targetUri, pageTitle : g.message(code: "application.pageTitle.callGraphVisualization"), history : dataForHistory as JSON]
		} else if (an) {
			def dataInicial = new Date();
			
			def info = [
				"totalNodes" : an.totalNodes,
				"deviationNodes" : an.qtdDeviationNodes,
				"scenarioName" : an.name,
				"system" : an.analyzedSystem.systemName,
				"versionFrom" : an.analyzedSystem.previousVersion,
				"versionTo" : an.analyzedSystem.nextVersion,
				"scenarioPreviousTime" : an.previousTime,
				"scenarioNextTime" : an.nextTime,
				"qtdOptimizedNodes" : an.qtdOptimizedNodes,
				"qtdDegradedNodes" : an.qtdDegradedNodes,
				"qtdAddedNodes" : an.qtdAddedNodes,
				"qtdRemovedNodes" : an.qtdRemovedNodes,
				"showingNodes" : an.qtdShowingNodes,
				"isDegraded" : an.isDegraded
			]
			
			def affectedNodesJSON = an.jsonNodesToVisualization
			
			def dataForHistory = getDataForHistory(an.analyzedSystem.systemName, an.name)
			
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
			println "Duração: ${analysisDuration}"
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info, backPage : params.targetUri, pageTitle : g.message(code: "application.pageTitle.callGraphVisualization"), history : dataForHistory as JSON]
		}
	}
	
	private def getDataForHistory(analyzedSystem, scenarioName) {
		def c = AnalyzedSystem.createCriteria()
		def results = c {
			eq ("systemName", analyzedSystem)
			analyzedScenarios {
				eq ("name", scenarioName)
			}
			order("previousVersion", "asc")
		}
		
		results
	} 
	
}
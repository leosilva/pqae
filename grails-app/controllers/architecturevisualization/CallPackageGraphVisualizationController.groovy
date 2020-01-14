package architecturevisualization

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory
import groovy.json.JsonOutput;

@Transactional(readOnly = true)
class CallPackageGraphVisualizationController {
	
	def callGraphVisualizationService
	def perfMinerIntegrationFilesService
	
    def callPackageGraphVisualization() {
		def force = params.force
		def an = null
		
		if (params.scenarioName && params.systemName && params.previousVersion && params.nextVersion) {
			def result = callGraphVisualizationService.searchResultByAnalyzedScenario(params)
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
			
			def scenarioPV = callGraphVisualizationService.searchPreviousVersionScenario(params)
			def scenarioNV = callGraphVisualizationService.searchNextVersionScenario(params)
			
			def blamedScenario = files*.scenarios.flatten().find { it.scenarioName == scenarioNV.name }
			
			def nodesPV = callGraphVisualizationService.searchPreviousVersionNode(scenarioPV)
			def nodesNV = callGraphVisualizationService.searchNextVersionNode(scenarioNV)

			def addedNodes = new HashSet()
			addedNodes = callGraphVisualizationService.determineAddedNodes(addedNodes, blamedScenario, nodesNV)
			
			def removedNodes = new HashSet()
			removedNodes = callGraphVisualizationService.determineRemovedNodes(removedNodes, blamedScenario, nodesPV)
			
			nodesToVisualization = callGraphVisualizationService.x(nodesNV, nodesToVisualization)
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
			
			render view: "callPackageGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info, backPage : params.targetUri, pageTitle : g.message(code: "application.pageTitle.callGraphVisualization"), history : dataForHistory as JSON]
		} else if (an) {
			def dataInicial = new Date();
			
			def deviationPercentage, dif
			if (an.isDegraded) {
				dif = an.nextTime - an.previousTime
				deviationPercentage = (dif*100)/an.previousTime
			} else {
				dif = an.previousTime - an.nextTime
				deviationPercentage = (dif*100)/an.previousTime
			}
					
			def adnd = JSON.parse(an.jsonNodesToVisualization)["nodes"].findAll { it.isAddedNode == true }
			def rmnd = JSON.parse(an.jsonNodesToVisualization)["nodes"].findAll { it.isRemovedNode == true }
			
			def dn = JSON.parse(an.jsonNodesToVisualization)["nodes"].findAll { it.hasDeviation == true && it.deviation == "degradation" && it.isAddedNode == false && it.isRemovedNode == false}
			def mapDn = dn.groupBy {it.member}
			
			def on = JSON.parse(an.jsonNodesToVisualization)["nodes"].findAll { it.hasDeviation == true && it.deviation == "optimization" && it.isAddedNode == false && it.isRemovedNode == false}
			def mapOn = on.groupBy {it.member}
			
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
				"qtdOptimizedMethods" : mapOn.size(),
				"qtdDegradedNodes" : an.qtdDegradedNodes,
				"qtdDegradedMethods" : mapDn.size(),
				"qtdAddedMethods" : an.qtdAddedMethods,
				"qtdAddedNodes" : adnd.size(),
				"qtdRemovedMethods" : an.qtdRemovedMethods,
				"qtdRemovedNodes" : rmnd.size(),
				"showingNodes" : an.qtdShowingNodes,
				"isDegraded" : an.isDegraded,
				"deviationPercentage" : deviationPercentage
			]
			def affectedNodesJSON = an.jsonNodesToVisualization
			(affectedNodesJSON, info) = callGraphVisualizationService.defineGrupedBlocksByPackage(an.jsonNodesToVisualization, info)
			
			def dataForHistory = getDataForHistory(an.analyzedSystem.systemName, an.name)
			
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
			println "Duração: ${analysisDuration}"

			// println affectedNodesJSON.class
			// println an.jsonNodesToVisualization
			
			render view: "callPackageGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info, backPage : params.targetUri, pageTitle : g.message(code: "application.pageTitle.callGraphVisualization"), history : dataForHistory as JSON]
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
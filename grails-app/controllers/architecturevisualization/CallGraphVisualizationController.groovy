package architecturevisualization

import java.nio.ReadOnlyBufferException;

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory

@Transactional(readOnly = true)
class CallGraphVisualizationController {
	
	def callGraphVisualizationService
	def perfMinerIntegrationFilesService
	def scenarioBatchProcessorService
	
	def showDeviationScenarios() {
		def files = perfMinerIntegrationFilesService.readBlamedMethodsScenariosFile(params.systemName, params.previousVersion, params.nextVersion)
		def scenarios = files.collect { it.scenarios*.scenarioName }.flatten()
		render view : "showDeviationScenarios", model : [scenarios: scenarios]
	}
	
	@Transactional(readOnly = false)
	def batchProcess() {
		scenarioBatchProcessorService.doBatchProcess("Jetty-Servlet", "9.2.6", "9.3.0.M1")
	}

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
			def nodesToVisualization = new HashSet()
			def nodesWithoutParent = new HashSet()
			def groupedNodes = new HashSet()
			
			def files = perfMinerIntegrationFilesService.readBlamedMethodsScenariosFile(params.systemName, params.previousVersion, params.nextVersion)
			
			def scenarioPV = Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
			def scenarioNV = Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
			
			def fileScenario = files*.scenarios.flatten().find { it.scenarioName == scenarioNV.name }
			
			def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
			
			def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])

			def addedNodes = new HashSet()
			addedNodes = callGraphVisualizationService.determineAddedNodes(addedNodes, nodesPV, nodesNV)
			def removedNodes = new HashSet()
			removedNodes = callGraphVisualizationService.determineRemovedNodes(removedNodes, nodesPV, nodesNV)
			
			nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesNV, nodesToVisualization)
			nodesToVisualization = callGraphVisualizationService.searchMethodsWithDeviation(nodesToVisualization, fileScenario, nodesNV, addedNodes)

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
				"showingNodes" : nodesToVisualization.size(),
				"isDegraded" : fileScenario.isDegraded
			]
			
			def affectedNodesJSON = (affectedNodes as JSON)
    		            		 
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
			println "Duração: ${analysisDuration}"

			callGraphVisualizationService.saveAnalyzedSystem(info, analysisDuration, affectedNodesJSON)
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info]
		} else if (an) {
			def dataInicial = new Date();
			
			def info = [
				"totalNodes" : an.totalNodes,
				"deviationNodes" : an.qtdDeviationNodes,
				"scenarioName" : an.name,
				"system" : an.analyzedSystem.systemName,
				"versionFrom" : an.analyzedSystem.previousVersion,
				"versionTo" : an.analyzedSystem.nextVersion,
				"broadScenarioTime" : an.broadTime,
				"addedNodes" : an.qtdAddedNodes,
				"removedNodes" : an.qtdRemovedNodes,
				"showingNodes" : an.qtdShowingNodes,
				"isDegraded" : an.isDegraded
			]
			
			def affectedNodesJSON = an.jsonNodesToVisualization
			
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
			println "Duração: ${analysisDuration}"
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info]
		}
	}
	
}

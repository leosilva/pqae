package architecturevisualization

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.sql.Sql
import groovy.time.TimeCategory

@Transactional(readOnly = true)
class CallGraphVisualizationController {
	
	def callGraphVisualizationService
	def perfMinerIntegrationFilesService
	def scenarioBatchProcessorService
	def postgreSQLService
	
	def restoreDatabase() {
		def sy = params.systemName
		def ds = params.dataSources
		def vs = params.versions
		def dsSplitted = ds.split(",")
		def vsSplitted = vs.split(",")
		dsSplitted.eachWithIndex { i, index ->
			postgreSQLService.recriateSchema(i)
			postgreSQLService.restoreDatabase(i, vsSplitted[index], sy)
		}
	}
	
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
			def nodesToVisualization = []
			def nodesWithoutParent = []
			def groupedNodes = new HashSet()
			
			def d1 = new Date();
			def files = perfMinerIntegrationFilesService.readBlamedMethodsScenariosFile(params.systemName, params.previousVersion, params.nextVersion)
			def d2 = new Date();
			println "Duração perfMinerIntegrationFilesService.readBlamedMethodsScenariosFile(): ${TimeCategory.minus(d2, d1)}"
			
			def d3 = new Date();
			def scenarioPV = Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
			def d4 = new Date();
			println "Duração Scenario.msrPreviousVersion.executeQuery(): ${TimeCategory.minus(d4, d3)}"
			
			def d5 = new Date();
			def scenarioNV = Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: params.scenarioName], [max: 1]).first()
			def d6 = new Date();
			println "Duração Scenario.msrNextVersion.executeQuery(): ${TimeCategory.minus(d6, d5)}"
			
			def d7 = new Date();
			def blamedScenario = files*.scenarios.flatten().find { it.scenarioName == scenarioNV.name }
			def d8 = new Date();
			println "Duração files*.scenarios.flatten().find(): ${TimeCategory.minus(d8, d7)}"
			
			// pode ser melhorado com o fetch
			def d9 = new Date();
			def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
			def d10 = new Date();
			println "Duração NodeScenario.msrPreviousVersion.executeQuery(): ${TimeCategory.minus(d10, d9)}"
			
			def d11 = new Date();
			def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])
			def d12 = new Date();
			println "Duração NodeScenario.msrNextVersion.executeQuery(): ${TimeCategory.minus(d12, d11)}"

			def d13 = new Date();
			def addedNodes = new HashSet()
			addedNodes = callGraphVisualizationService.determineAddedNodes(addedNodes, blamedScenario, nodesNV)
			def d14 = new Date();
			println "Duração callGraphVisualizationService.determineAddedNodes(): ${TimeCategory.minus(d14, d13)}"
			
			def d15 = new Date();
			def removedNodes = new HashSet()
			removedNodes = callGraphVisualizationService.determineRemovedNodes(removedNodes, blamedScenario, nodesPV)
			def d16 = new Date();
			println "Duração callGraphVisualizationService.determineRemovedNodes(): ${TimeCategory.minus(d16, d15)}"
			
			def d17 = new Date();
			nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesNV, nodesToVisualization)
			def d18 = new Date();
			println "Duração callGraphVisualizationService.searchRootNode(): ${TimeCategory.minus(d18, d17)}"
			
			def d19 = new Date();
			nodesToVisualization = callGraphVisualizationService.searchMethodsWithDeviation(nodesToVisualization, blamedScenario, nodesNV)
			def d20 = new Date();
			println "Duração callGraphVisualizationService.searchMethodsWithDeviation(): ${TimeCategory.minus(d20, d19)}"

			def d21 = new Date();
			nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
			def d22 = new Date();
			println "Duração nodesToVisualization.findAll(): ${TimeCategory.minus(d22, d21)}"
			
			def d23 = new Date();
			groupedNodes = callGraphVisualizationService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
			def d24 = new Date();
			println "Duração callGraphVisualizationService.defineGrupedBlocksToParents(): ${TimeCategory.minus(d24, d23)}"
			
			def d25 = new Date();
			groupedNodes = callGraphVisualizationService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
			def d26 = new Date();
			println "Duração callGraphVisualizationService.defineGrupedBlocksToChildren(): ${TimeCategory.minus(d26, d25)}"
			
			def d27 = new Date();
			groupedNodes = callGraphVisualizationService.determineSiblingsForAddedNodes(groupedNodes, addedNodes, nodesToVisualization)
			def d28 = new Date();
			println "Duração callGraphVisualizationService.determineSiblingsForAddedNodes(): ${TimeCategory.minus(d28, d27)}"

			def d33 = new Date();
			def qtdDeviationNodes = blamedScenario.modifiedMethods?.size()
			def d34 = new Date();
			println "Duração nodesToVisualization.findAll(): ${TimeCategory.minus(d34, d33)}"
			
			def d35 = new Date();
			nodesToVisualization = callGraphVisualizationService.removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes)
			def d36 = new Date();
			println "Duração callGraphVisualizationService.removeAddedNodesFromVisualization(): ${TimeCategory.minus(d36, d35)}"

			def d37 = new Date();
			nodesToVisualization = callGraphVisualizationService.calculateAverageNormalNodeTime(nodesToVisualization, scenarioNV, scenarioPV)
			def d38 = new Date();
			println "Duração callGraphVisualizationService.calculateAverageNormalNodeTime(): ${TimeCategory.minus(d38, d37)}"
			
			def d39 = new Date();
			groupedNodes = callGraphVisualizationService.calculateGroupedNodeTime(nodesToVisualization, groupedNodes)
			def d40 = new Date();
			println "Duração callGraphVisualizationService.calculateGroupedNodeTime(): ${TimeCategory.minus(d40, d39)}"
			
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
			
			def affectedNodesJSON = (affectedNodes as JSON)
    		            		 
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toMilliseconds() / 1000
			println "Duração: ${analysisDuration}"

			callGraphVisualizationService.saveAnalyzedSystem(info, analysisDuration, affectedNodesJSON)
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info, backPage : params.targetUri]
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
			
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
			println "Duração: ${analysisDuration}"
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info, backPage : params.targetUri]
		}
	}
	
}
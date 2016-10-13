package architecturevisualization

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory

@Transactional(readOnly = true)
class CallGraphVisualizationController {
	
	def callGraphVisualizationService
	def perfMinerIntegrationFilesService

    def callGraphVisualization() {
		def force = params.force
		def an = null
		if (params.scenarioName && params.systemName && params.previousVersion && params.nextVersion) {
			an = AnalyzedScenario.executeQuery("select distinct an from AnalyzedScenario an inner join an.analyzedSystem asy where an.name = :name and asy.systemName = :systemName and asy.previousVersion = :previousVersion and asy.nextVersion = :nextVersion", [name : params.scenarioName, systemName: params.systemName, previousVersion : params.previousVersion, nextVersion : params.nextVersion])
		}
		if (force || !an) {
			def dataInicial = new Date();
			def nodesToVisualization = new HashSet()
			def nodesWithoutParent = new HashSet()
			def groupedNodes = new HashSet()
			
			def y = new Date();
			def file = perfMinerIntegrationFilesService.readBlamedMethodsDegradedScenariosFile()
			def z = new Date();
			println "Duração perfMinerIntegrationFilesService.readBlamedMethodsDegradedScenariosFile(): ${TimeCategory.minus(z, y)}"
			
			def a = new Date();
			//def scenarioPV = Scenario.msrPreviousVersion.findByName("Entry point for SSLAsyncIOServletTest.testAsyncIOWritesWithAggregation", [sort: "id", order: "asc"])
			def scenarioPV = Scenario.msrPreviousVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: 'Entry point for SSLAsyncIOServletTest.testAsyncIOWritesWithAggregation'], [max: 1]).first()
			def b = new Date();
			println "Duração Scenario.msrPreviousVersion.findByName(): ${TimeCategory.minus(b, a)}"
			
			def c = new Date();
			//def scenarioNV = Scenario.msrNextVersion.findByName("Entry point for SSLAsyncIOServletTest.testAsyncIOWritesWithAggregation", [sort: "id", order: "asc"])
			def scenarioNV = Scenario.msrNextVersion.executeQuery("select s from Scenario s where s.id in (select max(s1.id) from Scenario s1 where s1.name = :scenarioName group by s1.execution) order by s.id", [scenarioName: 'Entry point for SSLAsyncIOServletTest.testAsyncIOWritesWithAggregation'], [max: 1]).first()
			def d = new Date();
			println "Duração Scenario.msrNextVersion.findByName(): ${TimeCategory.minus(d, c)}"
			
			def e = new Date();
			def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
			def f = new Date();
			println "Duração NodeScenario.msrPreviousVersion.executeQuery(): ${TimeCategory.minus(f, e)}"
			
			def g = new Date();
			def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])
			def h = new Date();
			println "Duração NodeScenario.msrNextVersion.executeQuery(): ${TimeCategory.minus(h, g)}"
			
			def i = new Date();
			def addedNodes = new HashSet()
			addedNodes = callGraphVisualizationService.determineAddedNodes(addedNodes, nodesPV, nodesNV)
			
			def j = new Date();
			println "Duração callGraphVisualizationService.determineAddedNodes(): ${TimeCategory.minus(j, i)}"
			
			def k = new Date();
			def removedNodes = new HashSet()
			removedNodes = callGraphVisualizationService.determineRemovedNodes(removedNodes, nodesPV, nodesNV)
			def l = new Date();
			println "Duração callGraphVisualizationService.determineRemovedNodes(): ${TimeCategory.minus(l, k)}"
			
			// adiciona o no root
			def bef1 = new Date();
			nodesToVisualization = callGraphVisualizationService.searchRootNode(nodesNV, nodesToVisualization)
			def bef2 = new Date();
			println "Duração callGraphVisualizationService.searchRootNode(): ${TimeCategory.minus(bef2, bef1)}"
			
			// determina se houve variacao de desempenho
			def bef3 = new Date();
			//nodesToVisualization = projectService.searchMethodsWithDeviation(nodesPV, nodesNV, nodesToVisualization)
			nodesToVisualization = callGraphVisualizationService.searchMethodsWithDeviation(nodesToVisualization, file, nodesNV, scenarioNV, addedNodes)
			def bef4 = new Date();
			println "Duração callGraphVisualizationService.searchMethodsWithDeviation(): ${TimeCategory.minus(bef4, bef3)}"
			
			def bef5 = new Date();
			nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
			def bef6 = new Date();
			println "Duração nodesWithoutParent: ${TimeCategory.minus(bef6, bef5)}"
			
			def bef9 = new Date();
			groupedNodes = callGraphVisualizationService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
			def bef10 = new Date();
			println "Duração callGraphVisualizationService.defineGrupedBlocksToParents(): ${TimeCategory.minus(bef10, bef9)}"
			
			def bef7 = new Date();
			groupedNodes = callGraphVisualizationService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
			def bef8 = new Date();
			println "Duração callGraphVisualizationService.defineGrupedBlocksToChildren(): ${TimeCategory.minus(bef8, bef7)}"
			
			def bef13 = new Date();
			groupedNodes = callGraphVisualizationService.collectInfoAddedNodes(groupedNodes, addedNodes, nodesToVisualization)
			def bef14 = new Date();
			println "Duração callGraphVisualizationService.collectInfoAddedNodes(): ${TimeCategory.minus(bef14, bef13)}"
			
			def bef11 = new Date();
			def scenarioTime = callGraphVisualizationService.calculateScenarioTime(nodesToVisualization)
			def bef12 = new Date();
			println "Duração callGraphVisualizationService.calculateScenarioTime(): ${TimeCategory.minus(bef12, bef11)}"
			
			def qtdDeviationNodes = nodesToVisualization.findAll { it.hasDeviation == true }.size()
			
			def bef15 = new Date();
			nodesToVisualization = callGraphVisualizationService.removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes)
			def bef16 = new Date();
			println "Duração callGraphVisualizationService.calculateScenarioTime(): ${TimeCategory.minus(bef16, bef15)}"
			
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
    		            		 
    		def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
			println "Duração: ${analysisDuration}"

			callGraphVisualizationService.saveAnalyzedSystem(info, analysisDuration, affectedNodesJSON)
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info]
		} else if (an) {
			
			def dataInicial = new Date();
			
			def file = perfMinerIntegrationFilesService.readBlamedMethodsDegradedScenariosFile()
		
			def info = [
				"totalNodes" : an.totalNodes[0],
				"deviationNodes" : an.qtdDeviationNodes[0],
				"scenarioName" : an.name[0],
				"system" : an.analyzedSystem.systemName[0],
				"versionFrom" : an.analyzedSystem.previousVersion[0],
				"versionTo" : an.analyzedSystem.nextVersion[0],
				"broadScenarioTime" : an.broadTime[0],
				"addedNodes" : an.qtdAddedNodes[0],
				"removedNodes" : an.qtdRemovedNodes[0],
				"showingNodes" : an.qtdShowingNodes[0]
			]
			
			def affectedNodesJSON = an.jsonNodesToVisualization[0]
			
			def dataFinal = new Date();
			def analysisDuration = TimeCategory.minus(dataFinal, dataInicial).toString()
			println "Duração: ${analysisDuration}"
			
			render view: "callGraphVisualization", model: [affectedNodes : affectedNodesJSON, info : info]
		}
	}
	
}

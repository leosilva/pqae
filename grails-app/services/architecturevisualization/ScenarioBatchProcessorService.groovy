package architecturevisualization

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory

import java.math.RoundingMode

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
				
				def scenarioPreviousTime = NodeScenario.msrPreviousVersion.executeQuery("select avg(n.time) from NodeScenario ns inner join ns.node n where n.member = (select n1.member from NodeScenario ns1 inner join ns1.node n1 where ns1.scenario.id = :idScenario and n1.node is null)", [idScenario : scenarioPV.id]).first()
				def scenarioNextTime = NodeScenario.msrNextVersion.executeQuery("select avg(n.time) from NodeScenario ns inner join ns.node n where n.member = (select n1.member from NodeScenario ns1 inner join ns1.node n1 where ns1.scenario.id = :idScenario and n1.node is null)", [idScenario : scenarioNV.id]).first()
			
				def qtdDeviationNodes = nodesToVisualization.findAll { it.hasDeviation == true }.size()
				
				nodesToVisualization = callGraphVisualizationService.removeAddedNodesFromVisualization(nodesToVisualization, groupedNodes)
				
				nodesToVisualization = callGraphVisualizationService.calculateAverageNodeTime(nodesToVisualization, scenarioNV)
				
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
					"scenarioPreviousTime" : scenarioPreviousTime,
					"scenarioNextTime" : scenarioNextTime,
					"addedNodes" : addedNodes.size(),
					"removedNodes" : removedNodes.size(),
					"showingNodes" : nodesToVisualization.size(),
					"isDegraded" : scenario.isDegraded
				]
				
				def affectedNodesJSON = (affectedNodes as JSON)
				
				def d2 = new Date();
				def analysisDuration = TimeCategory.minus(d2, d1).toMilliseconds() / 1000
				
				AnalyzedScenario ansce = new AnalyzedScenario(totalNodes: info.totalNodes as Integer,
					name: info.scenarioName,
					qtdAddedNodes: info.addedNodes as Integer,
					qtdRemovedNodes: info.removedNodes as Integer,
					qtdDeviationNodes: info.deviationNodes as Integer,
					qtdShowingNodes: info.showingNodes as Integer,
					previousTime: info.scenarioPreviousTime as BigDecimal,
					nextTime: info.scenarioNextTime as BigDecimal,
					jsonNodesToVisualization: affectedNodesJSON as String,
					analyzedSystem: ansys,
					date: new Date(),
					analysisDuration: analysisDuration as BigDecimal,
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

package architecturevisualization



import static org.springframework.http.HttpStatus.*

import org.hibernate.FlushMode;

import comparator.NodeComparator;

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonBuilder
import groovy.time.TimeCategory;
import main.DiscoverClassInfo

@Transactional(readOnly = true)
class ProjectController {
	
	def gitService
	def projectService
	
	static scaffold = true
	
	def clone() {
		def project = Project.get(params.id as Long)
		try {
			gitService.clone(project)
			project.isClonned = true
			project.save(flush: true)
			flash.message = "Projeto ${project.name} clonado com sucesso!"
		} catch (Exception ex) {
			println ex
			flash.message = "Erro ao clonar projeto ${project.name}!!"
		}
		redirect action: 'index'
	}
	
	def branchList() {
		gitService.branchList()
	}
	
	def tagList() {
		gitService.tagList()
	}
	
	def findClassInfo() {
		def dci = new DiscoverClassInfo()
		dci.findClassInfo()
		render view: "diagram", model: [map : dci.createBasedPlantUMLFile.map as JSON]
	}
	
	def callGraphVisualization() {
		def dataInicial = new Date();
		def nodesToVisualization = new HashSet()
		def nodesWithoutParent = new HashSet()
		def groupedNodes = new HashSet()
		
		def a = new Date();
		def scenarioPV = Scenario.msrPreviousVersion.findByName("Entry point for AsyncIOServletTest.testAsyncWriteThrowsError", [sort: "id", order: "asc"])
		def b = new Date();
		println "Duração Scenario.msrPreviousVersion.findByName(): ${TimeCategory.minus(b, a)}"
		
		def c = new Date();
		def scenarioNV = Scenario.msrNextVersion.findByName("Entry point for AsyncIOServletTest.testAsyncWriteThrowsError", [sort: "id", order: "asc"])
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
		addedNodes = projectService.determineAddedNodes(addedNodes, nodesPV, nodesNV)
		
		def j = new Date();
		println "Duração addedNodes: ${TimeCategory.minus(j, i)}"
		
		def k = new Date();
		def removedNodes = new HashSet()
		removedNodes = projectService.determineRemovedNodes(removedNodes, nodesPV, nodesNV)
		
		def l = new Date();
		println "Duração removedNodes: ${TimeCategory.minus(l, k)}"
		
		// adiciona o no root
		def bef1 = new Date();
		nodesToVisualization = projectService.searchRootNode(nodesNV, nodesToVisualization)
		def bef2 = new Date();
		println "Duração projectService.searchRootNode(): ${TimeCategory.minus(bef2, bef1)}"

		// determina se houve variacao de desempenho
		def bef3 = new Date();
		nodesToVisualization = projectService.searchMethodsWithDeviation(nodesPV, nodesNV, nodesToVisualization)
		def bef4 = new Date();
		println "Duração projectService.searchMethodsWithDeviation(): ${TimeCategory.minus(bef4, bef3)}"
		
		def bef5 = new Date();
		nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
		def bef6 = new Date();
		println "Duração nodesWithoutParent: ${TimeCategory.minus(bef6, bef5)}"

		def bef7 = new Date();
		groupedNodes = projectService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
		def bef8 = new Date();
		println "Duração projectService.defineGrupedBlocksToChildren(): ${TimeCategory.minus(bef8, bef7)}"

		def bef9 = new Date();
		groupedNodes = projectService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
		def bef10 = new Date();
		println "Duração projectService.defineGrupedBlocksToParents(): ${TimeCategory.minus(bef10, bef9)}"
		
		def bef13 = new Date();
		groupedNodes = projectService.collectInfoAddedNodes(groupedNodes, addedNodes)
		def bef14 = new Date();
		println "Duração projectService.collectInfoAddedNodes(): ${TimeCategory.minus(bef14, bef13)}"
		
		def bef11 = new Date();
		def scenarioTime = projectService.calculateScenarioTime(nodesToVisualization)
		def bef12 = new Date();
		println "Duração projectService.calculateScenarioTime(): ${TimeCategory.minus(bef12, bef11)}"
		
		def qtdDeviationNodes = nodesToVisualization.size()
		
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
		
		def dataFinal = new Date();
		println "Duração: ${TimeCategory.minus(dataFinal, dataInicial)}"
		render view: "callGraphVisualization", model: [affectedNodes : affectedNodes as JSON, info : info]
	}

	

	

	def showScenarios() {
		def scenariosList = Scenario.list().collect {
			["id" : it.id, "name" : it.name, date : it.date]
		}
		render view: "showScenarios", model: ["scenarios" : scenariosList as JSON]
	}
}

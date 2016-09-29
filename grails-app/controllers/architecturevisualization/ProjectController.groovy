package architecturevisualization



import static org.springframework.http.HttpStatus.*

import org.hibernate.FlushMode;

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
		def groupedNodes = []
		def scenarioPV = Scenario.msrPreviousVersion.findByName("Entry point for AsyncIOServletTest.testAsyncWriteThrowsError", [sort: "id", order: "asc"])
		def scenarioNV = Scenario.msrNextVersion.findByName("Entry point for AsyncIOServletTest.testAsyncWriteThrowsError", [sort: "id", order: "asc"])
		
		def nodesPV = NodeScenario.msrPreviousVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id])
		def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct n from NodeScenario ns inner join ns.node n where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id])
		
		// determina se houve variacao de desempenho
		nodesToVisualization = projectService.searchMethodsWithDeviation(nodesPV, nodesNV, nodesToVisualization)
		
		// adiciona o no root
		nodesToVisualization = projectService.searchRootNode(nodesNV, nodesToVisualization)
		
		nodesWithoutParent = nodesToVisualization.findAll { n-> nodesToVisualization.every { it.id != n?.node?.id } }
		
		groupedNodes = projectService.defineGrupedBlocksToChildren(nodesToVisualization, groupedNodes)
		
		groupedNodes = projectService.defineGrupedBlocksToParents(nodesWithoutParent, nodesToVisualization, groupedNodes)
		
		nodesToVisualization.addAll(groupedNodes)
		println nodesToVisualization.size()
		
		def affectedNodes = [
			"nodes" : nodesToVisualization
		]
		
		def info = [
			"totalNodes" : nodesNV.size(),
			"affectedNodes" : nodesToVisualization.size()
		]
		
		def dataFinal = new Date();
		println "Duração: ${TimeCategory.minus(dataFinal, dataInicial)}"
		render view: "callGraphVisualization", model: [affectedNodes : affectedNodes as JSON, info : info as JSON, scenarioPV: scenarioPV, scenarioNV : scenarioNV]
	}

	

	

	def showScenarios() {
		def scenariosList = Scenario.list().collect {
			["id" : it.id, "name" : it.name, date : it.date]
		}
		render view: "showScenarios", model: ["scenarios" : scenariosList as JSON]
	}
}

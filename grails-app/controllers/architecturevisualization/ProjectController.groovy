package architecturevisualization



import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonBuilder
import groovy.time.TimeCategory;
import main.DiscoverClassInfo

@Transactional(readOnly = true)
class ProjectController {
	
	def gitService
	
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
		def scenarioPV = Scenario.get(1)
		def scenarioNV = Scenario.msrNextVersion.get(100)
		
		def nodesPV = NodeScenario.executeQuery("select distinct ns from NodeScenario ns where ns.scenario.id = :idScenario", [idScenario : scenarioPV.id]).collect {it.node}
		def nodesNV = NodeScenario.msrNextVersion.executeQuery("select distinct ns from NodeScenario ns where ns.scenario.id = :idScenario", [idScenario : scenarioNV.id]).collect {it.node}
		
		nodesPV.each { pv ->
			nodesNV.each { nv ->
				if (nv.member == pv.member) {
					if (nv.time < pv.time) {
						nv.deviation = "improvement"
						nv.timeVariation = pv.time - nv.time
						nv.timeVariationSignal = "-" 
					} else if (nv.time > pv.time) {
						nv.deviation = "degradation"
						nv.timeVariation = nv.time - pv.time
						nv.timeVariationSignal = "+"
					}
				}
			}
		}
		
		def mapPreviousVersion = [
			"nodes" : nodesPV
		]
		
		def mapNextVersion = [
            "nodes" : nodesNV
        ]
		
		def dataFinal = new Date();
		println "Duração: ${TimeCategory.minus(dataFinal, dataInicial)}"
		render view: "callGraphVisualization", model: [mapPreviousVersion : mapPreviousVersion as JSON, mapNextVersion : mapNextVersion as JSON, scenarioPV: scenarioPV, scenarioNV : scenarioNV]
	}
	
	def showScenarios() {
		def scenariosList = Scenario.list().collect {
			["id" : it.id, "name" : it.name, date : it.date]
		}
		render view: "showScenarios", model: ["scenarios" : scenariosList as JSON]
	}
}

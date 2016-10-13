package architecturevisualization



import static org.springframework.http.HttpStatus.*
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.time.TimeCategory
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

	def showScenarios() {
		def scenariosList = Scenario.list().collect {
			["id" : it.id, "name" : it.name, date : it.date]
		}
		render view: "showScenarios", model: ["scenarios" : scenariosList as JSON]
	}
	
}

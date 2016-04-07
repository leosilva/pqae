package architecturevisualization



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class ProjectController {
	
	def gitService
	
	static scaffold = true
	
	def clone() {
		gitService.clone()
	}
}

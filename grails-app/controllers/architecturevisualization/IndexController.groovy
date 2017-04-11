package architecturevisualization

import grails.util.Environment;


class IndexController {

    def index() {
		if (Environment.getCurrent() == Environment.PRODUCTION) {
			render view : "/error/error", model : [errorTitle : g.message(code: "error.index.heroku.title"), errorMessage: g.message(code: "error.index.heroku.message")]
			return
		}
		def ans = AnalyzedSystem.list()
		ans = ans.groupBy { it.systemName }
		ans.each {
			it.value = it.value.flatten().sort { it.previousVersion }
		}
		render view : "index", model : [ans: ans, pageTitle: g.message(code: "application.pageTitle.analyzedSystems"), rootUri: g.createLink(uri : '/'), ajaxUri: g.createLink(controller: 'index', action: 'refreshSystemStatus')]
	}
	
	def refreshSystemStatus() {
		def ans = AnalyzedSystem.list()
		ans = ans.groupBy { it.systemName }
		ans.each {
			it.value = it.value.flatten().sort { it.previousVersion }
		}
		render template : "indexSystemsTable", model : [ans: ans]
	}
}

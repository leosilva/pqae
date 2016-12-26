package architecturevisualization

class IndexController {

    def index() {
		def ans = AnalyzedSystem.list()
		def an = new AnalyzedSystem(systemName : "Jetty-Servlet", previousVersion : "9.1.0", nextVersion : "9.2.0")
		def an1 = new AnalyzedSystem(systemName : "Netty", previousVersion : "1.0", nextVersion : "2.0")
		def an2 = new AnalyzedSystem(systemName : "Netty", previousVersion : "2.0", nextVersion : "3.0")
		ans += an
		ans += an1
		ans += an2
		ans = ans.groupBy { it.systemName }
		ans.each {
			it.value = it.value.flatten().sort { it.previousVersion }
		}
		render view : "index", model : [ans: ans, pageTitle: g.message(code: "application.pageTitle.analyzedSystems")]
	}
}

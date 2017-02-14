package architecturevisualization

class IndexController {

    def index() {
		def ans = AnalyzedSystem.list()
//		def ans = []
//		def an = new AnalyzedSystem(systemName : "System A", previousVersion : "1.0", nextVersion : "2.0", analyzedSystemStatus: AnalyzedSystemStatus.COMPLETED)
//		def an1 = new AnalyzedSystem(systemName : "System A", previousVersion : "2.0", nextVersion : "3.0", analyzedSystemStatus: AnalyzedSystemStatus.COMPLETED)
//		def an2 = new AnalyzedSystem(systemName : "System A", previousVersion : "3.0", nextVersion : "4.0", analyzedSystemStatus: AnalyzedSystemStatus.PENDING)
//		//def an3 = new AnalyzedSystem(systemName : "System A", previousVersion : "4.0", nextVersion : "5.0", analyzedSystemStatus: AnalyzedSystemStatus.PENDING)
//		def an4 = new AnalyzedSystem(systemName : "System B", previousVersion : "1.0", nextVersion : "2.0", analyzedSystemStatus: AnalyzedSystemStatus.COMPLETED)
//		def an5 = new AnalyzedSystem(systemName : "System B", previousVersion : "2.0", nextVersion : "3.0", analyzedSystemStatus: AnalyzedSystemStatus.COMPLETED)
//		def an6 = new AnalyzedSystem(systemName : "System B", previousVersion : "3.0", nextVersion : "4.0", analyzedSystemStatus: AnalyzedSystemStatus.COMPLETED)
//		def an7 = new AnalyzedSystem(systemName : "System B", previousVersion : "4.0", nextVersion : "5.0", analyzedSystemStatus: AnalyzedSystemStatus.ERROR)
//		//def an8 = new AnalyzedSystem(systemName : "System B", previousVersion : "5.0", nextVersion : "6.0", analyzedSystemStatus: AnalyzedSystemStatus.COMPLETED)
//		//def an9 = new AnalyzedSystem(systemName : "System B", previousVersion : "6.0", nextVersion : "7.0", analyzedSystemStatus: AnalyzedSystemStatus.ERROR)
//		ans += an
//		ans += an1
//		ans += an2
//		//ans += an3
//		ans += an4
//		ans += an5
//		ans += an6
//		ans += an7
//		//ans += an8
//		//ans += an9
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

package architecturevisualization

class IndexController {

    def index() {
		def ans = AnalyzedSystem.list()
		render view : "index", model : [ans: ans]
	}
}

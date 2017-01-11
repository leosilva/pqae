package architecturevisualization

class ErrorController {

    def error505() {
		def errorTitle = g.message(code: 'error.500.title')
		def errorMessage = g.message(code: 'error.500.message')
		render view: "error", model: [errorCode: "500", errorTitle: errorTitle, errorMessage: errorMessage]
	}
	
	def error404() {
		def errorTitle = g.message(code: 'error.404.title')
		def errorMessage = g.message(code: 'error.404.message')
		render view: "error", model: [errorCode: "404", errorTitle: errorTitle, errorMessage: errorMessage]
	}
}

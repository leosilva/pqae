package architecturevisualization

import grails.converters.JSON

class DeviationScenariosController {

    def index() {
		def analyzedSystem = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersion(params.systemName, params.previousVersion, params.nextVersion)
		def lista = []
		analyzedSystem.analyzedScenarios.eachWithIndex { v, i ->
			def width, dif
			if (v.isDegraded) {
				dif = v.nextTime - v.previousTime
				width = (dif*100)/v.previousTime
			} else {
				dif = v.previousTime - v.nextTime
				width = (dif*100)/v.previousTime
			}
			lista += (["id" : "${v.id}", "order": "${i}", "isDegraded" : "${v.isDegraded}", "weight" : "${v.nextTime}",
				"score" : "${v.nextTime}", "width" : "${width}", "label" : "${v.name}",
				"url" : g.createLink(action: "callGraphVisualization", controller:"callGraphVisualization", absolute: true, params: ["systemName" : analyzedSystem.systemName, "scenarioName" : v.name, "previousVersion" : analyzedSystem.previousVersion, "nextVersion" : analyzedSystem.nextVersion])])
		}
		
		render view : "index", model : [scenarios: (lista as JSON), analyzedSystem : analyzedSystem]
	}
}

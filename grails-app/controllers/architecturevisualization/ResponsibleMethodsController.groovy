package architecturevisualization

import grails.converters.JSON

class ResponsibleMethodsController {

    def showResponsibleMethods() {
		def analyzedSystem = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersionAndAnalyzedSystemStatus(params.systemName, params.previousVersion, params.nextVersion, AnalyzedSystemStatus.COMPLETED)
		def map = [:]
		analyzedSystem.analyzedScenarios.each { s ->
			s.responsibleMethods.each { rm ->
				if (!map[rm.methodSignature]) {
					map[rm.methodSignature] = []
				}
				map[rm.methodSignature].add(s.name)
			}
		}
		
		def urlsMap = [:]
		analyzedSystem.analyzedScenarios.each { s ->
			urlsMap[s.name] = g.createLink(action: "callGraphVisualization", controller:"callGraphVisualization", absolute: true, params: ["systemName" : params.systemName, "scenarioName" : s.name, "previousVersion" : params.previousVersion, 
					"nextVersion" : params.nextVersion, "targetUri" : g.createLink(controller:  params.controller, action: params.action, params : params)])
		}
		
		render view: "index", model: [responsibleMethods : (map as JSON), urlsMap : (urlsMap as JSON), backPage : params.targetUri, pageTitle : g.message(code: "application.pageTitle.networkGraph")]
		
	}
	
}

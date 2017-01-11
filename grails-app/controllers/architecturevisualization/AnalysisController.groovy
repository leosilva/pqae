package architecturevisualization

import grails.converters.JSON

class AnalysisController {
	
	def amazonAWSService
	def scenarioBatchProcessorService
	def postgreSQLService
	def sessionFactory_msrPreviousVersion
	def sessionFactory_msrNextVersion

    def startAnalysis() {
		def mapSystems = amazonAWSService.listSystems()
		def typeaheadJson = mapSystems.keySet()
		
		render view : "analysis", model : [systems : typeaheadJson, pageTitle : g.message(code: "application.pageTitle.newAnalysis"), ajaxURL : g.createLink(action : "findBackupsBySystemName", controller : "analysis"), rootURL : g.createLink(uri : "/"), backPage : params.targetUri]
	}
	
	def findBackupsBySystemName() {
		def systems = amazonAWSService.listSystems()
		def returnMap = [:]
		systems.each {
			if (it.key == params.systemName) {
				returnMap = it.value
			}
		}
		render returnMap as JSON
	}
	
	def processAndSaveAnalysis() {
		def ansys = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersion(params.systemName, params.previousVersion, params.nextVersion)
		if (!ansys) {
			def hasPendingAnalysis = AnalyzedSystem.findByAnalyzedSystemStatus(AnalyzedSystemStatus.PENDING)
			if (!hasPendingAnalysis) {
				scenarioBatchProcessorService.preSaveAnalyzedSystem(params.systemName, params.previousVersion, params.nextVersion)
				
				def backupPreviousVersion = amazonAWSService.downloadFile(params.systemName, new File(params.backupFilePreviousVersion))
				def backupNextVersion = amazonAWSService.downloadFile(params.systemName, new File(params.backupFileNextVersion))
				
				postgreSQLService.destroyAndRestoreDatabase(params.systemName, params.previousVersion, params.nextVersion, backupPreviousVersion, backupNextVersion, params.backupFilePreviousVersion, params.backupFileNextVersion)
				scenarioBatchProcessorService.doBatchProcess(params.systemName, params.previousVersion, params.nextVersion, params.resultFileDegradedScenarios, params.resultFileOptimizedScenarios)
				postgreSQLService.destroySchema()
				
				flash.message = true
				flash.alertClass = g.message(code: "defaul.message.success.class")
				flash.alertTitle = g.message(code: "defaul.message.success.title")
				flash.alertMessage = g.message(code: "newAnalysis.success.message", args: "[params.previousVersion, params.nextVersion]")
			} else {
				flash.message = true
				flash.alertClass = g.message(code: "defaul.message.error.class")
				flash.alertTitle = g.message(code: "defaul.message.error.title")
				flash.alertMessage = g.message(code: "newAnalysis.error.analysisInProgress")
			}
		} else {
			flash.message = true
			flash.alertClass = g.message(code: "defaul.message.error.class")
			flash.alertTitle = g.message(code: "defaul.message.error.title")
			flash.alertMessage = g.message(code: "newAnalysis.error.analysisExistis")
		}
		redirect(action : "startAnalysis", params : params)
	}
	
	def deleteAnalysis() {
		def ansys = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersion(params.systemName, params.previousVersion, params.nextVersion)
		if (ansys) {
			ansys.delete(flush: true)
		}
		
		ansys?.errors?.allErrors?.each {
			println it
		}
		
		redirect(controller: 'index', action: 'index')
	}
}

package architecturevisualization

import grails.converters.JSON

class AnalysisController {
	
	def amazonAWSService
	def scenarioBatchProcessorService
	def postgreSQLService

    def startAnalysis() {
		def mapSystems = amazonAWSService.listSystems()
		def typeaheadJson = mapSystems.keySet()
		
		render view : "analysis", model : [systems : typeaheadJson, pageTitle : g.message(code: "application.pageTitle.newAnalysis"), ajaxURL : g.createLink(action : "findBackupsBySystemName", controller : "analysis"), backPage : params.targetUri]
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
		def backupPreviousVersion = amazonAWSService.downloadFile(params.systemName, new File(params.backupFilePreviousVersion))
		def backupNextVersion = amazonAWSService.downloadFile(params.systemName, new File(params.backupFileNextVersion))
		
		postgreSQLService.destroyAndRestoreDatabase(params.systemName, params.previousVersion, params.nextVersion, backupPreviousVersion, backupNextVersion, params.backupFilePreviousVersion, params.backupFileNextVersion)
		
		scenarioBatchProcessorService.doBatchProcess(params.systemName, params.previousVersion, params.nextVersion, params.resultFileDegradedScenarios, params.resultFileOptimizedScenarios)
		
		postgreSQLService.destroySchema()
		
		flash.message = true
		flash.alertClass = "alert-success"
		flash.alertTitle = "Success!"
		flash.alertMessage = "Analysis processed successfully for version ${params.previousVersion} to ${params.nextVersion}."
		
		redirect(action : "startAnalysis")
	}
}

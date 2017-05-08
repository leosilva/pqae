package architecturevisualization

import grails.converters.JSON

import java.nio.file.Path
import java.nio.file.Paths

class AnalysisController {
	
	final def tempPath = "repositories/backups/"
	def scenarioBatchProcessorService
	def postgreSQLService
	def sessionFactory_msrPreviousVersion
	def sessionFactory_msrNextVersion
	def callGraphVisualizationService

    def startAnalysis() {
		def typeaheadJson = AnalyzedSystem.executeQuery('select distinct systemName from AnalyzedSystem')
		render view : "analysis", model : [typeahead : typeaheadJson, pageTitle : g.message(code: "application.pageTitle.newAnalysis"), ajaxURL : g.createLink(action : "findBackupsBySystemName", controller : "analysis"), rootURL : g.createLink(uri : "/", absolute: true), backPage : params.targetUri]
	}
	
	def processAndSaveAnalysis() {
			def ansys = AnalyzedSystem.findBySystemNameAndPreviousVersionAndNextVersion(params.systemName, params.previousVersion, params.nextVersion)
			if (!ansys) {
				def hasPendingAnalysis = AnalyzedSystem.findByAnalyzedSystemStatus(AnalyzedSystemStatus.PENDING)
				if (!hasPendingAnalysis) {
					def path1, path2
					try {
						scenarioBatchProcessorService.preSaveAnalyzedSystem(params.systemName, params.previousVersion, params.nextVersion)
						
						def backupPreviousVersion = request.getFile("backupFilePreviousVersion")
						
						Path dir = Paths.get(tempPath)
						def systemName = params.systemName.replace(" ", "-")
						new File("${dir.toAbsolutePath().toString()}/${params.systemName}").exists() ?: new File("${dir.toAbsolutePath().toString()}/${params.systemName}").mkdirs()
						path1 = "${dir.toAbsolutePath().toString()}/${params.systemName}/${backupPreviousVersion.getOriginalFilename()}"
						backupPreviousVersion.transferTo(new File(path1))

						def backupNextVersion = request.getFile("backupFileNextVersion")
												
						Path dir1 = Paths.get(tempPath)
						path2 = "${dir.toAbsolutePath().toString()}/${params.systemName}/${backupNextVersion.getOriginalFilename()}"
						backupNextVersion.transferTo(new File(path2))

						postgreSQLService.destroyAndRestoreDatabase(params.systemName, params.previousVersion, params.nextVersion, new File(path1).toPath(), new File(path2).toPath(), params.backupFilePreviousVersion, params.backupFileNextVersion)
						scenarioBatchProcessorService.doBatchProcess(params.systemName, params.previousVersion, params.nextVersion, params.resultFileDegradedScenarios, params.resultFileOptimizedScenarios)
						flash.message = true
						flash.alertClass = g.message(code: "defaul.message.success.class")
						flash.alertTitle = g.message(code: "defaul.message.success.title")
						flash.alertMessage = g.message(code: "newAnalysis.success.message", args: "[params.previousVersion, params.nextVersion]")
					} catch (Exception e) {
						callGraphVisualizationService.updateAnalyzedSystem(params.systemName, params.previousVersion, params.nextVersion, AnalyzedSystemStatus.ERROR)
						e.printStackTrace()
						flash.message = true
						flash.alertClass = g.message(code: "defaul.message.error.class")
						flash.alertTitle = g.message(code: "defaul.message.error.title")
						flash.alertMessage = g.message(code: "newAnalysis.error.message")
					}
					postgreSQLService.destroySchema()
					new File(path1).delete()
					new File(path2).delete()
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
		
		redirect(controller: 'index', action: 'index')
	}
}

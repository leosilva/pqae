package architecturevisualization

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes

class ConfigController {
	
	final def tempPath = "repositories/backups/"
	def amazonAWSService

    def preBackupUpload() {
		deleteFromTempDir()
		def mapSystems = amazonAWSService.listSystems()
		def typeaheadJson = mapSystems.keySet()
		
		render view : "backupUpload", model : [mapSystems : mapSystems, typeahead : typeaheadJson, pageTitle : g.message(code: "application.pageTitle.uploadABackup"), backPage : params.targetUri]
	}

	def saveBackupUpload() {
		def file = request.getFile("backupFile")
		Path dir = Paths.get(tempPath)
		def systemName = params.systemName.replace(" ", "-")
		new File("${dir.toAbsolutePath().toString()}/${systemName}").exists() ?: new File("${dir.toAbsolutePath().toString()}/${systemName}").mkdir() 
		def path = "${dir.toAbsolutePath().toString()}/${systemName}/${file.getOriginalFilename()}"
		file.transferTo(new File(path))
		amazonAWSService.uploadFile(new File(path), systemName)
		
		flash.message = true
		flash.alertClass = "alert-success"
		flash.alertTitle = "Success!"
		flash.alertMessage = "Backup file saved successfully."
		
		redirect(action : "preBackupUpload")
	}
	
	def deleteFromTempDir() {
		new File(tempPath).listFiles().each {
			if (it.isDirectory()) {
				it.deleteDir()
			} else {
				it.delete()
			}
		}
	}
	
	def deleteBackupFile() {
		amazonAWSService.deleteFile(new File(params.file), params.systemName)
		redirect(action : "preBackupUpload")
	}
	
	def downloadFile() {
		InputStream contentStream
		try {
			def fileName = params.file.toString()
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1)
			response.setHeader "Content-disposition", "attachment; filename=${fileName}"
			response.setHeader("Content-Length", "file-size")
			response.setContentType("file-mime-type")
			response.outputStream << params.file
			def webRequest = request.getAttribute(GrailsApplicationAttributes.WEB_REQUEST)
			webRequest.setRenderView(false) 
		} finally {
			IOUtils.closeQuietly(contentStream)
		}
	}

}

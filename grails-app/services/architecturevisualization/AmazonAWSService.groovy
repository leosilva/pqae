package architecturevisualization

import grails.transaction.Transactional

import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest

@Transactional
class AmazonAWSService {
	
	def amazonWebService
	def preffix = "backups/"
	def bucketName = "apvis-assets"
	
    def uploadFile(file, systemName) {
		def key = preffix + systemName + "/" + file.name
		amazonWebService.s3.putObject(new PutObjectRequest(bucketName, key, file).withCannedAcl(CannedAccessControlList.PublicRead))
    }
	
	def listSystems() {
		def mapSystems = [:]
		ObjectListing l = amazonWebService.s3.listObjects(bucketName)
		l.getObjectSummaries().each {
			if (it.key.contains(preffix)) {
				def map = [:]
				def key = it.key.substring(it.key.indexOf("/") + 1, it.key.lastIndexOf("/"))
				def value = it.key.substring(it.key.lastIndexOf("/") + 1)
				map << ["${value}" : ["path" : amazonWebService.s3.getResourceUrl(bucketName, it.key), "size" : it.size]]
				if (mapSystems.containsKey("${key}")) {
					Map mapa = mapSystems.get("${key}")
					mapa << ["${value}" : ["path" : amazonWebService.s3.getResourceUrl(bucketName, it.key), "size" : it.size]]
					mapSystems.put("${key}", mapa)
				} else {
					mapSystems << ["${key}" : map]
				}
			}
		}
		mapSystems as Map
	}
	
	def deleteFile(file, systemName) {
		def key = preffix + systemName + "/" + file.name
		amazonWebService.s3.deleteObject(bucketName, key)
	}
}

package architecturevisualization

import grails.transaction.Transactional
import grails.util.Environment
import groovy.sql.Sql
import groovy.time.TimeCategory

import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Transactional
class PostgreSQLService {
	
	def dataSource_msrPreviousVersion
	def dataSource_msrNextVersion
	def grailsApplication
	
	def destroyAndRestoreDatabase(systemName, previousVersion, nextVersion, backupPreviousVersion, backupNextVersion, backupFilePreviousVersionName, backupFileNextVersionName) {
		def dspv = dataSource_msrPreviousVersion.connection.catalog
		def dsnv = dataSource_msrNextVersion.connection.catalog
		
		recriateSchema(new Sql(dataSource_msrPreviousVersion))
		restoreDatabase(dspv, previousVersion, systemName, backupPreviousVersion, grailsApplication.config.dataSource_msrPreviousVersion.password, backupFilePreviousVersionName)
		
		recriateSchema(new Sql(dataSource_msrNextVersion))
		restoreDatabase(dsnv, nextVersion, systemName, backupNextVersion, grailsApplication.config.dataSource_msrPreviousVersion.password, backupFileNextVersionName)
	}
	
	def destroySchema() {
		recriateSchema(new Sql(dataSource_msrPreviousVersion))
		recriateSchema(new Sql(dataSource_msrNextVersion))
	}
	
    def recriateSchema(sql) {
		def dataInicial = new Date();
		println "starting recriating schema..."
		
		sql.call("set transaction read write")
		sql.call("DROP SCHEMA IF EXISTS public CASCADE;")
		sql.call("CREATE SCHEMA public")
		sql.call("commit")
		
		println "finish recriating schema..."
		def dataFinal = new Date();
		def duration = TimeCategory.minus(dataFinal, dataInicial).toMilliseconds() / 1000
		println "Duração: ${duration}"
    }
	
	def restoreDatabase(databaseName, v, sy, backupVersion, password, backupFileName) {
		def dataInicial = new Date();
		println "starting database restore..."
		
		def filePath = "backups/" + sy + "/" + backupFileName
		
		def f = new File(filePath)
		
		if (!f.exists()) {
			f.mkdirs()
			f.createNewFile()
		}
		
		Files.copy(backupVersion, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		List<String> comandos = buildRestoreDatabaseCommands(databaseName, filePath)
		
		println comandos
		
		ProcessBuilder pb = new ProcessBuilder(comandos);
		pb.environment().put("PGPASSWORD", password);
		try {
			final Process process = pb.start();
			final BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line = r.readLine();
			while (line != null) {
				System.err.println(line);
				line = r.readLine();
			}
			r.close();
			process.waitFor();
			process.destroy();
			println "finish database restore..."
		} catch (IOException e) {
			println "error"
			e.printStackTrace();
		} catch (InterruptedException ie) {
			println "error"
			ie.printStackTrace();
		} finally {
			f.delete()
		}
		def dataFinal = new Date();
		def duration = TimeCategory.minus(dataFinal, dataInicial).toMilliseconds() / 1000
		println "Duração: ${duration}"
	}
	
	private def buildRestoreDatabaseCommands(databaseName, filePath) {
		List<String> comandos = new ArrayList<String>();
		if (Environment.current == Environment.DEVELOPMENT) {
			comandos.add("pg_restore");
			comandos.add("-i");
			comandos.add("-h");
			comandos.add("localhost");
			comandos.add("-p");
			comandos.add("5432");
			comandos.add("-U");
			comandos.add("postgres");
			comandos.add("-d");
			comandos.add(databaseName);
			comandos.add("-v");
			comandos.add(filePath);
		} else if (Environment.current == Environment.PRODUCTION) {
			comandos.add("heroku");
			comandos.add("pg:backups:restore");
			comandos.add("https://s3-sa-east-1.amazonaws.com/apvis-assets/" + filePath);
			
			def url = grailsApplication.config.dataSource_msrPreviousVersion.url
			def dbName = url.split('/').toList().last().tokenize('?')[0]
			if (databaseName == dbName) {
				comandos.add("HEROKU_POSTGRESQL_BRONZE_URL");
			}
			
			url = grailsApplication.config.dataSource_msrNextVersion.url
			dbName = url.split('/').toList().last().tokenize('?')[0]
			if (databaseName == dbName) {
				comandos.add("HEROKU_POSTGRESQL_JADE_URL");
			}
		}
		comandos
	}
}

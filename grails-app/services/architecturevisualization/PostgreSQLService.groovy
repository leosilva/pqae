package architecturevisualization

import grails.transaction.Transactional
import groovy.sql.Sql
import groovy.time.TimeCategory

import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Transactional
class PostgreSQLService {
	
	def dataSource_msrPreviousVersion
	def dataSource_msrNextVersion
	
	def destroyAndRestoreDatabase(systemName, previousVersion, nextVersion, backupPreviousVersion, backupNextVersion) {
		def dspv = dataSource_msrPreviousVersion.connection.catalog
		def dsnv = dataSource_msrNextVersion.connection.catalog
		
		recriateSchema(new Sql(dataSource_msrPreviousVersion))
		restoreDatabase(dspv, previousVersion, systemName, backupPreviousVersion)
		
		recriateSchema(new Sql(dataSource_msrNextVersion))
		restoreDatabase(dsnv, nextVersion, systemName, backupNextVersion)
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
	
	def restoreDatabase(databaseName, v, sy, backupVersion) {
		def dataInicial = new Date();
		println "starting database restore..."
		
		def filePath = "repositories/backups/" + sy + "/" + v + ".backup"
		
		def f = new File(filePath)
		
		if (!f.exists()) {
			f.mkdirs()
			f.createNewFile()
		}
		
		Files.copy(backupVersion, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		final List<String> comandos = new ArrayList<String>();
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
		ProcessBuilder pb = new ProcessBuilder(comandos);
		pb.environment().put("PGPASSWORD", "postgres");
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
}

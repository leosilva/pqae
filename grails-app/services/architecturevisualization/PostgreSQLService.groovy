package architecturevisualization

import grails.transaction.Transactional
import groovy.sql.Sql
import groovy.time.TimeCategory

@Transactional
class PostgreSQLService {
	
	def dataSource_msrPreviousVersion
	def dataSource_msrNextVersion

    def recriateSchema(ds) {
		def dataInicial = new Date();
		println "starting recriating schema..."
		def sql
		if (ds == "msrpreviousversion") {
			sql = new Sql(dataSource_msrPreviousVersion)
		} else if (ds == "msrnextversion") {
			sql = new Sql(dataSource_msrNextVersion)
		}
		sql.call("set transaction read write")
		sql.call("DROP SCHEMA IF EXISTS public CASCADE;")
		sql.call("CREATE SCHEMA public")
		sql.call("commit")
		
		println "finish recriating schema..."
		def dataFinal = new Date();
		def duration = TimeCategory.minus(dataFinal, dataInicial).toMilliseconds() / 1000
		println "Duração: ${duration}"
    }
	
	def restoreDatabase(databaseName, v, sy) {
		def dataInicial = new Date();
		println "starting database restore..."
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
		comandos.add("repositories/" + sy + "/backups/jetty-servlet-" + v + "_db_30.backup");
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
		}
		def dataFinal = new Date();
		def duration = TimeCategory.minus(dataFinal, dataInicial).toMilliseconds() / 1000
		println "Duração: ${duration}"
	}
}

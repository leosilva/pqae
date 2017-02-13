hibernate {
    //cache.use_second_level_cache = true
    //cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    //cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
//	format_sql = true
}

// environment specific settings
environments {
    development {
        dataSource_msrPreviousVersion {
			pooled = false
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "postgres"
			password = "postgres"
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://localhost:5432/msrpreviousversion"
        }
		dataSource_msrNextVersion {
			//logSql = true
			pooled = false
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "postgres"
			password = "postgres"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://localhost:5432/msrnextversion"
		}
		dataSource_av {
			//logSql = true
			pooled = true
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "postgres"
			password = "postgres"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://localhost:5432/architecturevisualization"
		}
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
		dataSource_msrPreviousVersion {
			pooled = false
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "kshwfyudamlonf"
			password = "96890476e9b4e1c2dcf4235cc7b567a9e36159641c30fb4cb22f98a3b0e9f051"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://ec2-54-163-234-20.compute-1.amazonaws.com:5432/d723ilub2pee9p"
		}
		dataSource_msrNextVersion {
			//logSql = true
			pooled = false
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "agvwwcbjxjrzxs"
			password = "a175fb82b032e3cf1f3b8d02246998a49e47a7b1644af589ab9dd273ea357f28"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://ec2-54-163-234-20.compute-1.amazonaws.com:5432/d80noilf2290r"
		}
		dataSource_av {
			//logSql = true
			pooled = true
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "zldmzctjcwlqux"
			password = "5ba14f1500266cfe287da96b2ec3f3779aefebd7b6f23398b7f282a3bbd2ba6e"
			dbCreate = "create" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://ec2-54-163-234-20.compute-1.amazonaws.com:5432/detishud0cjacq"
		}
    }
}

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
			username = "duttskilzsduhv"
			password = "e34de0799333b76720f21c91ca23379d78814d67075308e3a340490ea1af4990"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://ec2-107-22-223-6.compute-1.amazonaws.com:5432/ddov4a68cj0kru"
		}
		dataSource_msrNextVersion {
			//logSql = true
			pooled = false
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "yajbmqsoetzieo"
			password = "0b4a2b453bcce46a3eb9e2552acf25ec6dbdb14a18b899a9edbbd2a96eebd13a"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://ec2-54-227-237-223.compute-1.amazonaws.com:5432/d7rl78n1uvbtum"
		}
		dataSource_av {
			//logSql = true
			pooled = true
			jmxExport = true
			driverClassName = "org.postgresql.Driver"
			dialect = "org.hibernate.dialect.PostgreSQLDialect"
			username = "zldmzctjcwlqux"
			password = "5ba14f1500266cfe287da96b2ec3f3779aefebd7b6f23398b7f282a3bbd2ba6e"
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:postgresql://ec2-54-163-234-20.compute-1.amazonaws.com:5432/detishud0cjacq"
		}
    }
}

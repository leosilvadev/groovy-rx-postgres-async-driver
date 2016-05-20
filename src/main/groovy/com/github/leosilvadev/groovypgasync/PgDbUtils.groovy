package com.github.leosilvadev.groovypgasync

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
class PgDbUtils {
	
	static Map defaultConfig(){
		def env = System.getenv()
		[
			hostname: env.DB_HOSTNAME ?: 'localhost',
			port: env.DB_PORT ? new Integer(env.DB_PORT) : 5432 ,
			database: env.DB_DATABASE ?: 'nosql',
			username: env.DB_USERNAME ?: 'dev',
			password: env.DB_PASSWORD ?: 'dev',
			poolSize: env.DB_POOLSIZE ? new Integer(env.DB_POOLSIZE) : 20
		]
	}

}

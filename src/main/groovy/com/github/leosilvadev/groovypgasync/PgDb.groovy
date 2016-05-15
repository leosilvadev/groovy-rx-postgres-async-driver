package com.github.leosilvadev.groovypgasync

import static com.github.leosilvadev.groovypgasync.PgDbType.*
import rx.Observable

import com.github.pgasync.ConnectionPoolBuilder
import com.github.pgasync.Db
import com.github.pgasync.impl.PgResultSet

class PgDb {

	final Db db

	PgDb(Map config = PgDbUtils.defaultConfig()){
		db = new ConnectionPoolBuilder()
			.hostname(config.hostname)
			.port(config.port)
			.database(config.database)
			.username(config.username)
			.password(config.password)
			.poolSize(config.poolSize)
			.build()
	}

	Observable find(String sql, Map objectTemplate, Map mapParams = null) {
		execute(sql, mapParams).map(PgDbResultMapper.mapMany(objectTemplate))
	}
	
	Observable findOne(String sql, Map objectTemplate, Map mapParams = null) {
		execute(sql, mapParams).map(PgDbResultMapper.mapOne(objectTemplate))
	}
	
	Observable insert(String sql, Map mapParams, Boolean mustReturnId = true) {
		execute(sqlReturnId(sql, mustReturnId), mapParams)
			.map({ PgResultSet selectResult ->
				def row = selectResult.row(0)
				row.getLong(0)
			})
	}
	
	Observable update(String namedSql, Map mapParams = [:]) {
		execute(namedSql, mapParams).map({ it.updatedRows })
	}
		
	Observable delete(String namedSql, Map mapParams = [:]) {
		execute(namedSql, mapParams).map({ it.updatedRows })
	}
	
	Observable execute(String namedSql, Map mapParams = [:]) {
		def (sql, params) = PgDbParams.namedParameters(namedSql, mapParams)
		db.querySet(sql, *(params))
	}
	
	String sqlReturnId(String sql, Boolean mustReturnId){
		mustReturnId ? (sql + ' RETURNING ID ') : sql
	}
	
}

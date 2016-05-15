package com.github.leosilvadev.groovypgasync

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import rx.Observable

import com.github.pgasync.ConnectionPoolBuilder
import com.github.pgasync.Db
import com.github.pgasync.impl.PgResultSet

@CompileStatic
@TypeChecked
class PgDb {

	final Db db

	PgDb(Map config = PgDbUtils.defaultConfig()){
		db = new ConnectionPoolBuilder()
			.hostname(config.hostname as String)
			.port(config.port as Integer)
			.database(config.database as String)
			.username(config.username as String)
			.password(config.password as String)
			.poolSize(config.poolSize as Integer)
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
		execute(namedSql, mapParams).map({ PgResultSet rs -> rs.updatedRows() })
	}
	
	Observable delete(String namedSql, Map mapParams = [:]) {
		execute(namedSql, mapParams).map({ PgResultSet rs -> rs.updatedRows() })
	}
	
	Observable execute(String namedSql, Map mapParams = [:]) {
		Tuple2<String, List> tuple = PgDbParams.namedParameters(namedSql, mapParams)
		def sql = tuple.first
		def params = PgDbTypes.prepareAttributes(tuple.second ?: [])
		db.querySet(sql, params.toArray())
	}
	
	String sqlReturnId(String sql, Boolean mustReturnId){
		mustReturnId ? (sql + ' RETURNING ID ') : sql
	}
	
}

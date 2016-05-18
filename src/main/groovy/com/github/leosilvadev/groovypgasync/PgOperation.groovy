package com.github.leosilvadev.groovypgasync

import rx.Observable

import com.github.pgasync.impl.PgResultSet

class PgOperation {
	
	final def provider
	
	PgOperation(provider){
		this.provider = provider
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
		provider.querySet(sql, params.toArray()).onErrorReturn({
			provider.rollback()
		})
	}
	
	String sqlReturnId(String sql, Boolean mustReturnId){
		mustReturnId ? (sql + ' RETURNING ID ') : sql
	}

}

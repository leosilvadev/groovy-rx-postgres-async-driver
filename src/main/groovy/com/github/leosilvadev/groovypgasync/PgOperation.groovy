package com.github.leosilvadev.groovypgasync

import rx.Observable
import rx.Subscriber

import com.github.leosilvadev.groovypgasync.paging.Page
import com.github.leosilvadev.groovypgasync.paging.PageRequest
import com.github.pgasync.Row
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
	
	Observable find(String namedSql, Map objectTemplate, Map mapParams = null, PageRequest request){
		Tuple2<String, List> tuple = PgDbParams.namedParameters(namedSql, mapParams)
		def sql = tuple.first
		def params = PgDbTypes.prepareAttributes(tuple.second ?: [])
		def countSql = "SELECT COUNT(*) FROM ( $sql ) AS count"
		def offset = request.page * request.itemsPerPage
		def pagingSql = "SELECT * FROM ( $sql ) AS result OFFSET $offset LIMIT $request.itemsPerPage"
		
		provider.queryRows(countSql, params.toArray()).map({ Row row ->
			row.getLong("count")
			
		}).flatMap({ count ->
			Observable.create({ Subscriber subscriber ->
				provider.querySet(pagingSql, params.toArray())
					.map(PgDbResultMapper.mapMany(objectTemplate))
					.onErrorReturn(subscriber.&onError)
					.subscribe({ items ->
						def pages = count / request.itemsPerPage
						subscriber.onNext( new Page(items, count, request.itemsPerPage, request.page, pages as Long) )
					})
				
			})
		})
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

package com.github.leosilvadev.groovypgasync.tx

import rx.Observable
import rx.Subscriber;

import com.github.leosilvadev.groovypgasync.PgDbParams
import com.github.leosilvadev.groovypgasync.PgDbResultMapper
import com.github.leosilvadev.groovypgasync.PgDbTypes
import com.github.pgasync.Transaction
import com.github.pgasync.impl.PgResultSet

class PgTransaction {
	
	final Transaction transaction
	
	PgTransaction(Transaction transaction) {
		this.transaction = transaction
	}
	
	Observable commit(){
		Observable.create({ Subscriber sub ->
			transaction.commit()
			.onErrorReturn(rollbackOnError.curry(transaction))
			.subscribe({
				sub.onNext([:])
			})
		})
	}
	
	Observable rollback(){
		Observable.create(rollbackOnError.curry(transaction))
	}
	
	Closure rollbackOnError = { Transaction transaction, Subscriber sub ->
		transaction.rollback()
			.onErrorReturn({
				sub.onError(new RuntimeException('Fatal Error in trasaction!!!'))
			})
			.subscribe({
				sub.onError(new RuntimeException('Error in transaction, it was rolled back'))
			})
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
		transaction.querySet(sql, params.toArray()).onErrorReturn({
			transaction.rollback()
		})
	}
	
	String sqlReturnId(String sql, Boolean mustReturnId){
		mustReturnId ? (sql + ' RETURNING ID ') : sql
	}

}

package com.github.leosilvadev.groovypgasync.tx

import rx.Observable
import rx.Subscriber

import java.util.Map;

import com.github.leosilvadev.groovypgasync.PgOperation
import com.github.leosilvadev.groovypgasync.paging.PageRequest;
import com.github.pgasync.Transaction

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
		new PgOperation(transaction).find(sql, objectTemplate, mapParams)
	}
	
	Observable find(String sql, Map objectTemplate, Map mapParams = null, PageRequest request) {
		new PgOperation(transaction).find(sql, objectTemplate, mapParams, request)
	}
	
	Observable findOne(String sql, Map objectTemplate, Map mapParams = null) {
		new PgOperation(transaction).findOne(sql, objectTemplate, mapParams)
	}
	
	Observable insert(String sql, Map mapParams, Boolean mustReturnId = true) {
		new PgOperation(transaction).insert(sql, mapParams, mustReturnId)
	}
	
	Observable update(String namedSql, Map mapParams = [:]) {
		new PgOperation(transaction).update(namedSql, mapParams)
	}
	
	Observable delete(String namedSql, Map mapParams = [:]) {
		new PgOperation(transaction).delete(namedSql, mapParams)
	}

}

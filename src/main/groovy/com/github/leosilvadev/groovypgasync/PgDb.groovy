package com.github.leosilvadev.groovypgasync

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import rx.Observable

import com.github.leosilvadev.groovypgasync.paging.PageRequest
import com.github.leosilvadev.groovypgasync.tx.PgTransaction
import com.github.pgasync.ConnectionPoolBuilder
import com.github.pgasync.Db
import com.github.pgasync.Transaction

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
			.converters(new JsonbConverter())
			.build()
	}

	Observable find(String sql, Map objectTemplate, Map mapParams = null) {
		new PgOperation(db).find(sql, objectTemplate, mapParams)
	}

	Observable find(String sql, Class<?> classTemplate, Map mapParams = null) {
		new PgOperation(db).find(sql, templateOf(classTemplate), mapParams)
	}
	
	Observable find(String sql, Map objectTemplate, Map mapParams = null, PageRequest request) {
		new PgOperation(db).find(sql, objectTemplate, mapParams, request)
	}
	
	Observable find(String sql, Class<?> classTemplate, Map mapParams = null, PageRequest request) {
		new PgOperation(db).find(sql, templateOf(classTemplate), mapParams, request)
	}
	
	Observable findOne(String sql, Map objectTemplate, Map mapParams = null) {
		new PgOperation(db).findOne(sql, objectTemplate, mapParams)
	}
	
	Observable findOne(String sql, Class<?> classTemplate, Map mapParams = null) {
		new PgOperation(db).findOne(sql, templateOf(classTemplate), mapParams)
	}
	
	Observable insert(String sql, Map mapParams, Boolean mustReturnId = true) {
		new PgOperation(db).insert(sql, mapParams, mustReturnId)
	}
	
	Observable insert(String sql, Object object, Boolean mustReturnId = true) {
		new PgOperation(db).insert(sql, object.properties, mustReturnId)
	}
	
	Observable update(String namedSql, Map mapParams = [:]) {
		new PgOperation(db).update(namedSql, mapParams)
	}
	
	Observable update(String namedSql, Object object) {
		new PgOperation(db).update(namedSql, object.properties)
	}
	
	Observable delete(String namedSql, Map mapParams = [:]) {
		new PgOperation(db).delete(namedSql, mapParams)
	}
	
	Observable execute(String namedSql, Map mapParams = [:]){
		new PgOperation(db).execute(namedSql, mapParams)
	}
	
	Observable transaction(Closure function){
		db.begin().flatMap({ Transaction tx ->
			function(new PgTransaction(tx))
		})
	}
	
	private Map templateOf(Class<?> clazz) {
		clazz.metaClass.properties.findAll({ it.name != 'class' }).collectEntries { [it.name, it.type] }
	}
	
}

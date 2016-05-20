package com.github.leosilvadev.groovypgasync

import java.util.concurrent.TimeUnit

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import spock.util.concurrent.BlockingVariables

import com.github.leosilvadev.groovypgasync.tx.PgTransaction

@Stepwise
class PgDbTxIntegrationSpec extends Specification {

	@Shared PgDb db
	
	def setupSpec(){
		def conds = new AsyncConditions()
		db = new PgDb()
		db.execute(Fixture.CREATE_LOGS_TABLE)
			.flatMap({ db.delete(Fixture.DELETE_ALL_LOGS) })
			.subscribe({ conds.evaluate({})})
			
		conds.await(5.0)
	}
	
	def "Should insert three Logs in a Transaction"(){
		def vars = new BlockingVariables(5, TimeUnit.SECONDS)
		given:
			def sql = 'INSERT INTO Logs (type, details, description, registration, config) VALUES (:type, :details, :description, :registration, :config)'
			
		and:
			def config = [plan:"GOLD", registrationDate:new Date(), events:[[when:new Date(), type:"IHA"], [when:new Date(), type:"UHU"]]]
			
		and:
			def paramsOne = [type:'DEBUG', details:'any details 1', description:'any description', registration:new Date(), config:config]
			
		and:
			def paramsTwo = [type:'DEBUG', details:'any details 2', description:'any description', registration:new Date(), config:config]
			
		and:
			def paramsThree = [type:'DEBUG', details:'any details 3', description:'any description', registration:new Date(), config:config]
			
		when:
			def obs = db.transaction { PgTransaction tx ->
				tx.insert(sql, paramsOne).flatMap({ id ->
					tx.insert(sql, paramsTwo)
					
				}).flatMap({ id ->
					tx.insert(sql, paramsThree)
					
				}).flatMap({ id ->
					tx.commit()
					
				})
			}
			
		and:
			obs.subscribe({ vars.commited = true })
			
		then:
			vars.commited
	}
	
	def "Should rollback a Transaction when got some error"(){
		def vars = new BlockingVariables(5, TimeUnit.SECONDS)
		given:
			def sql = 'INSERT INTO Logs (type, details, description, registration, config) VALUES (:type, :details, :description, :registration, :config)'
			
		and:
			def config = [plan:"GOLD", registrationDate:new Date(), events:[[when:new Date(), type:"IHA"], [when:new Date(), type:"UHU"]]]
			
		and:
			def wrongSql = 'INSERT INTO WRONGLOGTABLE'
			
		and:
			def paramsOne = [type:'DEBUG', details:'any details 1', description:'any description', registration:new Date(), config:config]
			
		and:
			def paramsTwo = [type:'DEBUG', details:'any details 2', description:'any description', registration:new Date(), config:config]
			
		and:
			def paramsThree = [type:'DEBUG', details:'any details 3', description:'any description', registration:new Date(), config:config]
			
		when:
			def obs = db.transaction { PgTransaction tx ->
				tx.insert(sql, paramsOne).flatMap({ id ->
					tx.insert(sql, paramsTwo)
					
				}).flatMap({ id ->
					tx.insert(wrongSql, paramsThree)
					
				})
			}
			
		and:
			obs.onErrorReturn({ vars.rolledBack = true }).subscribe()
			
		then:
			vars.rolledBack
	}
	
	def "Should rollback a Transaction when it is triggered"(){
		def vars = new BlockingVariables(5, TimeUnit.SECONDS)
		given:
			def sql = 'INSERT INTO Logs (type, details, description, registration, config) VALUES (:type, :details, :description, :registration, :config)'
			
		and:
			def config = [plan:"GOLD", registrationDate:new Date(), events:[[when:new Date(), type:"IHA"], [when:new Date(), type:"UHU"]]]
			
		and:
			def paramsOne = [type:'DEBUG', details:'any details 1', description:'any description', registration:new Date(), config:config]
			
		and:
			def paramsTwo = [type:'DEBUG', details:'any details 2', description:'any description', registration:new Date(), config:config]
			
		and:
			def paramsThree = [type:'DEBUG', details:'any details 3', description:'any description', registration:new Date(), config:config]
			
		when:
			def obs = db.transaction { PgTransaction tx ->
				tx.insert(sql, paramsOne).flatMap({ id ->
					tx.insert(sql, paramsTwo)
					
				}).flatMap({ id ->
					tx.insert(sql, paramsThree)
					
				}).flatMap({ id ->
					tx.rollback()
				})
			}
			
		and:
			obs.onErrorReturn({ vars.rolledBack = true }).subscribe()
			
		then:
			vars.rolledBack
	}
	
	def "Should not find one Log when the query returns many"(){
		def vars = new BlockingVariables(10, TimeUnit.SECONDS)
		given:
			def sql = 'SELECT * FROM Logs'
			
		when:
			def obs = db.find(sql, [id:Long])
			
		and:
			obs.subscribe({ vars.logs = it })
			
		then:
			vars.logs.size() == 3
	}
	
	def cleanupSpec(){
		db.delete(Fixture.DELETE_ALL_LOGS).subscribe()
	}
	
}

package com.github.leosilvadev.groovypgasync

import java.time.LocalDate
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import spock.util.concurrent.BlockingVariables

import com.github.leosilvadev.groovypgasync.exceptions.ResultMapException

@Stepwise
class PgDbIntegrationSpec extends Specification {

	@Shared PgDb db
	
	def setupSpec(){
		def conds = new AsyncConditions()
		db = new PgDb()
		db.execute(Fixture.CREATE_LOGS_TABLE)
			.flatMap({ db.delete(Fixture.DELETE_ALL_LOGS) })
			.subscribe({ conds.evaluate({})})
			
		conds.await(5.0)
	}
	
	def "Should insert a new Log"(){
		def vars = new BlockingVariables(10, TimeUnit.SECONDS)
		given:
			def sql = 'INSERT INTO Logs (type, details, description, registration) VALUES (:type, :details, :description, :registration)'

		and:
			def params = [type:'DEBUG', details:'any details', description:'any description', registration:new Date()]
			
		when:
			def obs = db.insert(sql, params)
			
		and:
			obs.subscribe({ vars.id = it })
			
		then:
			vars.id
	}
	
	def "Should insert a new Log again"(){
		def vars = new BlockingVariables(10, TimeUnit.SECONDS)
		given:
			def sql = 'INSERT INTO Logs (type, details, description, registration) VALUES (:type, :details, :description, :registration)'
		
		and:
			def params = [type:'DEBUG', details:'any details 2', description:'any description 2', registration:LocalDateTime.now()]
			
		when:
			def obs = db.insert(sql, params)
			
		and:
			obs.subscribe({ vars.id = it })
			
		then:
			vars.id
	}
	
	def "Should update a Log"(){
		def vars = new BlockingVariables(10, TimeUnit.SECONDS)
		given:
			def sql = 'UPDATE Logs SET details = :details WHERE description LIKE :description'
		
		when:
			def obs = db.update(sql, [details:'updated details', description:'any description'])
			
		and:
			obs.subscribe({ vars.updated = it })
			
		then:
			vars.updated == 1
	}
	
	def "Should find all Logs"(){
		def vars = new BlockingVariables(100, TimeUnit.SECONDS)
		given:
			def sql = 'SELECT * FROM Logs ORDER BY id'
			
		when:
			def obs = db.find(sql, [id:Long, type:String, description:String, details:String])
			
		and:
			obs.subscribe({ vars.logs = it })
			
		then:
			vars.logs.size() == 2
			
		and:
			def log = vars.logs.first()
			log.id > 0
			log.type == 'DEBUG'
			log.description == 'any description'
			log.details == 'updated details'
	}
	
	def "Should find one Log"(){
		def vars = new BlockingVariables(10, TimeUnit.SECONDS)
		given:
			def sql = 'SELECT * FROM Logs WHERE description = :description'
			
		when:
			def obs = db.findOne(sql, [id:Long, type:String, description:String, details:String], [description:'any description 2'])
			
		and:
			obs.subscribe({ vars.log = it })
			
		then:
			def log = vars.log
			log.id > 0
			log.type == 'DEBUG'
			log.description == 'any description 2'
			log.details == 'any details 2'
	}
	
	def "Should not find one Log when the query returns many"(){
		def vars = new BlockingVariables(10, TimeUnit.SECONDS)
		given:
			def sql = 'SELECT * FROM Logs WHERE type = :type'
			
		when:
			def obs = db.findOne(sql, [id:Long, type:String, description:String, details:String], [type:'DEBUG'])
			
		and:
			obs.onErrorReturn({ vars.error = it }).subscribe()
			
		then:
			vars.error instanceof ResultMapException
	}
	
	def cleanupSpec(){
		db.delete(Fixture.DELETE_ALL_LOGS).subscribe()
	}
	
}

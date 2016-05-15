package com.github.leosilvadev.groovypgasync

import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

import spock.lang.Specification

class PgDbTypesUnitSpec extends Specification {
	
	def "Should change java.util.Date to Timestamp"(){
		given:
			def date = new Date()
			
		when:
			def result = PgDbTypes.prepareAttribute(date)
			
		then:
			result instanceof Timestamp
			
		and:
			result.getTime() == date.getTime()
	}
	
	def "Should change Calendar to Timestamp"(){
		given:
			def calendar = Calendar.getInstance()
			
		when:
			def result = PgDbTypes.prepareAttribute(calendar)
			
		then:
			result instanceof Timestamp
			
		and:
			result.getTime() == calendar.getTime().getTime()
	}
	
	def "Should change LocalDate to java.sql.Date"(){
		given:
			def date = LocalDate.now()
			
		when:
			def result = PgDbTypes.prepareAttribute(date)
			
		then:
			result instanceof java.sql.Date
			
		and:
			result.getTime() == Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
	}
	
	def "Should change LocalDateTime to Timestamp"(){
		given:
			def datetime = LocalDateTime.now()
			
		when:
			def result = PgDbTypes.prepareAttribute(datetime)
			
		then:
			result instanceof java.sql.Timestamp
			
		and:
			result.getTime() == Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant()).getTime()
	}

}

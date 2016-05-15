package com.github.leosilvadev.groovypgasync

import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId;

import com.github.pgasync.Row

class PgDbResultMapperUnitSpec extends Specification {
	
	def "Should map a BigDecimal value"(){
		given:
			def type = BigDecimal
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getBigDecimal(column) >> 10.10
			
		and:
			result == 10.10
	}
	
	def "Should map a BigInteger value"(){
		given:
			def type = BigInteger
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getBigInteger(column) >> 10
			
		and:
			result == 10
	}
	
	def "Should map a Boolean value"(){
		given:
			def type = Boolean
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getBoolean(column) >> true
			
		and:
			result
	}
	
	def "Should map a Byte value"(){
		given:
			def type = Byte
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getByte(column) >> Byte.MIN_VALUE
			
		and:
			result == Byte.MIN_VALUE
	}
	
	def "Should map a Calendar value"(){
		def fake = Calendar.getInstance()
		given:
			def type = Calendar
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getDate(column) >> new java.sql.Date(fake.getTimeInMillis())
			
		and:
			result == fake
	}
	
	def "Should map a Character value"(){
		given:
			def type = Character
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getChar(column) >> 'A'
			
		and:
			result == 'A'
	}
	
	def "Should map a Date value"(){
		def fake = new Date()
		given:
			def type = Date
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getDate(column) >> new java.sql.Date(fake.getTime())
			
		and:
			result == fake
	}
	
	def "Should map a Double value"(){
		given:
			def type = Double
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getDouble(column) >> 1.0d
			
		and:
			result == 1.0d
	}
	
	def "Should map a Integer value"(){
		given:
			def type = Integer
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getInt(column) >> 1
			
		and:
			result == 1
	}
	
	def "Should map a LocalDate value"(){
		def fake = new java.sql.Date(new Date().getTime())
		def localDate = fake.toLocalDate()
		given:
			def type = LocalDate
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getDate(column) >> fake
			
		and:
			result == localDate
	}
	
	def "Should map a Long value"(){
		given:
			def type = Long
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getLong(column) >> 1l
			
		and:
			result == 1l
	}
	
	def "Should map a Short value"(){
		given:
			def type = Short
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getShort(column) >> 1
			
		and:
			result == 1
	}
	
	def "Should map a String value"(){
		given:
			def type = String
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getString(column) >> 'value'
			
		and:
			result == 'value'
	}
	
	def "Should map a Time value"(){
		def fake = new java.sql.Time(new Date().getTime())
		given:
			def type = java.sql.Time
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getTime(column) >> fake
			
		and:
			result == fake
	}
	
	def "Should map a Timestamp value"(){
		def fake = new java.sql.Timestamp(new Date().getTime())
		given:
			def type = java.sql.Timestamp
			
		and:
			def column = "column"
			
		and:
			Row row = Mock(Row)
			
		when:
			def result = PgDbResultMapper.mapType(column, type, row)
			
		then:
			1 * row.getTimestamp(column) >> fake
			
		and:
			result == fake
	}

}

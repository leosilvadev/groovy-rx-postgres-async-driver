package com.github.leosilvadev.groovypgasync

import groovy.json.JsonOutput

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class PgDbTypes {

	static def prepareAttribute(attr, level = 0){
		switch(attr) {
			case { it?.class?.enum }:
				return attr.toString()

			case Date:
				return new java.sql.Timestamp(attr.getTime())
				
			case Calendar:
				return new java.sql.Timestamp(attr.getTime().getTime())
				
			case LocalDate:
				Date date = Date.from(attr.atStartOfDay(ZoneId.systemDefault()).toInstant())
				return new java.sql.Date(date.getTime())
				
			case LocalDateTime:
				Date date = Date.from(attr.atZone(ZoneId.systemDefault()).toInstant())
				return new java.sql.Timestamp(date.getTime())
				
			case List:
				return attr.collect({ prepareAttribute(it, level + 1) })
			
			case Map:
				def map = [:]
				attr.each { key, value ->
					map."$key" = prepareAttribute(value, level + 1)
				}
				return (level == 0) ? JsonOutput.toJson(map) : map
				
			default:
				return attr
		}
	}
	
	static List prepareAttributes(List attrs){
		attrs.collect this.&prepareAttribute
	}
	
}

package com.github.leosilvadev.groovypgasync

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class PgDbTypes {

	static def prepareAttribute(attr){
		switch(attr) {
			case Date:
				return new java.sql.Date(attr.getTime())
				
			case Calendar:
				return new java.sql.Date(attr.getTime())
				
			case LocalDate:
				Date date = Date.from(attr.atStartOfDay(ZoneId.systemDefault()).toInstant())
				return new java.sql.Date(date.getTime())
				
			case LocalDateTime:
				Date date = Date.from(attr.atZone(ZoneId.systemDefault()).toInstant())
				return new java.sql.Timestamp(date.getTime())
				
			default:
				return attr
		}
	}
	
	static List prepareAttributes(List attrs){
		attrs.collect this.&prepareAttribute
	}
	
}

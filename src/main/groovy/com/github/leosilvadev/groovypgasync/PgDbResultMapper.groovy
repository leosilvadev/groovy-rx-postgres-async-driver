package com.github.leosilvadev.groovypgasync

import com.github.leosilvadev.groovypgasync.exceptions.ResultMapException;
import com.github.pgasync.ResultSet

class PgDbResultMapper {
	
	static def mapMany(Map<String, PgDbType> objectTemplate){
		{ ResultSet result ->
			def items = []
			result.iterator().each { row ->
				def map = [:]
				result.columns.each { column ->
					column = column.toLowerCase()
					if ( objectTemplate.keySet().contains(column) ) {
						def type = objectTemplate.get(column).value()
						map."$column" = row."get$type"(column)
						
					}
				}
				items << map
			}
			items
		}
	}
	
	static def mapOne(Map<String, PgDbType> objectTemplate){
		{ ResultSet result ->
			if ( result.size() > 1 ) throw new ResultMapException("Expected only one result but got many. [${result.size()}]")
			
			def row = result.iterator().next()
			def map = [:]
			result.columns.each { column ->
				column = column.toLowerCase()
				if ( objectTemplate.keySet().contains(column) ) {
					def type = objectTemplate.get(column).value()
					map."$column" = row."get$type"(column)
					
				}
			}
			map
		}
	}

}

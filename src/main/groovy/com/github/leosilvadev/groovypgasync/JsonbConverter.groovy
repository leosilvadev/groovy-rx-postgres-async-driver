package com.github.leosilvadev.groovypgasync

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

import com.github.pgasync.Converter
import com.github.pgasync.impl.Oid

@CompileStatic
class JsonbConverter implements Converter<Map> {

	Class<Map> type() {
		Map
	}
	
	byte[] from(Map jsonb) {
		JsonOutput.toJson(jsonb).getBytes('UTF-8')
	}
	
	Map to(Oid oid, byte[] value) {
		if ( value ) {
			def result = new JsonSlurper().parse(value)
			if ( result instanceof Map ) {
				result as Map
				
			} else {
				throw new IllegalArgumentException('Invalid Jsonb object')
			
			}
		}
	}
	
}

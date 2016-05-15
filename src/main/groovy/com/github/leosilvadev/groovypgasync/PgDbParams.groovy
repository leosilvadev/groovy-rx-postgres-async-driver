package com.github.leosilvadev.groovypgasync

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
class PgDbParams {
	
	static Tuple2<String, List> namedParameters(String sql, Map parameters){
		if ( parameters ) {
			def neededParams = sql.findAll(/:\w+/)
			def orderedParameters = []
			
			neededParams.eachWithIndex { param, index ->
				sql = sql.replace(param, "\$${index+1}")
				def paramName = param.substring(1)
				def paramValue = parameters[paramName]
				
				orderedParameters << paramValue
			}
			
			new Tuple2(sql, orderedParameters)
			
		} else {
			new Tuple2(sql, null)
			
		}
	}

}

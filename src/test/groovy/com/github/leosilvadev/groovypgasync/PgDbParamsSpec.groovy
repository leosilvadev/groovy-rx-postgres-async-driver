package com.github.leosilvadev.groovypgasync

import spock.lang.Specification

class PgDbParamsSpec extends Specification {
	
	def "Should handle named parameters"(){
		given:
			def sql = 'INSERT INTO Logs (description, details, type) VALUES (:desc, :details, :type)'
			
		and:
			def params = [desc:'Any Desc', details:'Any Detail', type:'DEBUG']
			
		when:
			def (newSql, newParams) = PgDbParams.namedParameters(sql, params)
			
		then:
			newSql == 'INSERT INTO Logs (description, details, type) VALUES ($1, $2, $3)'
			
		and:
			newParams.size() == 3
			newParams[0] == 'Any Desc'
			newParams[1] == 'Any Detail'
			newParams[2] == 'DEBUG'
	}

}

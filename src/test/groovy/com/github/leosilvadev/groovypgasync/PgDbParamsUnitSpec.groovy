package com.github.leosilvadev.groovypgasync

import com.github.leosilvadev.groovypgasync.objects.Log
import spock.lang.Specification

class PgDbParamsUnitSpec extends Specification {

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

	def "Should handle named parameters when multiple times the same parameter"(){
		given:
		def sql = 'SELECT * FROM Logs WHERE description = :desc AND type IN (:type) AND status = :status'

		and:
		def params = [desc:'Any Desc', type:['DEBUG', 'INFO', 'ERROR'], status: Log.Status.ACTIVE]

		when:
		def (newSql, newParams) = PgDbParams.namedParameters(sql, params)

		then:
		newSql == 'SELECT * FROM Logs WHERE description = $1 AND type IN ($2, $3, $4) AND status = $5'

		and:
		newParams.size() == 5
		newParams[0] == 'Any Desc'
		newParams[1] == 'DEBUG'
		newParams[2] == 'INFO'
    newParams[3] == 'ERROR'
		newParams[4] == Log.Status.ACTIVE
	}

}

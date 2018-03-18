package com.github.leosilvadev.groovypgasync.builder

import com.github.leosilvadev.groovypgasync.objects.Log
import com.github.leosilvadev.groovypgasync.objects.LogWithAnnotation
import com.github.leosilvadev.groovypgasync.sql.QueryBuilder
import spock.lang.Specification

/**
 * Created by leonardo on 3/18/18.
 */
class SelectBuilderUnitSpec extends Specification {

  def 'Build select query with all fields and no filter'() {
    when:
    def query = QueryBuilder.select(Log).build()

    then:
    query.sql == 'SELECT * FROM Log'
    query.params == []
  }

  def 'Build select query with all fields, @Table and no filter'() {
    when:
    def query = QueryBuilder.select(LogWithAnnotation).build()

    then:
    query.sql == 'SELECT * FROM Logs'
    query.params == []
  }

  def 'Build select query with all fields and two equals filters'() {
    when:
    def query = QueryBuilder.select(Log)
      .where('type').eq('WHATEVER')
      .where('status').eq(Log.Status.ACTIVE)
      .build()

    then:
    query.sql == 'SELECT * FROM Log WHERE type = :type AND status = :status'
    query.params == ['WHATEVER', Log.Status.ACTIVE]
  }

  def 'Build select query with all fields and one bigger than and one bigger than or equal filters'() {
    when:
    def query = QueryBuilder.select(Log)
      .where('age').bt(20)
      .where('points').bte(50)
      .build()

    then:
    query.sql == 'SELECT * FROM Log WHERE age > :age AND points >= :points'
    query.params == [20, 50]
  }

  def 'Build select query with all fields and one less than and one less than or equal filters'() {
    when:
    def query = QueryBuilder.select(Log)
      .where('age').lt(20)
      .where('points').lte(50)
      .build()

    then:
    query.sql == 'SELECT * FROM Log WHERE age < :age AND points <= :points'
    query.params == [20, 50]
  }

}

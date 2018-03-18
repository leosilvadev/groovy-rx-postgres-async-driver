package com.github.leosilvadev.groovypgasync

import com.github.leosilvadev.groovypgasync.exceptions.ResultMapException
import com.github.leosilvadev.groovypgasync.objects.Log
import com.github.leosilvadev.groovypgasync.paging.Page
import com.github.leosilvadev.groovypgasync.paging.PageRequest
import io.reactivex.functions.Consumer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import spock.util.concurrent.BlockingVariables

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Stepwise
class PgDbClassesIntegrationSpec extends Specification {

  @Shared
  PgDb db

  def setupSpec() {
    def conds = new AsyncConditions()
    db = new PgDb()
    db.execute(Fixture.CREATE_LOGS_TABLE)
      .flatMap({ db.delete(Fixture.DELETE_ALL_LOGS) })
      .subscribe({ Long id -> conds.evaluate({}) } as Consumer)

    conds.await(5.0)
  }

  def "Should insert a new Log"() {
    def vars = new BlockingVariables(10, TimeUnit.SECONDS)
    given:
    def sql = '''
				INSERT INTO Logs (type, details, description, registration, config, status) 
					VALUES 
				(:type, :details, :description, :registration, :config, :status)
			'''

    and:
    def config = [plan: "GOLD", registrationDate: new Date(), events: [[when: new Date(), type: "IHA"], [when: new Date(), type: "UHU"]]]

    and:
    def log = new Log(
      type: 'DEBUG',
      details: 'any details',
      description: 'any description',
      registration: LocalDateTime.now(),
      config: config,
      status: Log.Status.ACTIVE
    )

    when:
    def obs = db.insert(sql, log)

    and:
    obs.subscribe({ vars.id = it } as Consumer)

    then:
    vars.id
  }

  def "Should insert a new Log again"() {
    def vars = new BlockingVariables(10, TimeUnit.SECONDS)
    given:
    def sql = '''
				INSERT INTO Logs (type, details, description, registration, config, status) 
					VALUES 
				(:type, :details, :description, :registration, :config, :status)
			'''

    and:
    def config = [plan: "GOLD", registrationDate: new Date(), events: [[when: new Date(), type: "IHA"], [when: new Date(), type: "UHU"]]]

    and:
    def log = new Log(
      type: 'DEBUG',
      details: 'any details 2',
      description: 'any description 2',
      registration: LocalDateTime.now(),
      config: config,
      status: Log.Status.ACTIVE
    )

    when:
    def obs = db.insert(sql, log)

    and:
    obs.subscribe({ vars.id = it } as Consumer)

    then:
    vars.id
  }

  def "Should update a Log"() {
    def vars = new BlockingVariables(10, TimeUnit.SECONDS)
    given:
    def sql = 'UPDATE Logs SET details = :details WHERE description LIKE :description'

    when:
    def obs = db.update(sql, new Log(details: 'updated details', description: 'any description'))

    and:
    obs.subscribe({ vars.updated = it } as Consumer)

    then:
    vars.updated == 1
  }

  def "Should find all Logs"() {
    def vars = new BlockingVariables(100, TimeUnit.SECONDS)
    given:
    def sql = 'SELECT * FROM Logs ORDER BY id'

    when:
    def obs = db.find(sql, Log).toList()

    and:
    obs.subscribe({ vars.logs = it } as Consumer)

    then:
    vars.logs.size() == 2

    and:
    def log = vars.logs.first()
    log instanceof Log
    log.id > 0
    log.type == 'DEBUG'
    log.description == 'any description'
    log.details == 'updated details'
    log.status == Log.Status.ACTIVE

    and:
    log.config instanceof Map
    log.config.events.size() == 2
    log.config.events.first().type == 'IHA'
  }

  def "Should find all Logs from paging one"() {
    def vars = new BlockingVariables(10, TimeUnit.SECONDS)
    given:
    def sql = 'SELECT * FROM Logs ORDER BY id DESC'

    when:
    def obs = db.find(sql, Log, new PageRequest(1, 1))

    and:
    obs
      .onErrorReturn({ it.printStackTrace() })
      .subscribe({ vars.page = it } as Consumer)

    then:
    Page page = vars.page
    page.items.size() == 1
    page.pages == 2
    page.currentPage == 1
    page.itemsPerPage == 1
    page.totalItems == 2

    and:
    def log = vars.page.items.first()
    log.id > 0
    log.type == 'DEBUG'
    log.description == 'any description'
    log.details == 'updated details'

    and:
    log.config instanceof Map
    log.config.events.size() == 2
    log.config.events.first().type == 'IHA'
  }

  def "Should find one Log"() {
    def vars = new BlockingVariables(10, TimeUnit.SECONDS)
    given:
    def sql = 'SELECT * FROM Logs WHERE description = :description AND status IN (:status)'

    when:
    def obs = db.findOne(sql, Log, [description: 'any description 2', status: [Log.Status.ACTIVE, Log.Status.INACTIVE]])

    and:
    obs.subscribe({ vars.log = it } as Consumer)

    then:
    def log = vars.log
    log.id > 0
    log.type == 'DEBUG'
    log.description == 'any description 2'
    log.details == 'any details 2'

    and:
    log.config instanceof Map
    log.config.events.size() == 2
    log.config.events.first().type == 'IHA'
  }

  def "Should find one Log using BETWEEN"() {
    def vars = new BlockingVariables(10, TimeUnit.SECONDS)
    given:
    def sql = 'SELECT * FROM Logs WHERE registration BETWEEN :registration'

    when:
    def obs = db.find(sql, Log, [registration: new Tuple2(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1))])

    and:
    obs.toList().subscribe({ vars.logs = it } as Consumer)

    then:
    vars.logs.size() == 2
  }

  def "Should not find one Log when the query returns many"() {
    def vars = new BlockingVariables(10, TimeUnit.SECONDS)
    given:
    def sql = 'SELECT * FROM Logs WHERE type = :type'

    when:
    def obs = db.findOne(sql, Log, [type: 'DEBUG'])

    and:
    obs.onErrorReturn({ vars.error = it }).subscribe()

    then:
    vars.error instanceof ResultMapException
  }

  def cleanupSpec() {
    db.delete(Fixture.DELETE_ALL_LOGS).subscribe()
  }

}

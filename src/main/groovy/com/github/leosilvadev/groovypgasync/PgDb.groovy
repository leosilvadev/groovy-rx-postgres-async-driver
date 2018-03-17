package com.github.leosilvadev.groovypgasync

import com.github.leosilvadev.groovypgasync.paging.Page
import com.github.leosilvadev.groovypgasync.paging.PageRequest
import com.github.leosilvadev.groovypgasync.tx.PgTransaction
import com.github.pgasync.ConnectionPoolBuilder
import com.github.pgasync.Db
import com.github.pgasync.ResultSet
import com.github.pgasync.Transaction
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import io.reactivex.Observable
import io.reactivex.Single

@CompileStatic
@TypeChecked
class PgDb {

  final Db db

  PgDb(final Map config = PgDbUtils.defaultConfig()) {
    db = new ConnectionPoolBuilder()
      .hostname(config.hostname as String)
      .port(config.port as Integer)
      .database(config.database as String)
      .username(config.username as String)
      .password(config.password as String)
      .poolSize(config.poolSize as Integer)
      .converters(new JsonbConverter())
      .build()
  }

  public <T> Observable<T> find(final String sql,
                                final Class<T> classTemplate,
                                final Map mapParams = [:]) {
    new PgOperation(db).find(sql, classTemplate, mapParams)
  }

  public <T> Single<Page<T>> find(final String sql,
                                  final Class<T> classTemplate,
                                  final Map mapParams = [:],
                                  final PageRequest pageRequest) {
    new PgOperation(db).find(sql, classTemplate, mapParams, pageRequest)
  }

  public <T> Single<T> findOne(final String sql,
                               final Class<T> classTemplate,
                               final Map mapParams = [:]) {
    new PgOperation(db).findOne(sql, classTemplate, mapParams)
  }

  Single<Long> count(final String sql,
                     final Map mapParams = [:]) {
    new PgOperation(db).count(sql, mapParams)
  }

  public <T> Single<Long> insert(final String sql,
                                 final T object) {
    new PgOperation(db).insert(sql, object)
  }

  public <T> Single<Long> update(final String namedSql,
                                 final T object,
                                 final Map mapParams = [:]) {
    new PgOperation(db).update(namedSql, mapParams + object.properties)
  }

  Single<Long> delete(final String namedSql,
                      final Map mapParams = [:]) {
    new PgOperation(db).delete(namedSql, mapParams)
  }

  protected Single<ResultSet> execute(final String namedSql,
                                      final Map mapParams = [:]) {
    new PgOperation(db).execute(namedSql, mapParams)
  }

  Single<PgTransaction> transaction() {
    Single.create { emitter ->
      db.begin({ final Transaction transaction ->
        emitter.onSuccess(new PgTransaction(transaction))

      }, emitter.&onError)
    }
  }

  private Map templateOf(final Class<?> clazz) {
    clazz.declaredFields.collectEntries { [it.name, it.type] }
  }

}

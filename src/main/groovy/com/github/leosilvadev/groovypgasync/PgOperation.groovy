package com.github.leosilvadev.groovypgasync

import com.github.leosilvadev.groovypgasync.paging.Page
import com.github.leosilvadev.groovypgasync.paging.PageRequest
import com.github.pgasync.QueryExecutor
import com.github.pgasync.ResultSet
import com.github.pgasync.impl.PgResultSet
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function

class PgOperation {

  final QueryExecutor provider

  PgOperation(final QueryExecutor provider) {
    this.provider = provider
  }

  public <T> Observable<T> find(final String sql,
                                final Class<T> clazz,
                                final Map mapParams = [:]) {
    return execute(sql, mapParams).flatMapObservable { rs ->
      def many = PgDbResultMapper.mapMany(templateOf(clazz)).apply(rs)
      Observable.fromIterable(many)

    }.map { it.asType(clazz) }
  }

  public <T> Single<Page<T>> find(final String namedSql,
                                  final Class<T> clazz,
                                  final Map mapParams = [:],
                                  final PageRequest request) {
    def tuple = PgDbParams.namedParameters(namedSql, mapParams)
    def sql = tuple.first
    def params = PgDbTypes.prepareAttributes(tuple.second ?: [])
    def countSql = "SELECT COUNT(*) FROM ( $sql ) AS count"
    def offset = request.page * request.itemsPerPage
    def pagingSql = "SELECT * FROM ( $sql ) AS result OFFSET $offset LIMIT $request.itemsPerPage"

    Single.create({ emitter ->
      provider.query(countSql, params, { final ResultSet rs ->
        def results = rs.toList()
        if (results.size() == 0) {
          emitter.onError(new RuntimeException("No item found"))
          return
        }

        emitter.onSuccess(results.first().getLong('count'))

      }, emitter.&onError)

    }).flatMap({ final Long count ->
      Single.create({ emitter ->
        provider.query(pagingSql, params, { final ResultSet rs ->
          def many = PgDbResultMapper.mapMany(templateOf(clazz)).apply(rs)
          emitter.onSuccess(many.collect { it.asType(clazz) })

        }, emitter.&onError)

      }).map({ final List<T> items ->
        def pages = count / request.itemsPerPage
        new Page(items, count, request.itemsPerPage, request.page, pages as Long)
      })
    })
  }

  public <T> Single<T> findOne(final String sql,
                               final Class<T> clazz,
                               final Map mapParams = [:]) {
    execute(sql, mapParams)
      .map(PgDbResultMapper.mapOne(templateOf(clazz)))
      .map { it as T }
  }

  Single<Long> count(final String sql, final Map mapParams) {
    execute(sql, mapParams)
      .flatMap { rs ->
        if (rs.size() != 1) {
          return Single.error(new RuntimeException("Invalid result for a count operation"))
        }
        Single.just(rs.toList().first().getLong(0))
      }
  }

  public <T> Single<Long> insert(final String sql, final T object, final Boolean withId = true) {
    execute(sqlReturnId(sql, withId), object.properties)
      .map({ final PgResultSet selectResult ->
        if (!withId || selectResult.size() == 0 ){
          return 0
        }

        def row = selectResult.row(0)
        row.getLong(0)

      } as Function)
  }

  Single<Long> update(final String namedSql, final Map mapParams = [:]) {
    execute(namedSql, mapParams).map({ final PgResultSet rs -> rs.updatedRows() } as Function)
  }

  Single<Long> delete(final String namedSql, final Map mapParams = [:]) {
    execute(namedSql, mapParams).map({ final PgResultSet rs -> rs.updatedRows() } as Function)
  }

  Single<ResultSet> execute(final String namedSql, final Map mapParams = [:]) {
    final tuple = PgDbParams.namedParameters(namedSql, mapParams)
    final sql = tuple.first
    final params = PgDbTypes.prepareAttributes(tuple.second ?: [])
    Single.create({ emitter ->
      provider.query(sql, params, emitter.&onSuccess, emitter.&onError)

    }).doOnError({
      if (provider.class.getMethods().find { it.name == 'rollback' }) provider.rollback()
    })
  }

  private static String sqlReturnId(final String sql, final Boolean withId) {
    withId ? (sql + ' RETURNING ID ') : sql
  }

  private static Map templateOf(final Class<?> clazz) {
    clazz.declaredFields.collectEntries { [it.name, it.type] }
  }

}

package com.github.leosilvadev.groovypgasync.tx

import com.github.leosilvadev.groovypgasync.PgOperation
import com.github.leosilvadev.groovypgasync.paging.Page
import com.github.leosilvadev.groovypgasync.paging.PageRequest
import com.github.pgasync.Transaction
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.SingleEmitter

class PgTransaction {

  final Transaction transaction

  PgTransaction(Transaction transaction) {
    this.transaction = transaction
  }

  Single commit() {
    Single.create({ emitter ->
      transaction.commit()
        .onErrorReturn(rollbackOnError.curry(transaction))
        .subscribe({
        emitter.onSuccess([:])
      })
    })
  }

  Single rollback() {
    Single.create(rollbackOnError.curry(transaction))
  }

  Closure rollbackOnError = { final Transaction transaction, final SingleEmitter emitter ->
    transaction.rollback()
      .onErrorReturn({
      emitter.onError(new RuntimeException('Fatal Error in trasaction!!!'))
    })
      .subscribe({
      emitter.onError(new RuntimeException('Error in transaction, it was rolled back'))
    })
  }

  public <T> Observable<T> find(final String sql,
                                final Class<T> clazz,
                                final Map mapParams = [:]) {
    new PgOperation(transaction).find(sql, clazz, mapParams)
  }

  public <T> Single<Page<T>> find(final String sql,
                                  final Class<T> clazz,
                                  final Map mapParams = [:],
                                  final PageRequest request) {
    new PgOperation(transaction).find(sql, clazz, mapParams, request)
  }

  public <T> Single<T> findOne(final String sql,
                               final Class<T> clazz,
                               final Map mapParams = [:]) {
    new PgOperation(transaction).findOne(sql, clazz, mapParams)
  }

  public <T> Single<Long> insert(final String sql,
                                 final T object) {
    new PgOperation(transaction).insert(sql, object, false)
  }

  public <T> Single<Long> update(final String namedSql,
                                 final T object,
                                 final Map mapParams = [:]) {
    new PgOperation(transaction).update(namedSql, mapParams + object.properties)
  }

  Single<Long> delete(final String namedSql,
                      final Map mapParams = [:]) {
    new PgOperation(transaction).delete(namedSql, mapParams)
  }

}

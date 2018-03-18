package com.github.leosilvadev.groovypgasync.sql

/**
 * Created by leonardo on 3/18/18.
 */
class Query {

  final String sql
  final List<Object> params

  Query(final String sql, final List<Object> params) {
    this.sql = sql
    this.params = params
  }
}

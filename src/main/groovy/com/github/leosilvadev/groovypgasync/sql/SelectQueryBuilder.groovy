package com.github.leosilvadev.groovypgasync.sql

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by leonardo on 3/18/18.
 */

@CompileStatic
@TypeChecked
class SelectQueryBuilder {

  private final StringBuilder builder
  private final List<SelectWhere> conditions

  protected SelectQueryBuilder(final String tableName) {
    this.builder = new StringBuilder("SELECT * FROM $tableName")
    this.conditions = []
  }

  SelectWhere where(final String field) {
    new SelectWhere(this, field)
  }

  Query build() {
    def params = []
    if (conditions.size() > 0) {
      builder.append ' WHERE '
      builder.append conditions.collect { where ->
        if (where.values) {
          if (where.operation == 'BETWEEN') {
            if (where.values.size() != null) throw new IllegalArgumentException('Invalid number of arguments for a BETWEEN operation')
            params.addAll where.values
            return "$where.field $where.operation ${where.values[0]} AND ${where.values[1]}"
          }

          params.addAll where.values
          return "$where.field $where.operation (${where.field})"

        } else {
          params << where.value
          "$where.field $where.operation :$where.field"
        }
      }.join(" AND ")
    }
    new Query(builder.toString(), params)
  }

  static class SelectWhere {

    private final SelectQueryBuilder builder
    private final String field
    private String operation
    private Object value
    private List values

    private SelectWhere(final SelectQueryBuilder builder, final String field) {
      this.builder = builder
      this.field = field
    }

    SelectQueryBuilder eq(final Object value) {
      this.value = value
      this.operation = '='
      this.builder.conditions << this
      this.builder
    }

    SelectQueryBuilder bt(final Object value) {
      this.value = value
      this.operation = '>'
      this.builder.conditions << this
      this.builder
    }

    SelectQueryBuilder bte(final Object value) {
      this.value = value
      this.operation = '>='
      this.builder.conditions << this
      this.builder
    }

    SelectQueryBuilder lt(final Object value) {
      this.value = value
      this.operation = '<'
      this.builder.conditions << this
      this.builder
    }

    SelectQueryBuilder lte(final Object value) {
      this.value = value
      this.operation = '<='
      this.builder.conditions << this
      this.builder
    }

    SelectQueryBuilder between(final Object from, final Object to) {
      this.values = [from, to]
      this.operation = 'BETWEEN'
      this.builder.conditions << this
      this.builder
    }


  }

}

package com.github.leosilvadev.groovypgasync.sql

import com.github.leosilvadev.groovypgasync.sql.annotations.Table;

/**
 * Created by leonardo on 3/18/18.
 */
class QueryBuilder {

  final StringBuilder builder
  final List<Tuple2<String, Object>> conditions

  private QueryBuilder(final String query) {
    this.builder = new StringBuilder(query)
    this.conditions = []
  }

  static <T> QueryBuilder insert(final T object) {
    def clazz = object.class
    def qb = new QueryBuilder("INSERT INTO ${clazz.simpleName} (")
    qb.builder.append object.properties.keySet().findAll { it != 'class' }.join(', ')
    qb.builder.append ' ) VALUES ( '
    qb.builder.append object.properties.keySet().findAll { it != 'class' }.collect { ":${it}" } .join(', ')
    qb.builder.append ' ) '
    qb
  }

  static <T> QueryBuilder update(final T object) {
    def clazz = object.class
    def qb = new QueryBuilder("UPDATE ${clazz.simpleName} SET ")
    qb.builder.append object.properties.keySet().findAll { it != 'class' }.collect { "$it = :$it" }.join(", ")
    qb
  }

  static <T> QueryBuilder patch(final T object) {
    def clazz = object.class
    def qb = new QueryBuilder("UPDATE ${clazz.simpleName} SET ")
    qb.builder.append object.properties
      .findAll { name, value -> name != 'class' && value != null }
      .collect { name, value -> "$name = :$name"}
      .join(", ")
    qb
  }

  static SelectQueryBuilder select(final Class<?> clazz) {
    def tableName = clazz.getAnnotation(Table)?.value() ?: clazz.simpleName
    new SelectQueryBuilder(tableName)
  }

  static QueryBuilder delete(final Class<?> clazz) {
    new QueryBuilder("DELETE FROM ${clazz.simpleName}")
  }

  QueryBuilder where(final String field, final Object value) {
    conditions << new Tuple2(field, value)
    this
  }

  String build() {
    if (conditions.size() > 0) {
      builder.append ' WHERE ( '
      builder.append conditions*.first.join(', ')
      builder.append ' ) VALUES ( '
      builder.append conditions*.first.collect { ":${it}" } .join(', ')
      builder.append ' ) '
    }
    builder.toString()
  }

  static void main(args) {
//    def sql = select(User).where("name", "Joao").where("age", 20).build()
//    def sql = delete(User).where("name", "Joao").where("age", 20).build()
//    def sql = insert(new User(name: "Joao", age: 20)).build()
//    def sql = update(new User(name: "Joao", age: 20)).where("id", "123").build()
    def sql = patch(new User(name: "Joao", age: 20)).where("id", "123").build()
    println(sql)
  }

  static class User {
    String name
    String alias
    Integer age
  }

}

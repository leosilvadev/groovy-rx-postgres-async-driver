package com.github.leosilvadev.groovypgasync

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
class PgDbParams {

  static Tuple2<String, List> namedParameters(String sql, Map parameters) {
    if (parameters) {
      def neededParams = sql.findAll(/:\w+/)
      def orderedParameters = []
      def index = 0

      neededParams.each { param ->
        def paramName = param.substring(1)
        def paramValue = parameters[paramName]

        if (paramValue instanceof Tuple2) {
          sql = sql.replace(param, "\$${++index} AND \$${++index}")
          orderedParameters << paramValue.getFirst()
          orderedParameters << paramValue.getSecond()

        } else if (paramValue instanceof List) {
          sql = sql.replace(param, paramValue.collect {
            "\$${++index}"
          }.join(', '))
          orderedParameters.addAll paramValue

        } else {
          sql = sql.replace(param, "\$${++index}")
          orderedParameters << paramValue
        }
      }

      new Tuple2(sql, orderedParameters)

    } else {
      new Tuple2(sql, [])

    }
  }

}

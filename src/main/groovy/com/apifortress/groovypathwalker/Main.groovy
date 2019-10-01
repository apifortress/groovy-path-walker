package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        def map = ['foo':['cose':[['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1].foo1'
        printNavigation(map, path)

        map = [['foo':'bar'],['foo':'bar1']]
        path = '[1].foo'
        printNavigation(map, path)

        map = ['1','2','3','4','5']
        path = '[1]'
        printNavigation(map, path)

        map = ['foo':['cose':['foo':['foo1':'bar1']]]]
        path = 'foo.cose.foo.foo1'
        printNavigation(map, path)

        map = ['foo':['cose':['foo':['a','b','c']]]]
        path = 'foo.cose.foo[2]'
        printNavigation(map, path)

        map = ['foo':['cose':['foo':[['a':'a'],['b':'b'],['c':'c']]]]]
        path = 'foo.cose.foo[2]'
        printNavigation(map, path)
    }

    private static void printNavigation(def map, String path) {
        println "************************"
        println (JsonOutput.toJson(map))
        println "Path: " + path
        println "Result: "+ navigate(map, path)
    }

    static private def navigate(def map, String path){
        String key =""
        def result = null
        def index = -1
        def rec = false

        if (path.contains('.')) {
            key = path.split('\\.')[0]
            path = path.substring(path.indexOf('.')+1)
            rec = true
        } else {
            key = path
        }

        if (key.contains('[') && key.contains(']')) {
            index = key.substring(key.indexOf('[') + 1, key.indexOf(']')) as int
            key = key.substring(0, key.indexOf('['))
        }
        // funzioni

        result = obtainResult(rec, index, map, key, path, result)

        return result
    }

    private static Object obtainResult(boolean rec, int index, map, String key, String path, result) {
        if (rec && index > 0) {
            if (map instanceof Map)
                result = navigate(map.get(key)[index], path)
            if (map instanceof List)
                result = navigate(map[index], path)
        }
        if (rec && index < 0)
            result = navigate(map.get(key), path)


        if (!rec && index > 0) {
            if (map instanceof Map)
                result = map.get(key)[index]
            if (map instanceof List)
                result = map[index]
        }
        if (!rec && index < 0)
            result = map.get(key)
        result
    }
}


package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        def map = ['foo':['cose':[['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1].foo1'
/*        printNavigation(map, path)


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
        printNavigation(map, path)*/

        map = ['foo':['cose':['foo':[['a':'a'],['b':'b'],['c':'c']]]]]
        path = 'foo.cose.foo[2]'
        printNavigation(map, path)

        /*
        map = ['foo':'bar']
        path = 'banana'
        printNavigation(map, path)

        map = ['foo':['name':'bar']]
        path = 'foo.name'
        //path = 'foo["name"]'
        printNavigation(map, path)


        //path = 'foo["name"]'
        //printNavigation(map, path)
        /*
                {
                    "foo":{
                    "name":"bar"
                }
                }*/
    }

    private static void printNavigation(def map, String path) {
        println "************************"
        println (JsonOutput.toJson(map))
        println "Path: " + path
        //println "Result: "+ navigate(map, path)
        println "Result: "+ navigate(map, path)
    }

    static private def navigate(def item,def path,def index = 0){
        def element
        List paths = path.split('\\.')
        String key = paths[0]
        path = path.substring(path.indexOf('.')+1)

        if (key.contains('[') && key.contains(']')) {
            index = key.substring(key.indexOf('[') + 1, key.indexOf(']')) as int
            key = key.substring(0, key.indexOf('['))
        }

        if (item instanceof Map)
            element = navigate(item.get(key),path,index)
        else if (item instanceof List)
            element = navigate(item[index],path,index)
        else
            element = item

        return element
    }
}


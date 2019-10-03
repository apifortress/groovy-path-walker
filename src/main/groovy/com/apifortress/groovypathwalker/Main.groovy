package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        test1()
        test2()
        test3()
        test4()
        test5()
        test6()
        test7()
        test8()
        test9()
        test10()
        test11()
        test12()
        test13()
        test14()
    }

    private static void test14() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1'
        //println map.foo.cose.foo.foo1.ciccio
        //def mp = ["TopicName" : "Maps", "TopicDescription" : "Methods in Maps"]
        //println(map.containsKey("cose"));
        //println(mp.containsKey("Topic"));
        printNavigation(map, path)
    }

    private static void test13() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1.coo'
        //println map.foo.cose.foo.foo1.ciccio
        //def mp = ["TopicName" : "Maps", "TopicDescription" : "Methods in Maps"]
        //println(map.containsKey("cose"));
        //println(mp.containsKey("Topic"));
        printNavigation(map, path)
    }

    private static void test12() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo["cose"][\'foo\']["foo1"]'
        //def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
    }

    private static void test11() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo["cose"][1]["foo1"]'
        printNavigation(map, path)
    }

    private static void test10() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1]["foo1"]'
        printNavigation(map, path)
    }

    private static void test9() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo[\'foo1\']'
        printNavigation(map, path)
    }

    private static void test8() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
    }

    private static void test7() {
        def map = ['foo': 'bar']
        def path = 'banana'
        printNavigation(map, path)
    }

    private static void test6() {
        def map = ['foo': ['cose': ['foo': [['a': 'a'], ['b': 'b'], ['c': 'c']]]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
    }

    private static void test5() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
    }

    private static void test4() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1'
        //def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
    }

    private static void test3() {
        def map = ['1', '2', '3', '4', '5']
        def path = '[1]'
        printNavigation(map, path)
    }

    private static void test2() {
        def map = [['foo': 'bar'], ['foo': 'bar1']]
        def path = '[1].foo'
        printNavigation(map, path)
    }

    private static void test1() {
        def map = ['foo': ['cose': [['foo': 'bar'], ['foo1': 'bar1']]]]
        def path = 'foo.cose[1].foo1'
        printNavigation(map, path)
    }


    private static void printNavigation(def item, String path) {
        println "************************"
        println (JsonOutput.toJson(item))
        println "Path: " + path
        //println "Result: "+ navigate(map, path)
        //String input ='foo.cose[1]["foo1"]'
        path = PathWalker.sanifyPath(path)
        //println "NEW Path: " + path
        List paths = PathWalker.paths(path)
        //println "Result: "+ PathWalker.navigate(item, paths,paths.size())
        println "Result: "+ PathWalker.navigate(item, paths)
    }




}


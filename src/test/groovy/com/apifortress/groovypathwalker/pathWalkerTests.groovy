package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import org.junit.Test

import static org.junit.Assert.*

class pathWalkerTests {
    @Test
    public void test14() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1'
        //println map.foo.cose.foo.foo1.ciccio
        //def mp = ["TopicName" : "Maps", "TopicDescription" : "Methods in Maps"]
        //println(map.containsKey("cose"));
        //println(mp.containsKey("Topic"));
        printNavigation(map, path)
    }
    @Test
    public void test13() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1.coo'
        //println map.foo.cose.foo.foo1.ciccio
        //def mp = ["TopicName" : "Maps", "TopicDescription" : "Methods in Maps"]
        //println(map.containsKey("cose"));
        //println(mp.containsKey("Topic"));
        printNavigation(map, path)
    }
    @Test
    public void test12() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo["cose"][\'foo\']["foo1"]'
        //def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
    }
    @Test
    public void test11() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo["cose"][1]["foo1"]'
        printNavigation(map, path)
    }
    @Test
    public void test10() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1]["foo1"]'
        printNavigation(map, path)
    }
    @Test
    public void test9() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo[\'foo1\']'
        printNavigation(map, path)
    }
    @Test
    public void test8() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
    }
    @Test
    public void test7() {
        def map = ['foo': 'bar']
        def path = 'banana'
        printNavigation(map, path)
    }
    @Test
    public void test6() {
        def map = ['foo': ['cose': ['foo': [['a': 'a'], ['b': 'b'], ['c': 'c']]]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
    }
    @Test
    public void test5() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
    }
    @Test
    public void test4() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1'
        //def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
    }
    @Test
    public void test3() {
        def map = ['1', '2', '3', '4', '5']
        def path = '[1]'
        printNavigation(map, path)
    }
    @Test
    public void test2() {
        def map = [['foo': 'bar'], ['foo': 'bar1']]
        def path = '[1].foo'
        printNavigation(map, path)
    }
    @Test
    public void test1() {
        def map = ['foo': ['cose': [['foo': 'bar'], ['foo1': 'bar1']]]]
        def path = 'foo.cose[1].foo1'
        printNavigation(map, path)
    }

    public void printNavigation(def item, String path) {
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

package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import org.junit.Test

import static org.junit.Assert.*

class pathWalkerTests {
    @Test
    public void test14() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test13() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1.coo'
        printNavigation(map, path)
        //checkNavigation(map,path,null)
    }
    @Test
    public void test12() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo["cose"][\'foo\']["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test11() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo["cose"][1]["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test10() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1]["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test9() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo[\'foo1\']'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test8() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test7() {
        def map = ['foo': 'bar']
        def path = 'banana'
        printNavigation(map, path)
        checkNavigation(map,path,null)
    }
    @Test
    public void test6() {
        def map = ['foo': ['cose': ['foo': [['a': 'a'], ['b': 'b'], ['c': 'c']]]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
        checkNavigation(map,path,['c':'c'])
    }
    @Test
    public void test5() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
        checkNavigation(map,path,'c')
    }
    @Test
    public void test4() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1'
        //def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test3() {
        def map = ['1', '2', '3', '4', '5']
        def path = '[1]'
        printNavigation(map, path)
        checkNavigation(map,path,'2')
    }
    @Test
    public void test2() {
        def map = [['foo': 'bar'], ['foo': 'bar1']]
        def path = '[1].foo'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void test1() {
        def map = ['foo': ['cose': [['foo': 'bar'], ['foo1': 'bar1']]]]
        def path = 'foo.cose[1].foo1'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }

    public void checkNavigation(def item, String path, def expected) {
        path = PathWalker.sanifyPath(path)
        List paths = PathWalker.paths(path)
        def element = PathWalker.navigate(item, paths)
        assertEquals(expected,element)
    }

    public void printNavigation(def item, String path) {
        println "************************"
        println (JsonOutput.toJson(item))
        println "Path: " + path
        path = PathWalker.sanifyPath(path)
        List paths = PathWalker.paths(path)
        println "Result: "+ PathWalker.navigate(item, paths)
    }
}

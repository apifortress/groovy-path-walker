package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import org.junit.Test

import static org.junit.Assert.*

class PathWalkerTest {
    @Test
    public void testPlain() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1'
        //def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testListInMidle() {
        def map = ['foo': ['cose': [['foo': 'bar'], ['foo1': 'bar1']]]]
        def path = 'foo.cose[1].foo1'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testListAtStart() {
        def map = [['foo': 'bar'], ['foo': 'bar1']]
        def path = '[1].foo'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testList() {
        def map = ['1', '2', '3', '4', '5']
        def path = '[1]'
        printNavigation(map, path)
        checkNavigation(map,path,'2')
    }

    @Test
    public void testListAtTheEnd() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
        checkNavigation(map,path,'c')
    }
    @Test
    public void testListAtTheEndWithMaps() {
        def map = ['foo': ['cose': ['foo': [['a': 'a'], ['b': 'b'], ['c': 'c']]]]]
        def path = 'foo.cose.foo[2]'
        printNavigation(map, path)
        checkNavigation(map,path,['c':'c'])
    }
    @Test
    public void testNotExistingKey() {
        def map = ['foo': 'bar']
        def path = 'banana'
        printNavigation(map, path)
        checkNavigation(map,path,null)
    }
    @Test
    public void testAccesWithDoppiAppici() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testAccessWithSingleAppice() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = "foo.cose.foo['foo1']"
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testArrayInMiddleAccesDoppioAppice() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1]["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }

    @Test
    public void testDoppioAppiceListInMidleDoppioAppice() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo["cose"][1]["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testAppiciMixed() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo["cose"][\'foo\']["foo1"]'
        printNavigation(map, path)
        checkNavigation(map,path,'bar1')
    }

    @Test
    public void testNotExistingEndingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1.coo'
        printNavigation(map, path)
        checkNavigationException(map,path)
    }
    @Test
    public void testQuestionMarkk() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo?.foo1'
        printNavigation(map, path)
        checkNavigation(map, path, 'bar1')
    }
    @Test
    public void testQuestionMArkNotExistingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.fo?.foo1'
        printNavigation(map, path)
        checkNavigation(map, path, null)
    }

    public void checkNavigationException(def item, String path) {
        path = PathWalker.sanifyPath(path)
        List paths = PathWalker.paths(path)
        String element = PathWalker.navigate(item, paths)
        assertTrue(element.startsWith("Exception"))
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

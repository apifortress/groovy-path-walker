package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import org.junit.Test

import static org.junit.Assert.*

class PathWalkerTest {
    @Test
    public void testPlain() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]]]
        def path = 'payload.foo.cose.foo.foo1'
        checkNavigation(null,path,'bar1',map)
    }
    @Test
    public void testListInMidle() {
        def map = ['foo': ['cose': [['foo': 'bar'], ['foo1': 'bar1']]]]
        def path = 'foo.cose[1].foo1'
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testListAtStart() {
        def map = [['foo': 'bar'], ['foo': 'bar1']]
        def path = '[1].foo'
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testList() {
        def map = ['1', '2', '3', '4', '5']
        def path = '[1]'
        checkNavigation(map,path,'2')
    }

    @Test
    public void testListAtTheEnd() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[2]'
        checkNavigation(map,path,'c')
    }
    @Test
    public void testListAtTheEndWithMaps() {
        def map = ['foo': ['cose': ['foo': [['a': 'a'], ['b': 'b'], ['c': 'c']]]]]
        def path = 'foo.cose.foo[2]'
        checkNavigation(map,path,['c':'c'])
    }
    @Test
    public void testNotExistingKey() {
        def map = ['foo': 'bar']
        def path = 'banana'
        checkNavigation(map,path,null)
    }
    @Test
    public void testAccesWithDoppiAppici() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo["foo1"]'
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testAccessWithSingleAppice() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = "foo.cose.foo['foo1']"
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testArrayInMiddleAccesDoppioAppice() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1]["foo1"]'
        checkNavigation(map,path,'bar1')
    }

    @Test
    public void testDoppioAppiceListInMidleDoppioAppice() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo["cose"][1]["foo1"]'
        checkNavigation(map,path,'bar1')
    }
    @Test
    public void testAppiciMixed() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo["cose"][\'foo\']["foo1"]'
        checkNavigation(map,path,'bar1')
    }

    @Test
    public void testNotExistingEndingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1.coo'
        checkNavigationException(map,path)
    }
    @Test
    public void testQuestionMarkk() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo?.foo1'
        checkNavigation(map, path, 'bar1')
    }
    @Test
    public void testQuestionMArkNotExistingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.fo?.foo1'
        checkNavigation(map, path, null)
    }
    @Test
    public void testVariable() {
        def map = ['a': ['b': ['c': ['d': 'bar1']]]]
        def scope = ['var':'d']
        def path = 'a.b.c[var]'
        checkNavigation(map, path, 'bar1',scope)
    }
    @Test
    public void testPlainScope() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]],'var':'val']
        def path = 'payload.foo.cose.foo.foo1'
        checkNavigation(null,path,'bar1',map)
    }

    @Test
    public void testPlainScopeVar() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.foo.cose.foo[var]'
        checkNavigation(null,path,'bar1',map)
    }

    public void checkNavigationException(def item, String path) {
        path = PathWalker.sanifyPath(path)
        List paths = PathWalker.paths(path)
        String element = PathWalker.navigate(item, paths)
        assertTrue(element.startsWith("Exception"))
    }

    public void checkNavigation(def item, String path, def expected,def scope = null) {
        println "************************"
        if (item)  println "Item: " + (JsonOutput.toJson(item))
        if (scope) println "Scope: " + (JsonOutput.toJson(scope))
        println "Path: " + path
        path = PathWalker.sanifyPath(path)
        List paths = PathWalker.paths(path)
        def element = PathWalker.navigate(item, paths, scope)
        println "Result: " + element
        assertEquals(expected,element)
    }

}

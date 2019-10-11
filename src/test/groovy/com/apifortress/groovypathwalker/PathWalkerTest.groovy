package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import groovy.json.internal.LazyMap
import groovy.util.slurpersupport.GPathResult
import org.junit.Test

import static org.junit.Assert.*

class PathWalkerTest {
    @Test
    public void testPlain() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]]]
        def path = 'payload.foo.cose.foo.foo1'
        navigate(null,path,'bar1',map)
    }
    @Test
    public void testListInMidle() {
        def map = ['foo': ['cose': [['foo': 'bar'], ['foo1': 'bar1']]]]
        def path = 'foo.cose[1].foo1'
        navigate(map,path,'bar1')
    }
    @Test
    public void testListAtStart() {
        def map = [['foo': 'bar'], ['foo': 'bar1']]
        def path = '[1].foo'
        navigate(map,path,'bar1')
    }
    @Test
    public void testList() {
        def map = ['1', '2', '3', '4', '5']
        def path = '[1]'
        navigate(map,path,'2')
    }

    @Test
    public void testListAtTheEnd() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[2]'
        navigate(map,path,'c')
    }
    @Test
    public void testListAtTheEndWithMaps() {
        def map = ['foo': ['cose': ['foo': [['a': 'a'], ['b': 'b'], ['c': 'c']]]]]
        def path = 'foo.cose.foo[2]'
        navigate(map,path,['c':'c'])
    }
    @Test
    public void testNotExistingKey() {
        def map = ['foo': 'bar']
        def path = 'banana'
        navigate(map,path,null)
    }
    @Test
    public void testAccesWithDoppiAppici() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo["foo1"]'
        navigate(map,path,'bar1')
    }
    @Test
    public void testAccessWithSingleAppice() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = "foo.cose.foo['foo1']"
        navigate(map,path,'bar1')
    }
    @Test
    public void testArrayInMiddleAccesDoppioAppice() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1]["foo1"]'
        navigate(map,path,'bar1')
    }

    @Test
    public void testDoppioAppiceListInMidleDoppioAppice() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo["cose"][1]["foo1"]'
        navigate(map,path,'bar1')
    }
    @Test
    public void testAppiciMixed() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo["cose"][\'foo\']["foo1"]'
        navigate(map,path,'bar1')
    }

    @Test
    public void testNotExistingEndingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1.coo'
        navigateWithException(map,path)
    }
    @Test
    public void testQuestionMarkk() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo?.foo1'
        navigate(map, path, 'bar1')
    }
    @Test
    public void testQuestionMArkNotExistingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.fo?.foo1'
        navigate(map, path, null)
    }
    @Test
    public void testVariable() {
        def map = ['a': ['b': ['c': ['d': 'bar1']]]]
        def scope = ['var':'d']
        def path = 'a.b.c[var]'
        navigate(map, path, 'bar1',scope)
    }
    @Test
    public void testPlainScope() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]],'var':'val']
        def path = 'payload.foo.cose.foo.foo1'
        navigate(null,path,'bar1',map)
    }

    @Test
    public void testPlainScopeVar() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.foo.cose.foo[var]'
        navigate(null,path,'bar1',map)
    }

    @Test
    public void testPlainScopeSize() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.size()'
        navigate(null,path,'1',map)
    }

    @Test
    public void testPlainScopeBytes() {
        def map = ['payload':['a': ['b': ['c': "prova"]]],'var':'foo1']
        def path = 'payload.a.b.c.bytes'
        navigate(null,path,'[B@6b26e945',map,false)
    }
/*
    @Test
    public void testPlainScopeCASEINSENSITIVE() {
        def map = ['payload':['a': ['b': ['c': "prova"]]],'var':'foo1']
        def path = 'payload.a.b.c.CASE_INSENSITIVE_ORDER'
        navigate(null,path,'[B@794cb805',map)
    }*/

    @Test
    public void testPlainScopeValues() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.values()'
        navigate(null,path,'[bar1]',map)
    }

    @Test
    public void testPlainScopeKeySet() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.keySet()'
        navigate(null,path,'[foo1]',map)
    }

    @Test
    public void testPlainScopePick() {
        initMetaclasses()
        def map = ['payload':['a': ['b': ['c': ['a','b','c']]]],'var':'foo1']
        def valuesList = ['a','b','c']
        def path = 'payload.a.b.c.pick()'
        navigateRandomValues(null,path,valuesList,map)
    }

    @Test
    public void testPlainScopePickWithIndex() {
        initMetaclasses()
        def map = ['payload':['a': ['b': ['c': ['a','b','c','d','e','f','g','h']]]],'var':'foo1']
        def valuesList = ['a','b','c','d','e','f','g','h']
        def path = 'payload.a.b.c.pick(3)'
        navigateRandomValues(null,path,valuesList,map)
    }


    @Test
    public void testPlainUnsupportedBraces() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.{b}.c.size()'
        println path
        def supported = GroovyPathWalker.isSupported(path)
        println "Supported: " + supported
        assertFalse(supported)
    }

    @Test
    public void testPlainUnsupportedFunction() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.unsupported()'
        println path
        def supported = GroovyPathWalker.isSupported(path)
        println "Supported: " + supported
        assertFalse(supported)
    }

    @Test
    public void testPlainSupportedFunction() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.size()'
        println path
        def supported = GroovyPathWalker.isSupported(path)
        println "Supported: " + supported
        assertTrue(supported)
    }

    @Test
    public void testPlainUnSupportedStar() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.*'
        println path
        def supported = GroovyPathWalker.isSupported(path)
        println "Supported: " + supported
        assertFalse(supported)
    }

    @Test
    public void testPlainUnSupportedOperator() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c{it -> print it}'
        println path
        def supported = GroovyPathWalker.isSupported(path)
        println "Supported: " + supported
        assertFalse(supported)
    }

    @Test
    public void testPlainUnSupportedExclamation() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b!.c'
        println path
        def supported = GroovyPathWalker.isSupported(path)
        println "Supported: " + supported
        assertFalse(supported)
    }

    @Test
    public void testPlainUnSupportedAssignementOperator() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b=c'
        println path
        def supported = GroovyPathWalker.isSupported(path)
        println "Supported: " + supported
        assertFalse(supported)
    }

    public void navigateWithException(def item, String path) {
        printInformations(item, null, path)
        String element = GroovyPathWalker.walk(item, path)
        assertTrue(element.startsWith("Exception"))
    }

    public void navigate(def item, String path, def expected, def scope = null,def test = true) {
        printInformations(item, scope, path)
        def element = GroovyPathWalker.walk(item,path,scope)
        println "Result: " + element
        if (test) assertEquals(expected,element)
    }

    public void navigateRandomValues(def item, String path, def valuesList, def scope = null) {
        printInformations(item, scope, path)
        def element = GroovyPathWalker.walk(item,path,scope)
        def valuesIn = true
        item.each {
            valuesIn = valuesIn && it in valuesList
        }
        println "Result: " + element
        assertTrue(valuesIn)
    }

    private void printInformations(item, scope, String path) {
        println "************************"
        println "Item: " + (JsonOutput.toJson(item))
        println "Scope: " + (JsonOutput.toJson(scope))
        println "Path: " + path
    }

    public void initMetaclasses(){
        ArrayList.metaClass.pick { Integer q ->
            int quantity = q ?: 1
            int total = delegate.size();
            if(total == 0 && q == null)
                return null
            if(total == 0 && q != null)
                return [];
            if(quantity > total)
                quantity = total;
            ArrayList<Integer> pointers = new ArrayList<Integer>(total);
            for(int i=0;i<total;i++)
                pointers[i] = i;

            Collections.shuffle(pointers);
            pointers = pointers[0..quantity-1].sort();
            def items = [];
            for(int pointer : pointers)
                items.add(delegate[pointer]);
            if(q == null)
                return items[0]
            else
                return items;
        }

        ArrayList.metaClass.asJSON {
            return JsonOutput.prettyPrint(JsonOutput.toJson(delegate))
        }
        ArrayList.metaClass.getColumn { String letter ->
            return delegate[0]
        }
        LinkedHashMap.metaClass.asJSON{
            return JsonOutput.prettyPrint(JsonOutput.toJson(delegate))
        }
        LazyMap.metaClass.asJSON{
            return JsonOutput.prettyPrint(JsonOutput.toJson(delegate))
        }
        LinkedHashMap.metaClass.navigatePath{ String path -> return navigatePath(delegate,path,0)}

        GPathResult.metaClass.pick { Integer quantity ->
            int total = delegate.children().size()
            if(total == 0)
                return [];
            if(quantity > total)
                quantity = total;
            ArrayList<Integer> pointers = new ArrayList<Integer>(total);
            for(int i=0;i<total;i++)
                pointers[i] = i;
            Collections.shuffle(pointers);
            pointers = pointers[0..quantity-1].sort();
            def items = [];
            for(int pointer : pointers)
                items.add(delegate.children()[pointer]);

            return items;
        }
    }
}



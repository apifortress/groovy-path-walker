package com.apifortress.groovypathwalker

import com.apifortress.parsers.xmlparser2.XmlNode
import groovy.json.JsonOutput
import groovy.json.internal.LazyMap
import groovy.util.slurpersupport.GPathResult
import org.junit.Test

import java.lang.reflect.Field

import static org.junit.Assert.*

class PathWalkerTest {


    @Test
    public void testString() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]]]
        def path = '\'bar1\''
        navigate(null,path,'bar1',map)
    }
    @Test
    public void testInt() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]]]
        def path = '666'
        navigate(null,path,666,map)
    }
    @Test
    public void testDouble() {
        def map = ['payload':['foo': ['cose': ['foo': ['foo1': 'bar1']]]]]
        def path = '66.6'
        navigateDecimal(null,path,66.6,map)
    }
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
        navigate(map,path,'bar1',map)
    }

    @Test
    public void testListStringAccessorList() {
        def map = ['foo': ['a','b','c']]
        def path = 'foo[\'a\']'
        //navigate(map,path,'bar1')
        navigateWithException(map,path,map)
    }
    @Test
    public void testListAtStart() {
        def map = [['foo': 'bar'], ['foo': 'bar1']]
        def path = '[1].foo'
        navigate(map,path,'bar1',map)
    }
    @Test
    public void testList() {
        def map = ['1', '2', '3', '4', '5']
        def path = '[1]'
        navigate(map,path,'2',map)
    }
    @Test
    public void testListDoubleQuote() {
        def map = ['1', '2', '3', '4', '5']
        def path = '["1"]'
        //navigate(map,path,'2')
        navigateWithException(map,path,map)
    }
    @Test
    public void testListSingleQuote() {
        def map = ['foo':['1', '2', '3', '4', '5']]
        def path = 'foo[\'1\']'
        //navigate(map,path,'2')
        navigateWithException(map,path,map)
    }
    @Test
    public void testAccessorListDoubleQuote() {
        def map = ['foo':['1', '2', '3', '4', '5']]
        def path = 'foo["1"]'
        //navigate(map,path,'2')
        navigateWithException(map,path,map)
    }

    @Test
    public void testListAtTheEnd() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[2]'
        navigate(map,path,'c',map)
    }
    @Test
    public void testListAtTheEndWithMaps() {
        def map = ['foo': ['cose': ['foo': [['a': 'a'], ['b': 'b'], ['c': 'c']]]]]
        def path = 'foo.cose.foo[2]'
        navigate(map,path,['c':'c'],map)
    }
    @Test
    public void testNotExistingKey() {
        def map = ['foo': 'bar']
        def path = 'banana'
        navigate(map,path,null,map)
    }
    @Test
    public void testAccesWithDoubleQuotes() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo["foo1"]'
        navigate(map,path,'bar1',map)
    }
    @Test
    public void testAccessWithSingleQuote() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = "foo.cose.foo['foo1']"
        navigate(map,path,'bar1',map)
    }
    @Test
    public void testListAtTheEndSingleQuote() {
        def map = ['foo': ['cose': ['foo': ['a', 'b', 'c']]]]
        def path = 'foo.cose.foo[\'a\']'
        navigateWithException(map,path,map)
    }

    @Test
    public void testListDoubleQuotes() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo.cose[1]["foo1"]'
        navigate(map,path,'bar1',map)
    }

    @Test
    public void testDoubleQuoteListDoubleQuotes() {
        def map = ['foo': ['cose': [['foo':'bar'],['foo1':'bar1']]]]
        def path = 'foo["cose"][1]["foo1"]'
        navigate(map,path,'bar1',map)
    }

    @Test
    public void testMixedQuotes() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo["cose"][\'foo\']["foo1"]'
        navigate(map,path,'bar1',map)
    }

    @Test
    public void testNotExistingEndingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo.foo1.coo'
        navigate(map,path,null,map)
    }

    @Test
    public void testSafeNavigation(){
        def map = [data:[a:[1,2,3],b:[c:true]]]
        def path = 'data.b?.c'
        navigate(map,path,true,map)
    }

    @Test
    public void testSafeNavigation2(){
        def map = [data:[a:[1,2,3],b:null]]
        def path = 'data.b?.c'
        navigate(map,path,null,map)
    }

    @Test
    public void testQuestionMark() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.foo?.foo1'
        navigate(map, path, 'bar1',map)
    }

    @Test
    public void testQuestionMarkNullFunction() {
        def map = ['foo': ['cose': ['foo': null]]]
        def path = 'foo.cose.foo?.size()'
        navigate(map, path, null,map)
    }

    @Test
    public void testQuestionMarkNotExistingKey() {
        def map = ['foo': ['cose': ['foo': ['foo1': 'bar1']]]]
        def path = 'foo.cose.fo?.foo1'
        navigate(map, path, null,map)
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
        navigate(null,path,1,map)
    }

    @Test
    public void testPlainScopeBytes() {
        def map = ['payload':['a': ['b': ['c': "prova"]]],'var':'foo1']
        def path = 'payload.a.b.c.bytes'
        navigate(null,path,'[B@6b26e945',map,false)
    }

    @Test
    public void testPlainScopeValues() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.values()'
        def element = map.payload.a.b.c.values()
        navigate(null,path,element,map)
    }

    @Test
    public void testPlainScopeKeySet() {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.keySet()'
        def element = map.payload.a.b.c.keySet()
        navigate(null,path,element,map)
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

    @Test
    public void testPlainXml(){
        XmlNode node = new XmlNode(new XmlSlurper().parse(new File('stuff2.xml')))
        def path = 'a.b.c'
        navigate(node,path,node.a.b.c,node)
    }

    @Test
    public void testPlainXmlAttribute(){
        XmlNode node = new XmlNode(new XmlSlurper().parse(new File('stuff2.xml')))
        def path = 'a.@e'
        navigate(node,path,'ebar',node)
    }

    @Test
    public void testPlainXmlText(){
        XmlNode node = new XmlNode(new XmlSlurper().parse(new File('stuff2.xml')))
        def path = 'a.b.c.text'
        navigate(node,path,'bar1',node)
    }



    @Test
    public void testComplexXml(){
        XmlNode node = new XmlNode(new XmlSlurper().parse(new File('cardigan.xml')))
        def path = 'product[0].size[0]'
        navigate(node,path,node.product[0].size[0],node)
    }

    @Test
    public void testComplexXmlAttribute(){
        XmlNode node = new XmlNode(new XmlSlurper().parse(new File('cardigan.xml')))
        def path = 'product[0].size[0].@description'
        navigate(node,path,'Medium',node)
    }

    @Test
    public void testComplexXmlItemNumber(){
        XmlNode node = new XmlNode(new XmlSlurper().parse(new File('cardigan.xml')))
        def path = 'product[0].item_number'
        navigate(node,path,node.product[0].item_number,node)
    }

    @Test
    public void testGenericObjectGet(){
        def obj = new TestObject()
        def path = 'test'
        navigate(obj,path,'foobar: test',obj)
    }

    @Test
    public void testGenericWithoutObjectGet(){
        def obj = new TestObjectWithOutGet()
        def path = 'test'
        navigate(obj,path,null,obj)
        //navigateWithException(obj,path,obj)
    }

    private void navigateWithException(def item, String path,def scope) {
        printInformations(item, null, path)
        String element = GroovyPathWalker.walk(path,scope,item)
        println "Result: " + element
        assertTrue(element.startsWith("Exception") || element.startsWith("No signature"))
    }


    private void navigate(def item, def path, def expected, def scope,def test = true) {
        printInformations(item, scope, path)
        def element = GroovyPathWalker.walk(path,scope,item)
        println "Result: " + element
        if (test) assertEquals(expected,element)
    }

    private void navigateDecimal(def item, def path, def expected, def scope,def test = true) {
        printInformations(item, scope, path)
        def element = GroovyPathWalker.walk(path,scope,item)
        println "Result: " + element
        if (test) assertEquals(expected,element,0)
    }

    private void navigateRandomValues(def item, String path, def valuesList, def scope) {
        printInformations(item, scope, path)
        def element = GroovyPathWalker.walk(path,scope,item)
        def valuesIn = true
        item.each {
            valuesIn = valuesIn && it in valuesList
        }
        println "Result: " + element
        assertTrue(valuesIn)
    }

    private void printInformations(item, scope, def path) {
        if (scope instanceof XmlNode && item instanceof XmlNode) {
            println "************************"
            println "Item: " + item
            println "Scope: " + scope
            println "Path: " + path
        } else {
            println "************************"
            println "Item: " + (JsonOutput.toJson(item))
            println "Scope: " + (JsonOutput.toJson(scope))
            println "Path: " + path
        }
    }

    private void initMetaclasses(){
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



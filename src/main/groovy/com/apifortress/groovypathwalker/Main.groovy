package com.apifortress.groovypathwalker

import com.apifortress.parsers.xmlparser2.XmlNode

class Main {
    public static void main(String[] args) {
        XmlNode node
        def path
        def temp
        //XmlNode node = new XmlNode(new XmlSlurper().parse(new File('catalog.xml')))
        //def path = 'CATALOG[1].TITLE'
        //def temp = GroovyPathWalker.walk(path,node,node)
        /*println temp

        node = new XmlNode(new XmlSlurper().parse(new File('breakfast.xml')))
        path = 'breakfast_menu.food.description.price'
        temp = GroovyPathWalker.walk(path,node,node)
        println temp*/

        node = new XmlNode(new XmlSlurper().parse(new File('stuff2.xml')))
        path = 'a.b.c'
        temp = GroovyPathWalker.walk(path,node,node)
        println temp

        /*
        path = 'a.b.c.text'
        temp = GroovyPathWalker.walk(path,node,node)
        println temp

        //path = 'a.@e'
        path = "a['@e']"
        temp = GroovyPathWalker.walk(path,node,node)
        println temp*/


        node = new XmlNode(new XmlSlurper().parse(new File('cardigan.xml')))
        print(node.product[0].size[0])
        path = 'product[0].size[0]'
        temp = GroovyPathWalker.walk(path,node,node)
        println temp



    }
}


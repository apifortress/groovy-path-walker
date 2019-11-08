package com.apifortress.groovypathwalker

import com.apifortress.parsers.xmlparser2.XmlNode

class Main {
    public static void main(String[] args) {
        def map = ['payload':['a': ['b': ['c': ['foo1': 'bar1']]]],'var':'foo1']
        def path = 'payload.a.b.c.size()'
        def element = GroovyPathWalker.walk(path,map,map)
        println element

    }
}


package com.apifortress.groovypathwalker

import com.apifortress.parsers.xmlparser2.XmlNode

class Main {
    public static void main(String[] args) {
        def map = [payload:[a:true]]
        def path = 'payload.a'
        def element = GroovyPathWalker.walk(path,map,map)
        println element
        path = 'payload.a.ciccio'
        element = GroovyPathWalker.walk(path,map,map)
        println element

    }
}


package com.apifortress.groovypathwalker

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        String path = 'foo["cose"][foo]["foo1"]'

        if (path.matches(Regex.NORMALIZED_PATH_VARIABLE))
            println "math"

        path = path.replaceAll(Regex.NORMALIZED_PATH_DOUBLE_QUOTES, '.$1')
        println path
        path = path.replaceAll(Regex.NORMALIZED_PATH_VARIABLE, '.\\$$1\\$')
        println path


        List list = ['a','b','c','d','e']
        println list.indexOf('a')
        println list.indexOf('z')
    }
}


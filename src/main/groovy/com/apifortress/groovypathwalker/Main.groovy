package com.apifortress.groovypathwalker

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        String path = '["cose"]'

        if (path.matches(Regex.REGEX_SQUARE_BRACKETS_DOUBLE_QUOTE))
            println "math"

        path = 'object[\'accessor1\'][\'accessor2\']["accessor3"]'

        List paths = path.split('\\[').toList()
        println paths

    }
}


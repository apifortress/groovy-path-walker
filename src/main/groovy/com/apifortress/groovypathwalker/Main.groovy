package com.apifortress.groovypathwalker

class Main {
    public static void main(String[] args) {
        String path = '["cose"]'

        if (path.matches(Regex.REGEX_SQUARE_BRACKETS_DOUBLE_QUOTE))
            println "math"

        path = 'a.b.c[\'accessor1\'][\'accessor2\']["accessor3"].d.e.f'

        //List paths = path.split('\\[').toList()

        List paths = GroovyPathWalker.processPath(path)
        println paths
        println paths.get(0)

    }
}


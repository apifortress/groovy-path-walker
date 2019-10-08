package com.apifortress.groovypathwalker


import java.util.regex.*

class Main {
    public static void main(String[] args) {
        //Pattern pattern = Pattern.compile("\\[123\\]")
        //println "Matches: " + pattern.matches("\\[\\d*\\]", '[11]')

        println GroovyPathWalker.normalizePath('foo.cose["foo"]')
        println GroovyPathWalker.normalizePath('foo.cose[\'foo\']')
        println GroovyPathWalker.normalizePath('foo.cose[foo]')
        println GroovyPathWalker.normalizePath('foo.cose[1]')

        String regex = '\\$\\D*\\$'
        String xxx = GroovyPathWalker.normalizePath('foo.cose[foo]')

        println "Matches: " + Pattern.matches(regex, '$foo$')

    }
}


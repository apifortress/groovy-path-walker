package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        //Pattern pattern = Pattern.compile("\\[123\\]")
        //println "Matches: " + pattern.matches("\\[\\d*\\]", '[11]')

        println PathWalker.sanifyPath('foo.cose["foo"]')
        println PathWalker.sanifyPath('foo.cose[\'foo\']')
        println PathWalker.sanifyPath('foo.cose[foo]')
        println PathWalker.sanifyPath('foo.cose[1]')

        String regex = '\\$\\D*\\$'
        String xxx = PathWalker.sanifyPath('foo.cose[foo]')

        println "Matches: " + Pattern.matches(regex, '$foo$')

    }
}


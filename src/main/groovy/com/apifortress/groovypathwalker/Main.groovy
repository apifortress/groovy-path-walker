package com.apifortress.groovypathwalker

import groovy.json.JsonOutput
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("\\[123\\]")
        println "Matches: " + pattern.matches("\\[\\d*\\]", '[11]')

    }
}


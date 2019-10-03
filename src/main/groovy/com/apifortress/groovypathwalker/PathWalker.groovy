package com.apifortress.groovypathwalker

import java.util.regex.Pattern

class PathWalker {

    static public def navigate(def item,def paths){
        def element,key

        if (paths.size() <= 0)
            element =item
        else {
            (key, paths) = currentKey(paths)
            (key, paths, item) = navigateList(key, paths, item)
            element = navigateMap(item, key, paths, element)
        }

        return element
    }

    private static Object navigateMap(item, key, paths, element) {
        if (item instanceof Map && key != null
                || item instanceof String && key != null) {
            try {
                element = navigate(item.get(key), paths)
            } catch (Exception ex) {
                element = "Exception: " + ex.toString()
            }
        } else
            element = item
        element
    }

    private static def navigateList(def key, def paths, def item){
        String regex = "\\w*\\[\\d*\\]"
        def index

        if (Pattern.matches(regex, key)) {
            (key, index) = sanifyKeyListIndex(key)
            item = itemFromList(key, item, index)
            (key,paths) = currentKey(paths)
            return [key,paths,item]
        } else {
            return [key,paths,item]
        }
    }

    private static def navigateVariables(def key, def paths, def item){
        String regex = '\\$\\D*\\$'
        def index

        if (Pattern.matches(regex, key)) {
            (key, index) = sanifyKeyListIndex(key)
            item = itemFromList(key, item, index)
            (key,paths) = currentKey(paths)
            return [key,paths,item]
        } else {
            return [key,paths,item]
        }
    }

    private static Object itemFromList(key, item, index) {
        if (key != '')
            item = item.get(key)
        item = item[index]
        item
    }

    private static List sanifyKeyListIndex(key) {
        def index
        index = key.substring(key.indexOf('[') + 1, key.indexOf(']')) as int
        key = key.substring(0, key.indexOf('['))
        [key, index]
    }

    private static def currentKey(paths) {
        def key
        if (paths.size() > 0)
            key = paths.remove(0)
        else
            key == null
        return [key,paths]
    }

    private static List paths(String path) {
        List paths = path.split('\\.')
        paths
    }

    public static String sanifyPath(String path) {
        path = path.replaceAll("\\[\"(.*?)\"\\]", '.$1')
        path = path.replaceAll("\\[\'(.*?)\'\\]", '.$1')
        path = path.replaceAll("\\[(\\D*)\\]", '.\\$$1\\$')
        path = path.replaceAll("\\?", '')
        path
    }
}

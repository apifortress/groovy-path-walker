package com.apifortress.groovypathwalker

import java.util.regex.Pattern

class PathWalker {

    static public def navigate(def item,def paths, def scope = null){
        def element,key

        if (paths.size() <= 0)
            element =item
        else {
            (key, paths) = processKey(paths)
            (key, paths, item) = processList(key, paths, item)
            (key, paths, item) = processVariable(key, paths, item,scope)
            element = process(item, key, paths, element,scope)
        }

        return element
    }

    private static def process(item, key, paths, element, scope) {
        if (item instanceof Map && key != null
                || item instanceof String && key != null) {
            try {
                element = navigate(item.get(key), paths, scope)
            } catch (Exception ex) {
                element = "Exception: " + ex.toString()
            }
        } else
            element = item
        element
    }

    private static def processList(def key, def paths, def item){
        String regex = "\\w*\\[\\d*\\]"
        def index

        if (Pattern.matches(regex, key)) {
            index = listIndex(key,'[',']')
            key = sanifyKey(key,'[')
            item = itemFromList(key, item, index)
            (key,paths) = processKey(paths)
            return [key,paths,item]
        } else {
            return [key,paths,item]
        }
    }

    private static def processVariable(def key, def paths, def item, def scope){
        String regex = '\\$\\D*\\$'
        def index

        if (key && Pattern.matches(regex, key)) {
            key = sanifyKey(key,'$','$')
            index = scope.get(key)
            item = item.get(index)
            (key,paths) = processKey(paths)
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

    private static def sanifyKey(key,c) {
        key = key.substring(0, key.indexOf(c))
        key
    }

    private static def sanifyKey(key,s,e) {
        key = key.substring(key.indexOf(s) + 1)
        key = key.substring(0,key.indexOf(e))
        key
    }

    private static def listIndex(key,s,e) {
        def index
        index = key.substring(key.indexOf(s) + 1, key.indexOf(e)) as int
        index
    }

    private static List sanifyKeyListIndex(key) {
        def index
        index = key.substring(key.indexOf('[') + 1, key.indexOf(']')) as int
        key = key.substring(0, key.indexOf('['))
        [key, index]
    }

    private static def processKey(paths) {
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

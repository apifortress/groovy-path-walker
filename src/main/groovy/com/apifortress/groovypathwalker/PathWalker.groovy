package com.apifortress.groovypathwalker

import groovy.transform.CompileStatic

import java.util.regex.Pattern

class PathWalker {

    public static def walk(def item,def path, def scope = null){
        def sanifiedPath = PathWalker.sanifyPath(path)
        List paths = PathWalker.processPath(sanifiedPath)
        return processWalk(item, paths, scope)
    }

    private static def processWalk(def item, def paths, def scope = null){
        def element,key

        if (!item)
            item = scope

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

    private static def process(def item, String key, List paths, element, def scope) {
        if (item instanceof Map && key != null
                || item instanceof String && key != null) {
            try {
                element = processWalk(item.get(key), paths, scope)
            } catch (Exception ex) {
                element = "Exception: " + ex.toString()
            }
        } else
            element = item
        element
    }

    private static def processList(String key, List paths, def item){
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


    private static def processVariable(String key, List paths, def item, def scope){
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


    private static def itemFromList(String key,def item,int index) {
        if (key != '')
            item = item.get(key)
        item = item[index]
        return item
    }

    @CompileStatic
    private static def sanifyKey(String key,String c) {
        key = key.substring(0, key.indexOf(c))
        key
    }

    @CompileStatic
    private static def sanifyKey(String key, String s, String e) {
        key = key.substring(key.indexOf(s) + 1)
        key = key.substring(0,key.indexOf(e))
        return key
    }

    @CompileStatic
    private static def listIndex(String key,String s, String e) {
        def index
        index = key.substring(key.indexOf(s) + 1, key.indexOf(e)) as int
        return index
    }

    @CompileStatic
    private static def processKey(List paths) {
        String key
        if (paths.size() > 0)
            key = paths.remove(0)
        else
            key == null
        return [key,paths]
    }

    @CompileStatic
    public static List processPath(String path) {
        List paths = path.split('\\.').toList()
        return paths
    }

    @CompileStatic
    public static String sanifyPath(String path) {
        path = path.replaceAll("\\[\"(.*?)\"\\]", '.$1')
        path = path.replaceAll("\\[\'(.*?)\'\\]", '.$1')
        path = path.replaceAll("\\[(\\D*)\\]", '.\\$$1\\$')
        path = path.replaceAll("\\?", '')
        return path
    }
}

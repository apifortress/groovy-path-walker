package com.apifortress.groovypathwalker

import groovy.transform.CompileStatic
import com.apifortress.groovypathwalker.utils.impl.Functions

import java.util.regex.Pattern

class PathWalker {

    private static final String REGEX_REGEX = "\\w*\\(\\d?\\)"
    private static final String REGEX_VAR = '\\$\\D*\\$'
    private static final String REGEX_LIST = "\\w*\\[\\d*\\]"

    private static final String START_FUNC = '('
    private static final String START_VAR = '$'
    private static final String START_LIST = '['

    private static final String END_FUNC = ')'
    private static final String END_VAR = '$'
    private static final String END_LIST = ']'

    private static final String SANIFY_PATH_DOUBLE_QUOTES = "\\[\"(.*?)\"\\]"
    private static final String SANIFY_PATH_SINGLE_QUOTES = "\\[\'(.*?)\'\\]"
    private static final String SANIFY_PATH_VARIABLE = "\\[(\\D*)\\]"
    private static final String SANIFY_PATH_QUESTIONE_MARK = "\\?"




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
            (key, paths, item) = processFunction(key, paths, item)
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
        String regex = PathWalker.REGEX_LIST
        def index

        if (Pattern.matches(regex, key)) {
            index = listIndex(key,START_LIST,END_LIST)
            key = sanifyKey(key,START_LIST)
            item = itemFromList(key, item, index as int)
            (key,paths) = processKey(paths)
            return [key,paths,item]
        } else {
            return [key,paths,item]
        }
    }

    private static def processFunction(String key, List paths, def item){
        String regex = PathWalker.REGEX_REGEX
        def index = -1

        if (key && Pattern.matches(regex, key)) {
            index = listIndex(key,START_FUNC,END_FUNC)
            key = sanifyKey(key,START_FUNC)
            item = runFunction(key,index,item)
            (key,paths) = processKey(paths)
            return [key,paths,item]
        } else {
            return [key,paths,item]
        }
    }

    private static def runFunction(String key,def index, def item){
        switch (key){
            case 'size':
                item = Functions.size(item)
                break
            case 'pick':
                item = Functions.pick(item)
                break
            case 'values':
                item = Functions.values(item)
                break
            case 'keySet':
                item = Functions.keySet(item)
                break
            defaul: break;
        }
        return item.toString()
    }


    private static def processVariable(String key, List paths, def item, def scope){
        String regex = com.apifortress.groovypathwalker.PathWalker.REGEX_VAR
        def index

        if (key && Pattern.matches(regex, key)) {
            key = sanifyKey(key,START_VAR,END_VAR)
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
        index = key.substring(key.indexOf(s) + 1, key.indexOf(e))
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
        path = path.replaceAll(SANIFY_PATH_DOUBLE_QUOTES, '.$1')
        path = path.replaceAll(SANIFY_PATH_SINGLE_QUOTES, '.$1')
        path = path.replaceAll(SANIFY_PATH_VARIABLE, '.\\$$1\\$')
        path = path.replaceAll(SANIFY_PATH_QUESTIONE_MARK, '')
        return path
    }
}

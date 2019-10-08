package com.apifortress.groovypathwalker

import groovy.transform.CompileStatic
import com.apifortress.groovypathwalker.utils.impl.Functions

import java.util.regex.Pattern

/**
 * © 2019 API Fortress
 * @author Diego Brach
 * Walks in depth trought a groovy path
 */
class GroovyPathWalker {

    /**
     * Starts the walk trought the groovy path
     * @param item null or starting point
     * @param path path to walk trought
     * @param scope
     * @return the walk result
     */
    public static def walk(def item,def path, def scope = null){
        //normalize input path. we need path in the form a.b.c.d
        //every accessor will be normalized in a.b.c.d form
        def sanifiedPath = GroovyPathWalker.normalizePath(path)
        //splits the normalized path in a liste with every single part of the pat
        List paths = GroovyPathWalker.processPath(sanifiedPath)
        //walks the path list
        return processWalk(item, paths, scope)
    }

    /**
     * Walks the groovy path
     * @param item
     * @param paths
     * @param scope
     * @return
     */
    private static def processWalk(def item, def paths, def scope = null){
        def element,pathElement

        //if starting with null item, then the starting point is the scope itself
        if (!item)
            item = scope

        //if i have no more path items,then item is the result of the walk
        if (paths.size() <= 0)
            element =item
        else {
            //get path Element, removing it from path element list
            (pathElement, paths) = processPathElement(paths)
            //if pathElement is a list, process the list getting proper element from list and index
            (pathElement, paths, item) = processList(pathElement, paths, item)
            //if pathElement is a variable of scope, process the element to get the value of the variable
            (pathElement, paths, item) = processVariable(pathElement, paths, item,scope)
            //if pathElement is a supported function processs the function
            (pathElement, paths, item) = processFunction(pathElement, paths, item)
            //walk trought the path
            element = process(pathElement, paths, item, scope)
        }

        return element
    }

    /**
     * Process simple path element
     * @param pathElement
     * @param paths
     * @param item
     * @param scope
     * @return
     */
    private static def process(String pathElement, List paths,def item, def scope) {
        def element = item

        if (item instanceof Map && pathElement != null
            || item instanceof String && pathElement != null)
        {
            try {
                element = processWalk(item.get(pathElement), paths, scope)
            } catch (Exception ex) {
                element = "Exception: " + ex.toString()
            }
        }

        return element
    }

    /**
     * Process list path element
     * @param pathElement
     * @param paths
     * @param item
     * @return
     */
    private static def processList(String pathElement, List paths, def item){
        //if path element matches a list regex
        if (Pattern.matches(REGEX_LIST, pathElement)) {
            // get index of list
            def index = processIndex(pathElement,START_LIST,END_LIST)
            // get key part of path element
            pathElement = normalizePathElement(pathElement,START_LIST)
            // get item from the path element
            item = itemFromList(pathElement, item, index as int)
            // get new path element and advance in the walk
            (pathElement,paths) = processPathElement(paths)
            return [pathElement,paths,item]
        } else {
            return [pathElement,paths,item]
        }
    }

    /**
     * Process variable path element
     * @param pathElement
     * @param paths
     * @param item
     * @param scope
     * @return
     */
    private static def processVariable(String pathElement, List paths, def item, def scope){
        //if pathElement is not null and matches variable regex
        if (pathElement && Pattern.matches(REGEX_VAR, pathElement)) {
            // get key part of path element
            pathElement = normalizePathElement(pathElement,START_VAR,END_VAR)
            // retrieve the value from scope
            def scopeValue = scope.get(pathElement)
            item = item.get(scopeValue)
            // get new path element and advance in the walk
            (pathElement,paths) = processPathElement(paths)
            return [pathElement,paths,item]
        } else {
            return [pathElement,paths,item]
        }
    }

    /**
     * process function path element
     * @param pathElement
     * @param paths
     * @param item
     * @return
     */
    private static def processFunction(String pathElement, List paths, def item){
        //if pathElement is not null and matches function regex
        if (pathElement && Pattern.matches(REGEX_FUNC, pathElement)) {
            // get function argument element if exist
            def argument = processIndex(pathElement,START_FUNC,END_FUNC)
            // get key part of path element
            pathElement = normalizePathElement(pathElement,START_FUNC)
            //run the function
            item = runFunction(pathElement,argument,item)
            // get new path element and advance in the walk
            (pathElement,paths) = processPathElement(paths)
            return [pathElement,paths,item]
        } else {
            return [pathElement,paths,item]
        }
    }

    /**
     * Run a function
     * @param function
     * @param index
     * @param item
     * @return
     */
    private static def runFunction(String function,def index, def item){
        switch (function){
            case 'size':
                item = Functions.size(item)
                break
            case 'pick':
                if (index)
                    item = Functions.pick(item,index as int)
                else
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

    /**
     * Gets a item from a list given an index
     * @param pathElement
     * @param item
     * @param index
     * @return
     */
    private static def itemFromList(String pathElement,def item,int index) {
        if (pathElement != '')
            item = item.get(pathElement)
        item = item[index]
        return item
    }


    @CompileStatic
    private static def normalizePathElement(String pathElement, String c) {
        return pathElement.substring(0, pathElement.indexOf(c))
    }

    @CompileStatic
    private static def normalizePathElement(String pathElement, String s, String e) {
        pathElement = pathElement.substring(pathElement.indexOf(s) + 1)
        pathElement = pathElement.substring(0,pathElement.indexOf(e))
        return pathElement
    }

    @CompileStatic
    private static def processIndex(String pathElement, String s, String e) {
        def index
        index = pathElement.substring(pathElement.indexOf(s) + 1, pathElement.indexOf(e))
        return index
    }

    @CompileStatic
    private static def processPathElement(List paths) {
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
    public static String normalizePath(String path) {
        path = path.replaceAll(SANIFY_PATH_DOUBLE_QUOTES, '.$1')
        path = path.replaceAll(SANIFY_PATH_SINGLE_QUOTES, '.$1')
        path = path.replaceAll(SANIFY_PATH_VARIABLE, '.\\$$1\\$')
        path = path.replaceAll(SANIFY_PATH_QUESTIONE_MARK, '')
        return path
    }

    public static boolean  isSupported(String path){
        boolean supported = true
        def sanifiedPath = GroovyPathWalker.normalizePath(path)
        List paths = GroovyPathWalker.processPath(sanifiedPath)

        paths.each {
            supported = supported && !Pattern.matches(REGEX_UNSUPPORTED_BRACES, it)

            if (Pattern.matches(REGEX_FUNC, it)){
                def func = normalizePathElement(it,START_FUNC)
                supported = supported && func in ['size','pick','values','keySet']
            }

            supported = supported && !Pattern.matches(REGEX_UNSUPPORTED_STARTS, it)
            supported = supported && !Pattern.matches(REGEX_UNSUPPORTED_OPERATOR, it)
        }

        return supported
    }

    private static final String REGEX_FUNC = "\\w*\\(\\d?\\)"
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

    private static final String REGEX_UNSUPPORTED_BRACES = "\\{.*?\\}"
    private static final String REGEX_UNSUPPORTED_STARTS = "\\*"
    private static final String REGEX_UNSUPPORTED_OPERATOR = ".*?\\->.*?"

}

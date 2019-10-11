package com.apifortress.groovypathwalker

import groovy.transform.CompileStatic
import com.apifortress.groovypathwalker.utils.Functions

import java.lang.reflect.Field
import java.lang.reflect.Method

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
        def normalizedPath = GroovyPathWalker.normalizePath(path)
        //splits the normalized path in a liste with every single part of the pat
        List paths = GroovyPathWalker.processPath(normalizedPath)
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
        def result,pathElement

        //if starting with null item, then the starting point is the scope itself
        if (!item)
            item = scope

        //if i have no more path items,then item is the result of the walk
        if (paths.size() <= 0)
            result = item
        else {
            //get path Element, removing it from path element list
            (pathElement, paths, item) = processInit(paths,item,scope)
            result = processPathElement(pathElement, paths, item, scope)
        }

        return result
    }

    /**
     * Process simple path element
     * @param pathElement
     * @param paths
     * @param item
     * @param scope
     * @return
     */
    private static def processPathElement(String pathElement, List paths,def item, def scope) {
        def result = item

        if (item instanceof Map && pathElement != null
            || item instanceof String && pathElement != null)
        {
            try {
                result = processWalk(item.get(pathElement), paths, scope)
            } catch (Exception ex) {
                //result = "Exception: " + ex.toString()
                result = byReflection(item, pathElement, result)
            }
        } else
        if (!(item instanceof List) &&
                   !(item instanceof Map) &&
                   !(item instanceof String) &&
                    (item instanceof Object)){
            result = byReflection(item, pathElement, result)
        }

        return result
    }

    private static Object byReflection(item, String pathElement, result) {
        Field[] fields = item.getClass().getFields()
        String[] fieldsNames = new String[fields.length];
        for (int i = 0; i < fieldsNames.length; i++) {
            fieldsNames[i] = fields[i].getName();
        }
        if (pathElement in fieldsNames) {
            Field field = item.getClass().getField(pathElement)
            result = field.get(item)
        } else {
            try {
                Method method = null;
                method = item.getClass().getMethod("get" + pathElement.capitalize(), null);
                result = (String) method.invoke(item, new Object[0]);
            }
            catch (NoSuchMethodException exc) {
                result = "Exception: " + exc.toString()
            }
        }
        result
    }

    /**
     * getting next path element while advancing in the list of paths
     * @param paths
     * @return
     */
    @CompileStatic
    private static List<Object> processInit(List paths, def item, Map scope) {
        String pathElement
        //if i have still paths then i remove the first one and return him as the current path element to be analized
        if (paths.size() > 0) {
            pathElement = paths.remove(0)

            //if pathElement is a list, process the list getting proper element from list and index
            if (pathElement.matches(Regex.REGEX_LIST)) {
                List<Object> result = processList(pathElement, item, paths)
                pathElement = result[0]
                item = result[1]
                paths = (List) result[2]
            }

            //if pathElement is a variable of scope, process the element to get the value of the variable
            if (pathElement != null && pathElement.matches(Regex.REGEX_VAR)){
                List<Object> result = processVariable(pathElement, scope, item, paths)
                pathElement = result[0]
                item = result[1]
                paths = (List) result[2]
            }

            //if pathElement is a supported function processs the function

            if (pathElement != null && pathElement.matches(Regex.REGEX_FUNC)){
                List<Object> result = processFunction(pathElement, item, paths)
                pathElement = result[0]
                item = result[1]
                paths = (List) result[2]
            }

        } else
            pathElement == null

        return Arrays.asList(pathElement,paths,item)
        //return [pathElement,paths]
    }

    @CompileStatic
    private static List processFunction(String pathElement, item, List paths) {
        // get function argument element if exist
        def argument = processIndex(pathElement, Regex.START_FUNC, Regex.END_FUNC)
        // get key part of path element
        pathElement = normalizePathElement(pathElement, Regex.START_FUNC)
        //run the function
        item = runFunction(pathElement, argument, item)
        // get new path element and advance in the walk
        if (paths.size() > 0) pathElement = paths.remove(0) else pathElement = null
        return Arrays.asList(pathElement, item, paths)
    }

    @CompileStatic
    private static List processVariable(String pathElement, Map scope, item, List paths) {
        // get key part of path element
        pathElement = normalizePathElement(pathElement, Regex.START_VAR, Regex.END_VAR)
        // retrieve the value from scope
        def scopeValue = scope.get(pathElement)
        //def element = null
        if (item instanceof Map)
            item = item.get(scopeValue)
        // get new path element and advance in the walk
        if (paths.size() > 0) pathElement = paths.remove(0) else pathElement = null
        return Arrays.asList(pathElement, item, paths)
    }

    @CompileStatic
    private static List processList(String pathElement, item, List paths) {
        // get index of list
        def index = processIndex(pathElement, Regex.START_LIST, Regex.END_LIST)
        // get key part of path element
        pathElement = normalizePathElement(pathElement, Regex.START_LIST)
        // get item from the path element
        item = itemFromList(pathElement, item, index as int)
        // get new path element and advance in the walk
        if (paths.size() > 0) pathElement = paths.remove(0) else pathElement = null
        return Arrays.asList(pathElement, item, paths)
    }
    /**
     * Gets a item from a list given an index
     * @param pathElement
     * @param item
     * @param index
     * @return
     */
    @CompileStatic
    private static def itemFromList(String pathElement,def item,int index) {
        if (pathElement != '' && item instanceof Map)
            item = item.get(pathElement)
        if (item instanceof List)
            item = ((List) item)[index]
        return item
    }

    /**
     * Run a function
     * @param function
     * @param index
     * @param item
     * @return
     */
    @CompileStatic
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
     * Getting key part of a pathElement
     * @param pathElement
     * @param c
     * @return
     */
    @CompileStatic
    private static def normalizePathElement(String pathElement, String c) {
        return pathElement.substring(0, pathElement.indexOf(c))
    }

    /**
     * Getting key part of a pathElement
     * @param pathElement
     * @param s
     * @param e
     * @return
     */
    @CompileStatic
    private static def normalizePathElement(String pathElement, String s, String e) {
        pathElement = pathElement.substring(pathElement.indexOf(s) + 1)
        pathElement = pathElement.substring(0,pathElement.indexOf(e))
        return pathElement
    }

    /**
     * getting the index. can be used to get the index of a list of a argument of a function
     * @param pathElement
     * @param s
     * @param e
     * @return
     */
    @CompileStatic
    private static def processIndex(String pathElement, String s, String e) {
        return pathElement.substring(pathElement.indexOf(s) + 1, pathElement.indexOf(e))
    }

    /**
     * Return the list of paths
     * @param path
     * @return
     */
    @CompileStatic
    public static List processPath(String path) {
        List paths = path.split('\\.').toList()
        return paths
    }

    /**
     * Returns a normalized path.
     * @param path
     * @return
     */
    @CompileStatic
    public static String normalizePath(String path) {
        //replacing double quotes with .pathBeetweenDoubleQuotes
        path = path.replaceAll(Regex.NORMALIZED_PATH_DOUBLE_QUOTES, '.$1')
        //replacing single quotes with .pathBeetweenSingleQuotes
        path = path.replaceAll(Regex.NORMALIZED_PATH_SINGLE_QUOTES, '.$1')
        //replacing variable with .pathBeetweenSquareBrackets
        path = path.replaceAll(Regex.NORMALIZED_PATH_VARIABLE, '.\\$$1\\$')
        //removing quesion mark
        path = path.replaceAll(Regex.NORMALIZED_PATH_QUESTIONE_MARK, '')
        return path
    }

    /**
     * Returns if a path is supported or not
     * @param path
     * @return
     */
    @CompileStatic
    public static boolean  isSupported(String path){
        boolean supported = true
        def normalizedPath = GroovyPathWalker.normalizePath(path)
        List paths = GroovyPathWalker.processPath(normalizedPath)

        paths.each {
            String pathElement = it
            supported = supported && !pathElement.matches(Regex.REGEX_UNSUPPORTED_BRACES)
            supported = supported && !pathElement.matches(Regex.REGEX_UNSUPPORTED_STARTS)
            supported = supported && !pathElement.matches(Regex.REGEX_UNSUPPORTED_OPERATOR)
            supported = supported && !pathElement.matches(Regex.REGEX_UNSUPPORTED_EXCLAMATION_MARK)
            supported = supported && !pathElement.matches(Regex.REGEX_UNSUPPORTED_ASSIGNEMENT_OPERATOR)

            if (pathElement.matches(Regex.REGEX_FUNC)){
                def func = normalizePathElement(pathElement,Regex.START_FUNC)
                supported = supported && func in ['size','pick','values','keySet']
            }



        }

        return supported
    }


}

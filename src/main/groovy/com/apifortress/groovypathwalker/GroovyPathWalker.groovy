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
        //splits the normalized path in a liste with every single part of the pat
        List paths = GroovyPathWalker.processPath(path)
        if (!item) item = scope
        //walks the path list
        for (def p in paths){
            //if beetween square brackets
            if (p.startsWith('[') && p.endsWith(']'))
                item = processSquared(p, item, scope)
            // if matches function pattern
            else if (p.matches(Regex.REGEX_FUNC))
                item = processFunction(p,item)
            //otherwise plain accessor
            else
                item = processPlain(item, p)
        }

        return item
    }

    /**
     * process plain path element, in other words path elements not between square brackets
     * @param item
     * @param p
     * @return
     */
    private static def processPlain(def item, String p) {
        boolean stop = false
        //if it's map or list get the value. If map the result is guaranteed, if list and support get method then result is guaranteed else exception wil be thrown
        if (item instanceof Map || item instanceof List)
                item = item.get(p)
        // if it's generic object let's try using reflection.
        else if (item instanceof Object)
                item = byReflection(item, p)

        return item
    }

    /**
     * Process complex plain element, in other wors elements between square brackests. a path element between square brackets can be an accessor of a map, an index
     * of a list or a scope variable
     * @param p
     * @param item
     * @param scope
     * @return
     */
    private static def processSquared(def p, item, scope) {
        p = p.substring(p.indexOf('[') + 1, p.indexOf(']'))

        //get value between quotes or double quotes it is always an accessor
        if (p.startsWith('\'') && p.endsWith('\'') || p.startsWith('"') && p.endsWith('"'))
            p = p.substring(1, p.length() - 1)
        else {
            //if not between quotes then it is a variable or a number
            if (p.isNumber()) {
                p = p as int
            } else {
                //if i have a scope let's try to recover the value from the scope, if note present in the scope the value is the path element itself
                def pScope
                if (scope) pScope = scope.get(p)
                if (pScope) p = pScope
            }
        }

        //if item it's map then get the element
        if (item instanceof Map || item instanceof List) {
            try {
                item = item.get(p)
            } catch (Exception e) {
                item = e.getMessage()
            }
        }
        return item
    }

    /**
     * Process a supported function
     * @param p
     * @param item
     * @return
     */
    @CompileStatic
    private static def processFunction(String p, item) {
        // get function argument element if exist
        def argument = p.substring(p.indexOf('(') + 1, p.indexOf(')'))
        // get key part of path element
        p = p.substring(0, p.indexOf('('))
        //run the function
        item = runFunction(p, argument, item)
        // get new path element and advance in the walk
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
     * Returns if a path is supported or not
     * @param path
     * @return
     */
    @CompileStatic
    public static boolean  isSupported(String path){
        boolean supported = true
        //def normalizedPath = GroovyPathWalkerEvo.normalizePath(path)
        List paths = GroovyPathWalker.processPath(path)

        for (String p in paths){
            supported = supported && !p.matches(Regex.REGEX_UNSUPPORTED_BRACES)
            supported = supported && !p.matches(Regex.REGEX_UNSUPPORTED_STARTS)
            supported = supported && !p.matches(Regex.REGEX_UNSUPPORTED_OPERATOR)
            supported = supported && !p.matches(Regex.REGEX_UNSUPPORTED_EXCLAMATION_MARK)
            supported = supported && !p.matches(Regex.REGEX_UNSUPPORTED_ASSIGNEMENT_OPERATOR)

            if (p.matches(Regex.REGEX_FUNC)){
                def func = p = p.substring(0, p.indexOf('(')) //normalizePathElement(pathElement,Regex.START_FUNC)
                supported = supported && func in ['size','pick','values','keySet']
            }
        }

        return supported
    }

    /**
     * Return the list of paths
     * @param path
     * @return
     */
    @CompileStatic
    public static List processPath(String path) {
        path = path.replaceAll('\\[','.[')
        path = path.replaceAll('\\?', '')
        if (path.startsWith('.')) path = path.substring(1)
        List paths = path.split('\\.').toList()
        return paths
    }

    /**
     * Tries to revocer property or method oof an object by reflection
     * @param item
     * @param pathElement
     * @param result
     * @return
     */
    //@CompileStatic
    private static Object byReflection(Object item, String p) {
        def result = null
        //retieves properties
        Field[] fields = item.getClass().getFields()
        String[] fieldsNames = fields.collect{it.getName()}
        //retrieves methos
        Method[] methods = item.getClass().getDeclaredMethods()
        String[] methodsNames = methods.collect{it.getName()}

        //check if the propery with p name exist
        if (p in fieldsNames) {
            Field field = item.getClass().getField(p)
            result = field.get(item)
        }

        //check if the method with p name exist
        if ("get" + p.capitalize() in methodsNames){
            Method method = item.getClass().getMethod("get" + p.capitalize(), null);
            result = method.invoke(item, new Object[0]);

        }
        return result
    }
}

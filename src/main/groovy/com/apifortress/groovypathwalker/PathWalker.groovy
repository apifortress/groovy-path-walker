package com.apifortress.groovypathwalker

class PathWalker {

    static public def navigate(def item,def paths){

        if (paths.size() <= 0)
            return item

        def element,key,index
        (key,paths) = currentKey(paths)

        if (key?.contains('[') && key?.contains(']')) {
            (key, index) = sanifyKeyListIndex(key)
            item = itemFromList(key, item, index)
            (key,paths) = currentKey(paths)
        }

        if (item instanceof Map && key != null) {
            element = navigate(item.get(key), paths)
        } else if (item instanceof String && key != null) {
            try {
                element = navigate(item.get(key), paths)
            } catch (Exception ex) {
                element = ex.message
            }
        } else
            element = item

        return element
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

    private static String sanifyPath(String path) {
        path = path.replaceAll("\\[\"(.*?)\"\\]", '.$1')
        path = path.replaceAll("\\[\'(.*?)\'\\]", '.$1')
        path
    }
}

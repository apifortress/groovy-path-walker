package com.apifortress.groovypathwalker

class PathWalker {
    static public def navigate(def item,def paths, def depth=0,def currentDepht=0, def index = 0){

        def element

        if (currentDepht > depth)
            return item
        else
            currentDepht +=1

        def key, skip
        (item, key, index, skip) = keyIndexItemSkipNull(paths, currentDepht, item, index)

        if (item instanceof Map && key != null) {
            //if (skip == true && item.get(key) == null)
                element = navigate(item.get(key), paths, depth, currentDepht, index)
            //else
             //   element = item
        } else
            element = item

        return element
    }

    private static List keyIndexItemSkipNull(paths, currentDepht, item, int index) {
        boolean skip = false
        String key = paths[currentDepht - 1]
        if (key?.contains('[') && key?.contains(']')) {
            index = key.substring(key.indexOf('[') + 1, key.indexOf(']')) as int
            key = key.substring(0, key.indexOf('['))
            if (key != '')
                item = item.get(key)
            item = item[index]
            key = paths[currentDepht]
        }
        if (key?.contains('?'))
            skip = true
        [item, key, index,skip]
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

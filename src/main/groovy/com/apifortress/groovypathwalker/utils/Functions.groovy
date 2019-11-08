package com.apifortress.groovypathwalker.utils

class Functions {
    public static def size(def item) {
        return item.size()
    }

    public static def pick(def item,def index = null) {
        if (index)
            return item.pick(index)
        else
            return item.pick()
    }

    public static def values(def item) {
        return item.values()
    }

    public static def keySet(def item) {
        return item.keySet()
    }
}

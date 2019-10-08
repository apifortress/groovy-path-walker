package com.apifortress.groovypathwalker.utils.impl

class Functions {
    public static String size(def item) {
        return item.size() as String
    }

    public static String pick(def item,def index = null) {
        if (index)
            return item.pick(index)
        else
            return item.pick()
    }

    public static String values(def item) {
        return item.values() as String
    }

    public static String keySet(def item) {
        return item.keySet() as String
    }
}

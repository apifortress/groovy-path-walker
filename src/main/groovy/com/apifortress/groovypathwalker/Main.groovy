package com.apifortress.groovypathwalker

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.regex.*

class Main {
    public static void main(String[] args) {
        String stringa = ""
        Field[] campi = stringa.getClass().getFields()
        campi.each { f->
            println f.getName().toString()
        }

        String namToUpperCase = "Bytes"
        String ritorno = new String();
        Method metodo = null;
        try {
            metodo = stringa.getClass().getMethod("get" + namToUpperCase, null);
        }
        catch (NoSuchMethodException exc) { }
        if (metodo != null)
            try {
                ritorno = (String) metodo.invoke(stringa, new Object[0]);
            }
            catch (Exception ecc) { }
        println ritorno;

        println stringa.getBytes()
        println stringa.CASE_INSENSITIVE_ORDER


    }
}


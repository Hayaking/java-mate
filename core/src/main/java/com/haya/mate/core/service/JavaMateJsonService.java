package com.haya.mate.core.service;

import com.haya.mate.core.spi.JavaMateJsonHandler;

import java.util.ServiceLoader;

public class JavaMateJsonService {
    private static  JavaMateJsonHandler handler = null;
    static {
        ServiceLoader<JavaMateJsonHandler> loader = ServiceLoader.load(JavaMateJsonHandler.class);
        for (JavaMateJsonHandler item : loader) {
            handler = item;
            break;
        }
    }


    public static String toJson(Object obj) {
        return handler.toJson(obj);
    }
}

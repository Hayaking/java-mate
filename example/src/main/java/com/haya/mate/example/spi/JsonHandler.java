package com.haya.mate.example.spi;

import com.alibaba.fastjson.JSON;
import com.google.auto.service.AutoService;
import com.haya.mate.core.spi.JavaMateJsonHandler;

@AutoService(JavaMateJsonHandler.class)
public class JsonHandler implements JavaMateJsonHandler {

    public  String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }
}

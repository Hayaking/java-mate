# java-mate

# Annotation Processor 
根据注解信息，编译期修改字节码

## @ToString()

生成返回Json字符串的toString方法

需要自行使用SPI适配暴露出来的接口，参考example里的spi下的JsonHandler代码:
```java

package com.haya.mate.example.spi;

import com.alibaba.fastjson.JSON;
import com.google.auto.service.AutoService;
import com.haya.mate.core.spi.JavaMateJsonHandler;

@AutoService(JavaMateJsonHandler.class)
public class JsonHandler implements JavaMateJsonHandler {

    public  String toJson(Object obj) {
        // 这里使用的fastjson
        return JSON.toJSONString(obj);
    }
}

```

生成出来的toString方法
```java
public String toString() {
    return JavaMateJsonService.toJson(this);
}
```

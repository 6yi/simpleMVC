# SimpleMVC

### 介绍
一个简易的Java MVC框架


#### 启动流程
![启动图](https://images.gitee.com/uploads/images/2020/0925/155930_d945c353_5343539.png "屏幕截图.png")



### 基本使用

#### 添加配置文件application.properties,指定配置类


```
configuration=cn.lzheng.simpleMVC.config.SimpleMvcConfiguration
```

#### 编写java配置类

```
package cn.lzheng.simpleMVC.config;

import cn.lzheng.simpleMVC.annotation.Configuration;
import cn.lzheng.simpleMVC.annotation.JsonProcess;
import cn.lzheng.simpleMVC.jsonProcess.FastJsonProcessHandler;
import cn.lzheng.simpleMVC.jsonProcess.JacksonProcessHandler;
import cn.lzheng.simpleMVC.jsonProcess.JsonProcessHandler;

@Configuration(controllerSrc = "cn.lzheng.simpleMVC.controller")
public class SimpleMvcConfiguration {
    
    @JsonProcess
    public JsonProcessHandler jsonProcess(){
        return new JacksonProcessHandler();
    }
    
}

```
@Configuration表明是配置类,controllerSrc为控制器的包路径  
@JsonProcess配置自定义json解析器,需要实现JsonProcessHandler接口,默认可以使用jackson和fastjson,如果不配置将使用jackson作为json解析器  

#### 编写控制器

```
@Controller
public class HelloController {

    @Router(url = "/hi")
    @ResponseBody
    public String hi(@PathVariable("key1")Integer k1, @PathVariable("key2")String k2,HttpServletRequest request) throws IOException {
        return ""+k1;
    }
}

```
@Controller注解表示该类为控制器  
@Router是处理方法,参数url表示请求路径,method表示请求方法,默认为get  
@ResponseBody表示解析成json字符串返回  
@PathVariable是路径参数,参数为参数名  




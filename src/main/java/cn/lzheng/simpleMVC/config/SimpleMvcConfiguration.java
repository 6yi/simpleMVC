package cn.lzheng.simpleMVC.config;

import cn.lzheng.simpleMVC.annotation.Configuration;
import cn.lzheng.simpleMVC.annotation.JsonProcess;
import cn.lzheng.simpleMVC.jsonProcess.FastJsonProcessHandler;
import cn.lzheng.simpleMVC.jsonProcess.JsonProcessHandler;

/**
 * @ClassName SimpleMvcConfiguartion
 * @Author 刘正
 * @Date 2020/9/25 14:17
 * @Version 1.0
 * @Description:
 */

@Configuration(controllerSrc = "cn.lzheng.simpleMVC.controller")
public class SimpleMvcConfiguration {


    @JsonProcess
    public JsonProcessHandler jsonProcess(){
        return new FastJsonProcessHandler();
    }


}

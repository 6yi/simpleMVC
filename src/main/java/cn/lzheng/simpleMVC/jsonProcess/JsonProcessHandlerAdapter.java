package cn.lzheng.simpleMVC.jsonProcess;

import cn.lzheng.simpleMVC.Utils.ConfigurationLoader;
import cn.lzheng.simpleMVC.Utils.PropertiesLoader;
import cn.lzheng.simpleMVC.annotation.JsonProcess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @ClassName GetJsonProcessHandler
 * @Author 刘正
 * @Date 2020/9/25 10:19
 * @Version 1.0
 * @Description:
 */


public class JsonProcessHandlerAdapter {


    private static JsonProcessHandler ProcessHandler;

    private JsonProcessHandlerAdapter() {
    }

    public static JsonProcessHandler getJsonProcessHandler(){
        if (ProcessHandler==null){
            synchronized (JsonProcessHandlerAdapter.class){
                if(ProcessHandler==null){
                    Class configuration = ConfigurationLoader.getConfiguration();
                    Method[] methods = configuration.getMethods();
                    for (Method method : methods) {
                        if(method.getAnnotation(JsonProcess.class)!=null){
                            try {
                                ProcessHandler=(JsonProcessHandler)method.invoke(configuration.getConstructor().newInstance());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if(ProcessHandler==null){
                        ProcessHandler=new FastJsonProcessHandler();
                    }
                }
            }
        }
        return ProcessHandler;
    }



}

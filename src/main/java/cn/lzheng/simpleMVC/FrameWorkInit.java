package cn.lzheng.simpleMVC;


import cn.lzheng.simpleMVC.Utils.ConfigurationLoader;
import cn.lzheng.simpleMVC.Utils.PropertiesLoader;
import cn.lzheng.simpleMVC.annotation.Configuration;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import java.io.*;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * @ClassName FrameWorkInit
 * @Author 刘正
 * @Date 2020/9/23 23:33
 * @Version 1.0
 * @Description:
 */

@HandlesTypes(WebInitializer.class)
public class FrameWorkInit implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        try {
            Class configuration = ConfigurationLoader.getConfiguration();
            Configuration annotationConfig = (Configuration) configuration.getAnnotation(Configuration.class);

            String controllerPKG = annotationConfig.controllerSrc();


            if(controllerPKG==null){
                throw new Exception(" Controller ScanSrc NotFound");
            }

            ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new RouterServlet(controllerPKG));
            dispatcher.addMapping(annotationConfig.dispatcherUrl());
            if (!set.isEmpty()){
                for (Class clazz:set){
                    try {
                        WebInitializer var1 = (WebInitializer) clazz.getConstructor().newInstance();
                        var1.onStartup(servletContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }  catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }



}

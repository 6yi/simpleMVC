package cn.lzheng.simpleMVC;


import cn.lzheng.simpleMVC.Utils.ConfigurationLoader;

import cn.lzheng.simpleMVC.annotation.Configuration;

import cn.lzheng.simpleMVC.jsonProcess.JsonProcessHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
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

    private static Logger logger = LoggerFactory.getLogger(FrameWorkInit.class);

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        logger.debug("start--------------------------");
        try {
            Class configuration = ConfigurationLoader.getConfiguration();
            Configuration annotationConfig = (Configuration) configuration.getAnnotation(Configuration.class);

            String controllerPKG = annotationConfig.controllerSrc();

            if(controllerPKG==null){
                throw new Exception(" Controller ScanSrc NotFound");
            }

            //dispatcher init
            ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new RouterServlet(controllerPKG));
            dispatcher.addMapping(annotationConfig.dispatcherUrl());

            ServletRegistration.Dynamic staticServlet = servletContext.addServlet("staticServlet", new simpleMvcStaticServlet());
            staticServlet.addMapping("/static/*");

            //Json init
            JsonProcessHandlerAdapter.Init();

            //diy init
            if (set!=null&&!set.isEmpty()){
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

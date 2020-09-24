package cn.lzheng.simpleMVC;


import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import java.io.*;
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
            Properties properties = loadProperties();
            String controllerPKG = properties.getProperty("controllerPKG");
            if(controllerPKG==null){
                throw new Exception(" Controller ScanSrc NotFound");
            }
            ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new RouterServlet(controllerPKG));
            dispatcher.addMapping("/");
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

    public Properties loadProperties() {
        InputStream in=null;
        Properties properties=null;
        try {

            in = new BufferedInputStream(
                    Objects.requireNonNull(FrameWorkInit.class
                            .getClassLoader()
                            .getResourceAsStream("application.properties")));

            properties= new Properties();
            properties.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(in!=null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

}

package cn.lzheng.simpleMVC.Utils;

import cn.lzheng.simpleMVC.FrameWorkInit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @ClassName PropertiesLoader
 * @Author 刘正
 * @Date 2020/9/25 10:25
 * @Version 1.0
 * @Description:
 */


public class PropertiesLoader {

    private static Properties properties;

    public static Properties getProperties(){
        if(properties==null){
            synchronized (PropertiesLoader.class){
                if(properties==null){
                    properties=loadProperties();
                }
            }
        }
        return properties;
    }


    private static Properties loadProperties() {
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

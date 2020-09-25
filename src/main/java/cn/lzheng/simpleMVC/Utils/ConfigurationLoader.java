package cn.lzheng.simpleMVC.Utils;

import java.util.Properties;

/**
 * @ClassName ConfigurationLoader
 * @Author 刘正
 * @Date 2020/9/25 14:31
 * @Version 1.0
 * @Description:
 */


public class ConfigurationLoader {

    private static Class configurationClazz;

    public static Class getConfiguration(){
        if(configurationClazz==null){
            synchronized (ConfigurationLoader.class){
                if(configurationClazz==null){
                    Properties properties = PropertiesLoader.getProperties();
                    String configuration = properties.getProperty("configuration");
                    try {
                        Class<?> Class = ConfigurationLoader.class.getClassLoader().loadClass(configuration);
                        configurationClazz=Class;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return configurationClazz;
    }

    private ConfigurationLoader() {
    }
}

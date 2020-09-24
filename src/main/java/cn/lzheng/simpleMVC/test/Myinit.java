package cn.lzheng.simpleMVC.test;

import cn.lzheng.simpleMVC.WebInitializer;

import javax.servlet.ServletContext;

/**
 * @ClassName Myinit
 * @Author 刘正
 * @Date 2020/9/24 0:30
 * @Version 1.0
 * @Description:
 */


public class Myinit implements WebInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        System.out.println("自定义噢");
    }
}

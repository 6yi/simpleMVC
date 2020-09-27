package cn.lzheng.simpleMVC;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @ClassName WebInitializer
 * @Author 刘正
 * @Date 2020/9/24 0:17
 * @Version 1.0
 * @Description:
 */

public interface WebInitializer {

    void onStartup(ServletContext servletContext);



}

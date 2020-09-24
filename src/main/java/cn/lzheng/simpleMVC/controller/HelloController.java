package cn.lzheng.simpleMVC.controller;



import cn.lzheng.simpleMVC.annotation.Controller;
import cn.lzheng.simpleMVC.annotation.PathVariable;
import cn.lzheng.simpleMVC.annotation.ResponseBody;
import cn.lzheng.simpleMVC.annotation.Router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName HelloController
 * @Author 刘正
 * @Date 2020/9/14 18:15
 * @Version 1.0
 * @Description:
 */

@Controller
public class HelloController {



    @Router(url = "/hi")
    @ResponseBody
    public String hi(@PathVariable("key1")String k1, @PathVariable("key2")String k2,HttpServletRequest request, HttpServletResponse response) throws IOException {
        return "haha";
    }


}

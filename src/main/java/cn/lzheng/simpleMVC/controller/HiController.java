package cn.lzheng.simpleMVC.controller;

import cn.lzheng.simpleMVC.annotation.Controller;
import cn.lzheng.simpleMVC.annotation.Router;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HiController
 * @Author 刘正
 * @Date 2020/9/15 0:27
 * @Version 1.0
 * @Description:
 */

@Controller
public class HiController {


    @Router(url = "/haha")
    public String haha(HttpServletRequest request, HttpServletResponse response){
        return "index.jsp";
    }


}

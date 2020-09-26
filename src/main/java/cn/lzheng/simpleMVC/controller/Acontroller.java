package cn.lzheng.simpleMVC.controller;

import cn.lzheng.simpleMVC.annotation.*;
import cn.lzheng.simpleMVC.test.account;

/**
 * @ClassName Acontroller
 * @Author 刘正
 * @Date 2020/9/25 21:27
 * @Version 1.0
 * @Description:
 */

@Controller
public class Acontroller {


    @Router(url = "/A")
    @ResponseBody
    public account test(@JsonVar account account){
        System.out.println(account);
        return account;
    }

}

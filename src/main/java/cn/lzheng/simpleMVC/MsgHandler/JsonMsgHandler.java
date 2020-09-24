package cn.lzheng.simpleMVC.MsgHandler;

import cn.lzheng.simpleMVC.BaseController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName JsonMesgHandler
 * @Author 刘正
 * @Date 2020/9/24 18:35
 * @Version 1.0
 * @Description:
 */


public class JsonMsgHandler implements BaseMsgHandler {

    @Override
    public List process(BaseController baseController, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

}

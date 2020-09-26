package cn.lzheng.simpleMVC.msgHandler;

import cn.lzheng.simpleMVC.BaseController;
import cn.lzheng.simpleMVC.annotation.JsonVar;
import cn.lzheng.simpleMVC.jsonProcess.JsonProcessHandler;
import cn.lzheng.simpleMVC.jsonProcess.JsonProcessHandlerAdapter;
import cn.lzheng.simpleMVC.mvcException.ParamsException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
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
    public List process(BaseController baseController, HttpServletRequest request, HttpServletResponse response) throws ParamsException {
        BufferedReader streamReader = null;
        JsonProcessHandler jsonProcessHandler = JsonProcessHandlerAdapter.getJsonProcessHandler();
        List<Object> args=new ArrayList<>();
        try {
            streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            Parameter[] parameters = baseController.getMethod().getParameters();

            for (Parameter parameter : parameters){
                JsonVar annotation = parameter.getAnnotation(JsonVar.class);
                if(annotation!=null){
                    args.add(jsonProcessHandler.toJavaObject(responseStrBuilder.toString(),parameter.getType()));
                }else{
                    Type type = parameter.getType();
                    if (type.getTypeName().equals(HttpServletRequest.class.getTypeName())){
                        args.add(request);
                    }
                    if(type.getTypeName().equals(HttpServletResponse.class.getTypeName())){
                        args.add(response);
                    }
                }
            }
        }catch (Exception e){
            throw new ParamsException();
        }

        return args;
    }

}

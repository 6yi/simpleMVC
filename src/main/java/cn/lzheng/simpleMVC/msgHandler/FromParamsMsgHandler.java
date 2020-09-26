package cn.lzheng.simpleMVC.msgHandler;

import cn.lzheng.simpleMVC.BaseController;
import cn.lzheng.simpleMVC.annotation.FromParams;
import cn.lzheng.simpleMVC.mvcException.ParamsException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName FromParamsMsgHandler
 * @Author 刘正
 * @Date 2020/9/25 21:46
 * @Version 1.0
 * @Description:
 */


public class FromParamsMsgHandler implements BaseMsgHandler {

    @Override
    public List process(BaseController baseController, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try{
            Method method = baseController.getMethod();
            Parameter[] parameters = method.getParameters();

            ArrayList<Object> args = new ArrayList<>();

            for (Parameter parameter : parameters) {
                FromParams annotation = parameter.getAnnotation(FromParams.class);
                if(annotation!=null){
                    String var1=request.getParameter(annotation.value());
                    if (var1!=null){
                        Type type = parameter.getType();
                        if(type.getTypeName().equals(Integer.class.getTypeName())){
                            args.add(Integer.parseInt(var1));
                        }else if(type.getTypeName().equals(Double.class.getTypeName())){
                            args.add(Double.parseDouble(var1));
                        }else{
                            args.add(var1);
                        }
                    }
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
            return args;
        } catch (Exception e) {
            throw new ParamsException();
        }
    }

}

package cn.lzheng.simpleMVC.MsgHandler;

import cn.lzheng.simpleMVC.BaseController;
import cn.lzheng.simpleMVC.MvcException.ParamsException;
import cn.lzheng.simpleMVC.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PathVariableMesgHandler
 * @Author 刘正
 * @Date 2020/9/24 18:43
 * @Version 1.0
 * @Description:
 */


public class PathVariableMsgHandler implements BaseMsgHandler{


    @Override
    public List<Object> process(BaseController baseController, HttpServletRequest request, HttpServletResponse response) throws ParamsException {
        try {
            Method method = baseController.getMethod();
            Map<String, String[]> parameterMap = request.getParameterMap();
            ArrayList<Object> args = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                PathVariable annotation = parameter.getAnnotation(PathVariable.class);
                if(annotation!=null){
                    if (parameterMap.containsKey(annotation.value())){
                            Type type = parameter.getType();
                            if(type.getTypeName().equals(Integer.class.getTypeName())){
                                args.add(Integer.parseInt(parameterMap.get(annotation.value())[0]));
                            }else if(type.getTypeName().equals(Double.class.getTypeName())){
                                args.add(Double.parseDouble(parameterMap.get(annotation.value())[0]));
                            }else{
                                args.add(parameterMap.get(annotation.value())[0]);
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

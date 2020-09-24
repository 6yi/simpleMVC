package cn.lzheng.simpleMVC;


import cn.lzheng.simpleMVC.MsgHandler.PathVariableMsgHandler;
import cn.lzheng.simpleMVC.annotation.*;
import com.alibaba.fastjson.JSON;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @ClassName RouterServlet
 * @Author 刘正
 * @Date 2020/9/14 17:59
 * @Version 1.0
 * @Description:
 */

public class RouterServlet extends HttpServlet {

    private String scanSrc;

    public RouterServlet(String scanSrc) {
        this.scanSrc = scanSrc;
    }

    private static HashMap<String,BaseController> routerMap;

    private ThreadLocal<HashMap<String,String>> requestParams=new ThreadLocal<>();

    @Override
    public void init() throws ServletException {
        List<Class<?>> classes = ClassScanner.getClasses(this.scanSrc);
        routerMap=new HashMap<>();
        classes.forEach(clazz->{
            if(clazz.getAnnotation(Controller.class)!=null){
                Method[] methods = clazz.getMethods();
                Arrays.stream(methods).forEach(method -> {
                    Router annotation = method.getAnnotation(Router.class);
                    if (annotation!=null){
                        BaseController baseController = new BaseController();
                        baseController.setRequestMethod(annotation.method());
                        addParams(baseController,method);
//                        baseController.getPathVariable().forEach(System.out::println);
//                        baseController.getFromParams().forEach(System.out::println);

                        if(method.getAnnotation(ResponseBody.class)!=null){
                            baseController.setReturnView(false);
                        }

                        try{
                            baseController.setObject(clazz.getConstructor().newInstance());
                            baseController.setMethod(method);
                            routerMap.put(annotation.url(),baseController);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    public void addParams(BaseController controller,Method method){
        Parameter[] parameters = method.getParameters();
        if(parameters!=null){
            ArrayList<String> pathVariables = new ArrayList<>();
            ArrayList<String> fromParams = new ArrayList<>();
            for (int i = 0; i < parameters.length; i++) {
                PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
                FromParams fromParam = parameters[i].getAnnotation(FromParams.class);
                if(pathVariable!=null){
                    pathVariables.add(pathVariable.value());
                }
                if(fromParam!=null){
                    fromParams.add(fromParam.value());
                }
            }
            controller.setPathVariable(pathVariables);
            controller.setFromParams(fromParams);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String requestURI = request.getRequestURI();
        BaseController baseController = routerMap.get(requestURI);
        if (baseController!=null){
            if(baseController.getRequestMethod().toUpperCase().equals(request.getMethod())) {
                try {
                    PathVariableMsgHandler pathVariableMsgHandler = new PathVariableMsgHandler();
                    List<Object> process = pathVariableMsgHandler.process(baseController, request, response);
                    if (baseController.isReturnView()) {
                        String view = (String) baseController.getMethod().invoke(baseController.getObject(), request, response);
                        request.getRequestDispatcher(view).forward(request,response);

                    } else {
                        Object returnValue = baseController.getMethod().invoke(baseController.getObject(), process.toArray());
                        String s = JSON.toJSONString(returnValue);
                        response.setContentType("text/json; charset=utf-8");
                        PrintWriter out = response.getWriter();
                        out.print(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getOutputStream().print("error");
                }
            }else {
                response.getOutputStream().print("405");
            }
        }else{
            response.getOutputStream().print("404");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }





}
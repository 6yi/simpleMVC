package cn.lzheng.simpleMVC;

import cn.lzheng.simpleMVC.jsonProcess.JsonProcessHandlerAdapter;
import cn.lzheng.simpleMVC.msgHandler.BaseMsgHandler;
import cn.lzheng.simpleMVC.msgHandler.FromParamsMsgHandler;
import cn.lzheng.simpleMVC.msgHandler.JsonMsgHandler;
import cn.lzheng.simpleMVC.msgHandler.PathVariableMsgHandler;
import cn.lzheng.simpleMVC.mvcException.ParamsException;
import cn.lzheng.simpleMVC.Utils.ClassScanner;
import cn.lzheng.simpleMVC.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @ClassName RouterServlet
 * @Author 刘正
 * @Date 2020/9/14 17:59
 * @Version 1.0
 * @Description:
 *
 *  Dispatcher,所有的请求先到这里,然后匹配消息处理器,
 *
 */

public class RouterServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(RouterServlet.class);

    private String scanSrc;
    private String staticPath;
    private String suffix;

    public RouterServlet(Configuration annotationConfig) {
        this.scanSrc = annotationConfig.controllerSrc();
        this.staticPath = annotationConfig.controllerSrc();
        this.suffix = "/index"+annotationConfig.suffix();
    }

    private  static PathVariableMsgHandler pathVariableMsgHandler;

    private static JsonMsgHandler jsonMsgHandler;

    private static FromParamsMsgHandler fromParamsMsgHandler;



    static {
        pathVariableMsgHandler=new PathVariableMsgHandler();
        jsonMsgHandler=new JsonMsgHandler();
        fromParamsMsgHandler=new FromParamsMsgHandler();
    }


    private static HashMap<String,BaseController> routerMap;

    /**
     * @author 6yi
     * @date 2020/9/26
     * @return
     * @Description 初始化,扫描controller下的包
     **/
    @Override
    public void init() throws ServletException {
        logger.debug("routerServlet init---------");
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


//    public void addParams(BaseController controller,Method method){
//        Parameter[] parameters = method.getParameters();
//        if(parameters!=null){
//            ArrayList<String> pathVariables = new ArrayList<>();
//            ArrayList<String> fromParams = new ArrayList<>();
//            for (int i = 0; i < parameters.length; i++) {
//                PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
//                FromParams fromParam = parameters[i].getAnnotation(FromParams.class);
//                if(pathVariable!=null){
//                    pathVariables.add(pathVariable.value());
//                }
//                if(fromParam!=null){
//                    fromParams.add(fromParam.value());
//                }
//            }
//            controller.setPathVariable(pathVariables);
//            controller.setFromParams(fromParams);
//        }
//    }


    /**
     * @author 6yi
     * @date 2020/9/26
     * @return
     * @Description 消息处理器选择器,根据conten-type进行匹配
     **/
    public BaseMsgHandler selectMsgHandler(HttpServletRequest request){
        String contentType = request.getContentType();
        if(contentType==null){
            return pathVariableMsgHandler;
        }else if(contentType.equals("application/x-www-form-urlencoded")){
            return fromParamsMsgHandler;
        }else if(contentType.equals("application/json")){
            return jsonMsgHandler;
        }else{
            return pathVariableMsgHandler;
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String requestURI = request.getRequestURI();
        BaseController baseController = routerMap.get(requestURI);

        if (baseController!=null){
            if(baseController.getRequestMethod().toUpperCase().equals(request.getMethod())) {
                try{
                    //获取消息处理器
                    BaseMsgHandler MsgHandler = selectMsgHandler(request);

                    //获取参数
                    List<Object> process = MsgHandler.process(baseController, request, response);

                    //是否需要返回视图
                    if (baseController.isReturnView()) {
                        String view = (String) baseController.getMethod().invoke(baseController.getObject(), process.toArray());
                        request.getRequestDispatcher(view).forward(request,response);
                    } else {
                        //否则返回json
                        Object returnValue=baseController.getMethod().invoke(baseController.getObject(), process.toArray());
                        response.setContentType("text/json; charset=utf-8");
                        response.getWriter().print(JsonProcessHandlerAdapter.getJsonProcessHandler().toJsonString(returnValue));
                    }





                }catch (ParamsException e){
                    e.printStackTrace();
                    response.getOutputStream().print("error_Code:415");
                }catch (Exception e) {
                    e.printStackTrace();
                    response.getOutputStream().print("error");
                }
            }else {
                response.getOutputStream().print("error_Code:405");
            }
        }else{
                if (requestURI.equals("/")&&this.suffix.equals("/index.jsp")){
                        request.getRequestDispatcher("/index.jsp").forward(request,response);
                        return;
                    }else{
                        requestURI=this.suffix;
                }
                request.setAttribute("url",staticPath+requestURI);
                request.getRequestDispatcher("/simpleMvcStatic"+requestURI).forward(request,response);

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }





}
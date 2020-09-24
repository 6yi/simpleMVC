package cn.lzheng.simpleMVC;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @ClassName BaseController
 * @Author 刘正
 * @Date 2020/9/14 22:44
 * @Version 1.0
 * @Description:
 */


public class BaseController {

    private Method method;
    private String requestMethod;
    private Object object;
    private boolean isReturnView=true;

    private ArrayList<String> pathVariable;

    private ArrayList<String> FromParams;

    public ArrayList<String> getPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(ArrayList<String> pathVariable) {
        this.pathVariable = pathVariable;
    }

    public ArrayList<String> getFromParams() {
        return FromParams;
    }

    public void setFromParams(ArrayList<String> fromParams) {
        FromParams = fromParams;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isReturnView() {
        return isReturnView;
    }

    public void setReturnView(boolean returnView) {
        isReturnView = returnView;
    }
}

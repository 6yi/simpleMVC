package cn.lzheng.simpleMVC.jsonProcess;

/**
 * @ClassName JsonProcesHandler
 * @Author 刘正
 * @Date 2020/9/25 10:05
 * @Version 1.0
 * @Description:
 */

public interface JsonProcessHandler<T> {

    String toJsonString(Object object);

    Object toJavaObject(String json,Class clazz);

}

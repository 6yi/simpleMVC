package cn.lzheng.simpleMVC.jsonProcess;

import cn.lzheng.simpleMVC.annotation.JsonProcess;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName FastJsonProcessHandler
 * @Author 刘正
 * @Date 2020/9/25 10:14
 * @Version 1.0
 * @Description:
 */

public class FastJsonProcessHandler<T> implements JsonProcessHandler {

    private static Logger logger = LoggerFactory.getLogger(FastJsonProcessHandler.class);

    @Override
    public String toJsonString(Object object) {
        return JSONObject.toJSONString(object);
    }

    @Override
    public T toJavaObject(String json,Class clazz) {
        return  (T)JSONObject.parseObject(json,clazz);
    }

}

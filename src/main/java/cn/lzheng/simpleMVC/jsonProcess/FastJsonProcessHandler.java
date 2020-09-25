package cn.lzheng.simpleMVC.jsonProcess;

import cn.lzheng.simpleMVC.annotation.JsonProcess;
import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName FastJsonProcessHandler
 * @Author 刘正
 * @Date 2020/9/25 10:14
 * @Version 1.0
 * @Description:
 */

public class FastJsonProcessHandler implements JsonProcessHandler {



    @Override
    public String toJsonString(Object object) {
        return JSONObject.toJSONString(object);
    }

    @Override
    public Object toJavaObject(String json,Object object) {
        return  JSONObject.parseObject(json,object.getClass());
    }

}

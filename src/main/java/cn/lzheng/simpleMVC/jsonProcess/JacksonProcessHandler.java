package cn.lzheng.simpleMVC.jsonProcess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @ClassName JacksonProcessHandler
 * @Author 刘正
 * @Date 2020/9/25 15:30
 * @Version 1.0
 * @Description:
 */


public class JacksonProcessHandler<T> implements JsonProcessHandler {

    private static Logger logger = LoggerFactory.getLogger(JacksonProcessHandler.class);

    @Override
    public String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String s=null;
        try {
            s = objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }



    @Override
    public T toJavaObject(String json, Class clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            T o = (T) mapper.readValue(json, clazz);
            logger.debug("object---"+o);
            return o;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

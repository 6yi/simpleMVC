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


public class JacksonProcessHandler implements JsonProcessHandler {

    private static Logger logger = LoggerFactory.getLogger(JacksonProcessHandler.class);

    @Override
    public String toJsonString(Object object) {
        logger.debug("jackson----------");
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
    public Object toJavaObject(String json, Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json,object.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

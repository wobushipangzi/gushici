import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public class Data {

    @Value("${strikeDB_channel}")
    public String strikeDBChannel;

    @Value("${hello}")
    private String hello;

    @Test
    public void test1(){

        String s = "jpg";

        System.out.println(s.toUpperCase());
    }




}

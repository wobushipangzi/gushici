import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Date {


    public static void main(String[] args) {

        List list = JSONObject.parseObject(JSONObject.toJSONString(null), List.class);
        if(null != list && list.size() > 3){
            System.out.println("ss");
        }
        System.out.println(list);


    }
}


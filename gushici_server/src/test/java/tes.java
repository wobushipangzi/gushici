

import com.alibaba.fastjson.JSONObject;
import com.gushici.common.utils.HttpClientUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class tes {

    public static void main(String[] args) {
        String url = "http://localhost:8081/comment/praise";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("fromOpenId","1234567");
        hashMap.put("toOpenId","7654321");
        hashMap.put("commentId","");
        hashMap.put("replyId","");
        hashMap.put("suibiId","3");

        String data = JSONObject.toJSONString(hashMap);

        AtomicInteger atomicInteger = new AtomicInteger(0);

        while (true){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String s = HttpClientUtils.httpPostJsonRestRequest(url, data);
                    int andAdd = atomicInteger.getAndAdd(1);
                    System.out.println("第" + andAdd + "次调用点赞接口返参" + s);
                }
            }).start();

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}

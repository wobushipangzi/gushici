package com.gushici.common.utils;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

public class BaiDuSample {

    //设置APPID/AK/SK
    public static final String APP_ID = "16965936";
    public static final String API_KEY = "mDeiVH9EBpnXnHBAoYe5CL6r";
    public static final String SECRET_KEY = "QQZv0w4IZDGyslz7nRofllQocP2BG0NL";

    public static String imgToText(MultipartFile file) throws IOException {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(3000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        byte[] bytes = file.getBytes();
        JSONObject res = client.basicGeneral(bytes, new HashMap<String, String>());
        return res.toString(2);
    }
}

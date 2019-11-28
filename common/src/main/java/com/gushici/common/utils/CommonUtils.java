package com.gushici.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CommonUtils {

    private static Logger logger = LoggerFactory.getLogger(CommonUtils.class);


    /**
     * 将输入流转为base64
     * @param in 输入流
     * @return
     */
    public static String getBase64FromInputStream(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(Base64.encodeBase64(data));
    }




    /**
     * base64转inputStream
     * @param base64string
     * @return
     */
    public static InputStream BaseToInputStream(String base64string){
        InputStream stream = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes1 = decoder.decodeBuffer(base64string);
            stream = new ByteArrayInputStream(bytes1);
        } catch (Exception e) {
            logger.error("base64转inputStream异常", e);
        }
        return stream;
    }

}

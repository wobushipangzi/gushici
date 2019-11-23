package com.gushici.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.gushici.common.result.GlobalResult;
import com.gushici.common.result.ResultCode;
import com.gushici.common.smallprogram.AliOOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OOSUtils {

    private static Logger logger = LoggerFactory.getLogger(OOSUtils.class);

    /**
     * 给图片添加水印
     */
    public static InputStream markImageByIcon(MultipartFile file, Integer degree) {
        logger.info("开始添加水印");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            //1原图片
            Image srcImg = ImageIO.read(file.getInputStream());

            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 2得到画笔对象
            Graphics2D graphics = buffImg.createGraphics();


            // 3、设置对线段的锯齿状边缘处理
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            // 4、设置水印旋转
            if (null != degree) {
                graphics.rotate(Math.toRadians(degree),  buffImg.getWidth()/2,buffImg.getHeight() /2);
            }
            // 5、设置水印文字颜色
            Color color = new Color(57, 60, 76);
            graphics.setColor(color);
            System.out.println("图片长：" + buffImg.getWidth() + "\n图片宽为：" + buffImg.getHeight());
            // 6、设置水印文字Font
            graphics.setFont(new Font("楷体", Font.BOLD, 20));
            // 7、设置水印文字透明度
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
            graphics.drawString("新火诗词",  buffImg.getWidth()-100, buffImg.getHeight() - 20);

            // 9、释放资源
            graphics.dispose();

            ImageIO.write(buffImg, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            logger.error("流转换异常提示:",e);
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                logger.error("处理图片水印流关闭异常");
            }
        }
        return null;
    }


    /**
     * 给图片添加图片水印
     */
    public static InputStream markImageByMoreIcon(MultipartFile file, Integer degree) {
        logger.info("开始添加图片水印");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            //将icon加载到内存中
            Image ic = ImageIO.read(new URL(AliOOS.ICON_URL));
            //icon高度
            int icheight = ic.getHeight(null);
            //icon宽
            int icWidth = ic.getWidth(null);
            //将源图片读到内存中
            Image img = ImageIO.read(file.getInputStream());
            //图片宽
            int width = img.getWidth(null);
            //图片高
            int height = img.getHeight(null);
            BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            //创建一个指定 BufferedImage 的 Graphics2D 对象
            Graphics2D g = bi.createGraphics();
            try {
                //x,y轴表示水印的位置
                int x = width - icWidth;
                int y = height - icheight;
                //设置对线段的锯齿状边缘处理
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                //呈现一个图像，在绘制前进行从图像空间到用户空间的转换
                g.drawImage(img.getScaledInstance(width,height,Image.SCALE_SMOOTH),0,0,null);
                if (null != degree) {
                    //设置水印旋转
                    g.rotate(Math.toRadians(degree),(double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
                }
                ImageIcon imgIcon = new ImageIcon(new URL(AliOOS.ICON_URL));
                //水印图象的路径 水印一般为gif或者png的，这样可设置透明度
                //得到Image对象。
                Image con = imgIcon.getImage();
                //透明度，最小值为0，最大值为1
                float clarity = 0.6f;
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,clarity));
                //表示水印图片的坐标位置(x,y)
                g.drawImage(con, x, y, null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            }finally {
                g.dispose();
            }
            String[] splitArr = file.getOriginalFilename().split("\\.");
            String format = splitArr[splitArr.length - 1];  //获取到的格式名字
            ImageIO.write(bi, format, os); // 保存图片
            logger.info("图片添加水印完成");
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            logger.error("图片添加水印方法异常", e);
            return null;
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                logger.error("处理图片水印流关闭异常");
            }
        }
    }


    /**
     * 上传文件到OOS
     *
     * @param inputStream   小程序获取到的文件输入流
     * @param folder 第一层文件夹名称
     * @param format 文件格式名
     */
    public static GlobalResult uploadToOOS(InputStream inputStream, String folder, String format) {
        logger.info("开始上传文件到OOS");
        GlobalResult globalResult = GlobalResult.success();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(AliOOS.ENDPOINT, AliOOS.ACCESSKEY_ID, AliOOS.ACCESSKEY_SECRET);
        try {
            //判断存储空间是否存在
            boolean exists = ossClient.doesBucketExist(AliOOS.BUCKET_NAME);
            if (!exists) {
                //不存在则创建一个
                String bucketName = AliOOS.BUCKET_NAME;
                // 新建存储空间默认为标准存储类型，私有权限。
                ossClient.createBucket(bucketName);
            }

            //设置上传到阿里云的文件路径
            String twoPath = DateTimeUtils.formatDateToYMD(new Date());
            //文件名
            String fileName = UUID.randomUUID().toString() + UUID.randomUUID().toString() + "." + format;
            //文件路径
            String filePath = folder + "/" + twoPath + "/" + fileName;
            //开始上传
            ossClient.putObject(AliOOS.BUCKET_NAME, filePath, inputStream);
            //获取上传文件的url地址
            String uploadUrl = "https://" + AliOOS.BUCKET_NAME + "." + AliOOS.ENDPOINT + "/" + filePath;

            HashMap<String, String> uploadUrlMap = new HashMap<>();
            uploadUrlMap.put("uploadUrl", uploadUrl);
            globalResult.setData(uploadUrlMap);
            logger.info("上传文件到OOS结束");
        }catch (Exception e){
            logger.error("上传文件至OOS异常", e);
            globalResult.setCode(ResultCode.上传文件到OOS失败.getCode());
            globalResult.setMessage(ResultCode.上传文件到OOS失败.getMsg());
        } finally {
            logger.info("关闭OSSClient");
            ossClient.shutdown();
        }
        return globalResult;
    }


    /**
     * 批量删除OOS存储内容
     * @param files  对象名称集合
     * @return
     */
    public static GlobalResult delToOOS(List<String> files) {

        GlobalResult globalResult = GlobalResult.success();

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(AliOOS.ENDPOINT, AliOOS.ACCESSKEY_ID, AliOOS.ACCESSKEY_SECRET);
        try {
            logger.info("开始从OOS删除文件");

            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(AliOOS.BUCKET_NAME).withKeys(files));

            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();

            logger.info("从OOS删除的文件名称：==>" + JSONObject.toJSONString(deletedObjects));
        } catch (Exception e) {
            logger.error("从OOS删除文件失败{}", e);
            globalResult.setCode(ResultCode.OOS删除数据失败.getCode());
            globalResult.setMessage(ResultCode.OOS删除数据失败.getMsg());
        } finally {
            // 关闭OSSClient。
            logger.info("关闭OOS实例");
            ossClient.shutdown();
        }
        logger.info("从OOS删除文件结束");
        return globalResult;
    }



}

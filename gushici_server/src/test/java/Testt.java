import com.gushici.common.utils.HttpClientUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class Testt {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        /*File file = new File("https://www.xinhuo.fun/statics/shuiyin.png");
        Image read = ImageIO.read(file);
        System.out.println("高：" + read.getHeight(null));
        System.out.println("宽：" + read.getWidth(null));*/


        String url = "https://www.xinhuo.fun/statics/shuiyin.png";
        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5000);
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=inStream.read(buffer)) != -1 ){
                outStream.write(buffer, 0, len);
            }

            byte[] bytes = outStream.toByteArray();


            try {
                File file = new File("D:/Test123/yuan.jpg");

                //将icon加载到内存中
                Image ic = ImageIO.read(new URL(url));
                //icon高度
                int icheight = ic.getHeight(null);
                //icon宽
                int icWidth = ic.getWidth(null);
                //将源图片读到内存中
                Image img = ImageIO.read(file);
                //图片宽
                int width = img.getWidth(null);
                //图片高
                int height = img.getHeight(null);
                BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
                //创建一个指定 BufferedImage 的 Graphics2D 对象
                Graphics2D g = bi.createGraphics();
                //x,y轴表示水印的位置
                int x = width - icWidth;
                int y = height - icheight;
                //设置对线段的锯齿状边缘处理
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                //呈现一个图像，在绘制前进行从图像空间到用户空间的转换
                g.drawImage(img.getScaledInstance(width,height,Image.SCALE_SMOOTH),0,0,null);

                //水印图象的路径 水印一般为gif或者png的，这样可设置透明度
                ImageIcon imgIcon = new ImageIcon(new URL(url));
                //得到Image对象。
                Image con = imgIcon.getImage();
                //透明度，最小值为0，最大值为1
                float clarity = 0.6f;
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,clarity));
                //表示水印图片的坐标位置(x,y)
                g.drawImage(con, x, y, null);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                g.dispose();
                inStream.close();
                File sf = new File("D:/Test123", "img_mark_icon.jpg");
                ImageIO.write(bi, "jpg", sf); // 保存图片
            } catch (Exception e) {
                e.printStackTrace();
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

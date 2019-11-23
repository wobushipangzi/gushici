import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ShuiYin {

    @Test
    public void shuiyin(){
        String srcImgPath = "D:/Test123/yuan.jpg";
        String iconPath = "D:/Test123/shuiyin.png";
        String targerPath = "D:/Test123/img_mark_icon.jpg";
        String targerPath2 = "D:/Test123/img_mark_icon_rotate.jpg";
        // 给图片添加水印
        //markImageByIcon(iconPath, srcImgPath, targerPath);
        // 给图片添加水印,水印旋转-45
        //markImageByIcon(iconPath, srcImgPath, targerPath2,-45);

        String s = markImageByMoreIcon(iconPath, srcImgPath, "D:/Test123", "img_mark_icon", "jpg", null);
        System.out.println("------" + s);
    }

    /**
     * 给图片添加水印
     * @param iconPath 水印图片路径
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     */
    public static void markImageByIcon(String iconPath, String srcImgPath,
                                       String targerPath) {
        markImageByIcon(iconPath, srcImgPath, targerPath, null);
    }

    /**
     * 给图片添加水印、可设置水印图片旋转角度
     * @param iconPath 水印图片路径
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     * @param degree 水印图片旋转角度
     */
    public static void markImageByIcon(String iconPath, String srcImgPath,
                                       String targerPath, Integer degree) {
        OutputStream os = null;
        try {
            //1原图片
            Image srcImg = ImageIO.read(new File(srcImgPath));

            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 2得到画笔对象
            Graphics2D g = buffImg.createGraphics();



            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            // 4、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree),  buffImg.getWidth()/2,buffImg.getHeight() /2);
            }
            // 5、设置水印文字颜色
            Color color = new Color(64, 66, 76);
            g.setColor(color);
            System.out.println("图片长：" + buffImg.getWidth() + "\n图片宽为：" + buffImg.getHeight());
            // 6、设置水印文字Font
            g.setFont(new Font("楷体", Font.BOLD, 20));
            // 7、设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
            g.drawString("新火诗词",  buffImg.getWidth()-100 , buffImg.getHeight() - 20);




            // 9、释放资源
            g.dispose();

            os = new FileOutputStream(targerPath);

            // 生成图片
            ImageIO.write(buffImg, "JPG", os);

            System.out.println("图片完成添加Icon印章。。。。。。");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static String markImageByMoreIcon(String icon,String source,String output,String imageName,String imageType,Integer degree) {
        String result = "添加图片水印出错";
        try {
            File file = new File(source);
            File ficon = new File(icon);
            if (!file.isFile()) {
                return source + " 不是一个图片文件!";
            }
            //将icon加载到内存中
            Image ic = ImageIO.read(ficon);
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
            if (null != degree) {
                //设置水印旋转
                g.rotate(Math.toRadians(degree),(double) bi.getWidth() / 2, (double) bi.getHeight() / 2);
            }
            //水印图象的路径 水印一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(icon);
            //得到Image对象。
            Image con = imgIcon.getImage();
            //透明度，最小值为0，最大值为1
            float clarity = 0.6f;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,clarity));
            //表示水印图片的坐标位置(x,y)
            g.drawImage(con, x, y, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.dispose();
            File sf = new File(output, imageName+"."+imageType);
            ImageIO.write(bi, imageType, sf); // 保存图片
            result = "图片完成添加Icon水印";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}

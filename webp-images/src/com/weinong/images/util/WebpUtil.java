package com.weinong.images.util;

import com.luciad.imageio.webp.WebPImageReaderSpi;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPWriteParam;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.weinong.images.Conf;
import com.weinong.images.base.PictureParams;
import com.weinong.images.modules.upload.Thumbnail;
import com.weinong.images.modules.upload.WaterMark;
import yao.util.log.*;
import yao.util.log.Console;
import yao.util.object.ObjectUtil;

import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by xxx on 2016/1/29.
 */
public class WebpUtil {

    /**
     * 生成webp图片，
     * @throws IOException
     */
    public synchronized static void getWebp(String srcfile,String desfile, float quality) throws IOException {

        Console.info(WebpUtil.class,(System.getProperty("java.library.path")));

//        System.loadLibrary("webp-imageio");  //切换到监听器里面加载了，不用再每次都进行加载了
        Console.info(WebpUtil.class,"加载结束");


        File src_file = new File(srcfile);
        File des_file = new File(desfile);
        if (!des_file.getParentFile().exists()) {

            des_file.getParentFile().mkdirs();
        }

        FileImageOutputStream __out = new FileImageOutputStream(des_file);
        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = findWriter(ImageIO.getImageWritersByMIMEType("image/webp"));
        WebPWriteParam webPWriteParam = (WebPWriteParam) imageWriter.getDefaultWriteParam();
        webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        webPWriteParam.setCompressionType("Lossy");
        webPWriteParam.setCompressionQuality(quality);

        imageWriter.setOutput(__out);
        imageWriter.write(null, new IIOImage(image, null, null), webPWriteParam);
        __out.flush();
        __out.close();
        imageWriter.dispose();
        Console.info(WebpUtil.class,"zoom：" + srcfile
                + "  to  " + desfile);
    }

    /**
     * 此方法不可用！！！！！！只是当时留下的一个疑问回头看看能解不
     * @throws IOException
     */
    private static void getWebp2() throws IOException {

        System.loadLibrary("webp-imageio");
        String srcfile = "D:\\temp\\image/aa.jpg";
        String desfile = "D:\\temp\\image/rrrrrrrrrrr22222222222222.jpg.webp";
        File src_file = new File(srcfile);
        File des_file = new File(desfile);

        FileOutputStream __out = new FileOutputStream(des_file);
        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = findWriter(ImageIO.getImageWritersByMIMEType("image/webp"));
//         new WebPReadParam();
        WebPWriteParam webPWriteParam = (WebPWriteParam) imageWriter.getDefaultWriteParam();
        webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        webPWriteParam.setCompressionType("Lossy");
        webPWriteParam.setCompressionQuality(0.8f);

//        ImageWriter imageWriter = new ImageWriter(new ImageWriterSpi() );
        imageWriter.setOutput(ImageIO.createImageOutputStream(__out));
        imageWriter.write(null, new IIOImage(image, null, null), webPWriteParam);

        imageWriter.dispose();
        __out.flush();
        __out.close();

    }

    /**
     * 比较原图和目标图的大小 source>des true
     * else return false
     * @param source
     * @param des
     * @return
     */
    public  static boolean compareSize(String source, String des) throws IOException {

        File source_file = new File(source);
        File des_file = new File(des);
        if (!des_file.exists()) {
            des_file.createNewFile();
        }
        return source_file.length() > des_file.length();
    }


    public  static void copyFile(String source, String des) throws IOException {

        int length = 0;
        File des_file = new File(des);
        if (!des_file.exists()) {
            des_file.createNewFile();
        }

        FileInputStream inputStream = new FileInputStream(source);
        FileOutputStream outputStream = new FileOutputStream(des_file);

        while ((length= inputStream.read()) != -1) {
            outputStream.write(length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }



    /**
     * 针对webp寻找支持webp的writer
     * @param writers
     * @return
     */
    public static ImageWriter findWriter(Iterator<ImageWriter> writers) {
        ImageWriter writer = null;
        while (writers.hasNext()) {
            ImageWriter writerCandidate = writers.next();
            if (writerCandidate.getOriginatingProvider() instanceof WebPImageWriterSpi) {
                writer = writerCandidate;
                break;
            }
        }
        return writer;
    }

    /**
     * 针对webp寻找支持webp的readers
     * @param readers
     * @return
     */
    public static ImageReader findReader(Iterator<ImageReader> readers) {
        ImageReader reader = null;
        while (readers.hasNext()) {
            ImageReader readerCandidate = readers.next();
            if (readerCandidate.getOriginatingProvider() instanceof WebPImageReaderSpi) {
                reader = readerCandidate;
                break;
            }
        }
        return reader;
    }


    /**
     * z此方法相对于方法一多了一段，用来处理编码，显式的指定压缩比例为1，打上水印以后图片大小变化较小，方法一的变化很大将近是原来的十分之一
     * @param pressText
     * @param srcImg
     * @param targetImg
     * @param fontName
     * @param fontStyle
     * @param color
     * @param fontSize
     * @param x
     * @param y
     * @param alpha
     */
    @SuppressWarnings("restriction")
    public static void pressText3(String pressText, String srcImg, String targetImg, String fontName, int fontStyle, Color color, int fontSize, int x, int y, float alpha) {

        try {
            File _file = new File(srcImg);
            Image src = ImageIO.read(_file);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            int textCoordinateX = (x < 0) ? (width + x -fontSize*pressText.length()/2 - fontSize ) : x;
            int textCoordinateY = (y < 0) ? (height + y -fontSize / 2 ) : y + fontSize;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);

            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            g.drawString(pressText, textCoordinateX, textCoordinateY);
            g.dispose();


            FileOutputStream out = new FileOutputStream(targetImg);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(1.0f, true);// 默认0.75
            encoder.setJPEGEncodeParam(param);
            encoder.encode(image);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 在对图片进行压缩裁剪的时候同时进行水印添加，这里填写的是一个测试类，不能用于生产的
     * @param pressText
     * @param source
     * @param dest
     * @param width
     * @param height
     * @param textCoordinateX
     * @param textCoordinateY
     * @throws IOException
     */
    public static void drawImage(String pressText ,String source, String dest, int width, int height ,int textCoordinateX, int textCoordinateY) throws IOException {
        System.loadLibrary("webp-imageio");

        BufferedImage bufferedImagesrc = ImageIO.read(new File(source));
        BufferedImage bufferedImagedes = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Color color = Color.white;
        File desfile = new File(dest);
        if (!desfile.getParentFile().exists()) {
            desfile.getParentFile().mkdirs();
        }
        Graphics2D g = bufferedImagedes.createGraphics();
        g.drawImage(bufferedImagesrc, 0, 0, width, height, null);

        g.setColor(color);
        g.setFont(new Font("宋体", Font.BOLD, 12));
//        g.setFont(new Font("黑体", Font.BOLD, 12));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));//透明度设置

        g.drawString(pressText, textCoordinateX, textCoordinateY);
        g.dispose();

        FileImageOutputStream __out = new FileImageOutputStream(new File(dest));
//        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = findWriter(ImageIO.getImageWritersByMIMEType("image/webp"));
        WebPWriteParam webPWriteParam = (WebPWriteParam) imageWriter.getDefaultWriteParam();
        webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        webPWriteParam.setCompressionType("Lossy");
        webPWriteParam.setCompressionQuality(0.2f);

        imageWriter.setOutput(__out);
        imageWriter.write(null, new IIOImage(bufferedImagedes, null, null), webPWriteParam);
        __out.flush();
        __out.close();
        imageWriter.dispose();
        Console.info(WebpUtil.class,"zoom：" + source
                + "  to  " + desfile);
    }

    public static void scalePartlyWithWaterMark(String source, String dest, int width, int height , WaterMark waterMark, float compress_ratio, boolean isWebp) throws IOException {

        BufferedImage bufferedImagesrc = ImageIO.read(new File(source));
        BufferedImage bufferedImagedes = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);


        File desfile = new File(dest);
        if (!desfile.getParentFile().exists()) {
            desfile.getParentFile().mkdirs();
        }
        Graphics2D g = bufferedImagedes.createGraphics();
        g.drawImage(bufferedImagesrc, 0, 0, width, height, null);

        int x = waterMark.getCoordinateX();
        int y = waterMark.getCoordinateY();
        int fontSize = waterMark.getSize();
        String pressText = waterMark.getTextForWaterMark();
        int textCoordinateX = (x < 0) ? (width + x  ) : x;
        int textCoordinateY = (y < 0) ? (height + y - fontSize / 2 ) : y + fontSize;
        WaterMark waterMark1 = new WaterMark();
        ObjectUtil.copyProperty(waterMark,waterMark1);
        waterMark1.setCoordinateX(textCoordinateX);
        waterMark1.setCoordinateY(textCoordinateY);

        pressWaterMarkPartly(g, waterMark1);

        g.dispose();

        if (isWebp) {

            compressToWebp(bufferedImagedes, dest, compress_ratio);
        } else {
            compressToJpg(bufferedImagedes, dest, compress_ratio);
        }

        Console.info(WebpUtil.class,"scale watermark and compress file ：" + source
        + "  to  " + desfile);


    }
    public static void scalePartly(String source, String dest, int width, int height , WaterMark waterMark, float compress_ratio, boolean isWebp) throws IOException {

        BufferedImage bufferedImagesrc = ImageIO.read(new File(source));
        BufferedImage bufferedImagedes = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        File desfile = new File(dest);
        if (!desfile.getParentFile().exists()) {
            desfile.getParentFile().mkdirs();
        }
        Graphics2D g = bufferedImagedes.createGraphics();
        g.drawImage(bufferedImagesrc, 0, 0, width, height, null);

//        pressWaterMarkPartly(g, waterMark);

        g.dispose();

        if (isWebp) {

            compressToWebp(bufferedImagedes, dest, compress_ratio);
        } else {
            compressToJpg(bufferedImagedes, dest, compress_ratio);
        }

        Console.info(WebpUtil.class,"scale watermark and compress file ：" + source
        + "  to  " + desfile);


    }
    public static void pressWaterMarkPartly(Graphics2D g , WaterMark waterMark) {

        int red = Conf.red;
        int green = Conf.green;
        int blue = Conf.blue;
        Color color = new Color(red, green, blue);
        g.setColor(color);
        g.setFont(new Font("宋体", Font.BOLD, 12));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));//透明度设置
        Console.info(WebpUtil.class, "图片要添加的水印文字是 ： " +waterMark.getTextForWaterMark());
        g.drawString(waterMark.getTextForWaterMark(), waterMark.getCoordinateX(), waterMark.getCoordinateY());

    }

    public static void compressToWebp(BufferedImage bufferedImagedes, String dest, float compress_ratio) throws IOException {

        FileImageOutputStream __out = new FileImageOutputStream(new File(dest));
//        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = findWriter(ImageIO.getImageWritersByMIMEType("image/webp"));
        WebPWriteParam webPWriteParam = (WebPWriteParam) imageWriter.getDefaultWriteParam();
        webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        webPWriteParam.setCompressionType("Lossy");
        webPWriteParam.setCompressionQuality(compress_ratio);

        imageWriter.setOutput(__out);
        imageWriter.write(null, new IIOImage(bufferedImagedes, null, null), webPWriteParam);
        __out.flush();
        __out.close();
        imageWriter.dispose();

    }

    public static void compressToJpg(BufferedImage bufferedImagedes, String dest, float compress_ratio) throws IOException {

        FileImageOutputStream __out = new FileImageOutputStream(new File(dest));
//        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = ImageIO.getImageWritersByMIMEType("image/jpeg").next();
        ImageWriteParam imageWriteParam =  imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        imageWriteParam.setCompressionType("Lossy");//webp实现的，在这里是没有的
        imageWriteParam.setCompressionQuality(compress_ratio);

        imageWriter.setOutput(__out);
        imageWriter.write(null, new IIOImage(bufferedImagedes, null, null), imageWriteParam);
        __out.flush();
        __out.close();
        imageWriter.dispose();


    }


    public static PictureParams getDestSizeWithDiffScaleWay(String type, String fileName,
                                                             int width, int height) throws IOException {

        if (type.equals("fix_width")) {

            Image image = ImageIO.read(new File(fileName));
            int realWidth = image.getWidth(null);

            return new PictureParams.Builder(realWidth,height).build();

        } else if (type.equals("fix_height")) {

            Image image = ImageIO.read(new File(fileName));
            int realHeight = image.getHeight(null);

            return new PictureParams.Builder(width,realHeight).build();

        } else if (type.equals("fix_smart")) {

            Image image = ImageIO.read(new File(fileName));
            int sourceWidth = image.getWidth(null);
            int sourceHeight = image.getHeight(null);
            float ratio = 0.0f;

            if ((sourceWidth / width) == (sourceHeight / height)) {
                ratio = (sourceWidth * 1.0f / width);
            } else {
                ratio = (((sourceWidth * 1.0f / width) > (sourceHeight * 1.0f / height)) ?
                        (sourceWidth * 1.0f / width) : (sourceHeight * 1.0f / height));
            }
            int realWidth = (int) (sourceWidth / ratio);
            int realHeight = (int) (sourceHeight / ratio);

            return new PictureParams.Builder(realWidth,realHeight).build();

        } else if (type.equals("scale")) {

            return new PictureParams.Builder(width,height).build();

        } else if (type.equals("cut")) {

            return new PictureParams.Builder(width,height).build();

        }

        return null;
    }


    /**
     * 没有用的方法
     * @param src
     * @param dest
     * @param compress_ratio
     * @throws IOException
     */
    @Deprecated
    public static void cut(String src, String dest, float compress_ratio) throws IOException {


        if (dest.endsWith("jpg")) {

//            cutjpg(src, dest, compress_ratio);

        } else if (dest.endsWith("webp")) {
            String bridge_file = dest + "jpg";
//            cutjpg(src, bridge_file, compress_ratio);
            src = bridge_file;
        }

        //将图片进行水印以及压缩处理


       /* System.loadLibrary("webp-imageio");
        BufferedImage bufferedImagedes = ImageIO.read(new File(src));
        Rectangle rect = new Rectangle(0, 0, 30, 500);


        FileImageOutputStream __out = new FileImageOutputStream(new File(dest));
//        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = findWriter(ImageIO.getImageWritersByMIMEType("image/webp"));
        WebPWriteParam webPWriteParam = (WebPWriteParam) imageWriter.getDefaultWriteParam();
        webPWriteParam.setSourceRegion(rect);
        webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        webPWriteParam.setCompressionType("Lossy");
        webPWriteParam.setCompressionQuality(compress_ratio);

        imageWriter.setOutput(__out);
        imageWriter.write(null, new IIOImage(bufferedImagedes, null, null), webPWriteParam);
        __out.flush();
        __out.close();
        imageWriter.dispose();*/

    }

    /**
     * 将图片进行剪切操作，最后生成jpg图片，目前是将compress_ratio参数做了屏蔽处理，测试发现不设置默认的效果依然比较好
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void cutjpg(String src, String dest, Thumbnail thumbnail) throws IOException {

        BufferedImage bufferedImagedes = ImageIO.read(new File(src));
        Rectangle rect = new Rectangle(0, 0, thumbnail.getWidth(), thumbnail.getHeight());

        FileImageOutputStream __out = new FileImageOutputStream(new File(dest));
//        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = ImageIO.getImageWritersByMIMEType("image/jpeg").next();
        ImageWriteParam imageWriteParam =  imageWriter.getDefaultWriteParam();
        imageWriteParam.setSourceRegion(rect);
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        imageWriteParam.setCompressionType("Lossy");//webp实现的，在这里是没有的
//        imageWriteParam.setCompressionQuality(compress_ratio);//在实验中发现，cut的时候不设置质量参数，图片会变小，而且是最协调的清晰度

        imageWriter.setOutput(__out);
        imageWriter.write(null, new IIOImage(bufferedImagedes, null, null), imageWriteParam);
        __out.flush();
        __out.close();
        imageWriter.dispose();


    }

   /* private static PictureParams getSizeWithNoExchangeWH(String fileName, String destFileName, int width, int height) {

        return null;
    }

    private static PictureParams getSizeWithSmart(String fileName, String destFileName, int width, int height) {
        return null;
    }

    private static PictureParams getSizeWithFixHeight(String fileName, String destFileName, int width, int height) {
        return null;
    }

    private static PictureParams getSizeWithFixWith(String fileName, String destFileName, int width, int height) {
        return null;
    }*/


    public static void main(String[] args) throws IOException {


//        Font font = new Font("Consolas", Font.BOLD, 12);
//        Font font = new Font("Courier", Font.BOLD, 12);
//        Font font = new Font("David", Font.BOLD, 12);
        Font font = new Font("宋体", Font.BOLD, 12);
        font.getFontName();
        System.out.println(font.getFontName());
        System.out.println(font.getName());

//        WaterMark waterMark = new WaterMark();
//        waterMark.setHaveWaterMark(true);
//        waterMark.setCoordinate("5,5");
//        waterMark.setTextForWaterMark("图片压缩水印一体化");
        drawImage("图片压缩水印一体化","D:\\temp\\image/001.jpg","D:\\temp\\image/56568.jpg.webp",640,640,20,20);
////        scalePartlyWithWaterMark("D:\\temp\\image/222.jpg","D:\\temp\\image/222.jpg.webp",320, 640, waterMark, 90/100.0f ,true);
////    cut ("D:\\temp\\image/001.jpg","D:\\temp\\image/002562rt.jpg.webp",0.99f);
////    cutjpg ("D:\\temp\\image/66.jpg","D:\\temp\\image/1abcart666.jpg",0.99f);//
//
//        String aa = "nininini";
//        String BB = aa;

    }

}

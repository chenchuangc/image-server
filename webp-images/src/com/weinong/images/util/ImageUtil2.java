package com.weinong.images.util;

import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.weinong.images.base.PictureParams;
import yao.util.log.Console;
import yao.util.string.StringUtil;

/**
 * 处理图片，压缩放大等
 *
 * @author yicha 创建人：顾代辉 创建时间：2012-4-13
 */
@SuppressWarnings("restriction")
public class ImageUtil2 {

    private static Component component = new Canvas();

    // ".pcx","tga",".tif"这三种格式目前还不支持； 这些定义的格式经过我测试过是可以支持的。
    private static String[] imageFormatArray = new String[]{".jpg", ".jpeg",
            ".gif", ".png", ".bmp"};

    /**
     * 校验图像文件的格式是否可以进行缩放
     *
     * @param fileName fileName
     * @return boolean iszoom able
     */
    public synchronized static boolean isZoomAble(String fileName) {
        boolean result = false;
        for (String imageFormat : imageFormatArray) {
            if (fileName.toLowerCase().lastIndexOf(imageFormat) == (fileName
                    .length() - imageFormat.length())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 获取图片的类型
     *
     * @param imgFileName
     * @return
     */
    public static String getFormatName(String imgFileName) {
        String format = "";
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(new File(
                    imgFileName));
            // Find all image readers that recognize the image format
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            if (!iter.hasNext()) { // No readers found
                return null;
            }
            ImageReader reader = iter.next();
            format = reader.getFormatName();
            iis.close();
            // Return the format name
        } catch (IOException e) {
        }
        return format;
    }

    /**
     * 转换图片格式
     *
     * @param fileName
     * @param format
     * @param destFileName
     * @return
     */
    public static boolean convertFormat(String fileName, String format,
                                        String destFileName) {
        try {
            RenderedImage img = ImageIO.read(new File(fileName));
            ImageIO.write(img, format, new File(destFileName));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 把图片印刷到图片上
     *
     * @param pressImg  -- 水印文件
     * @param targetImg -- 目标文件
     * @param x         -- 偏移量x
     * @param y         -- 偏移量y
     */
    public synchronized static void pressImage(String pressImg,
                                               String targetImg, int x, int y) {
        try {
            File _file = new File(targetImg);
            Image src = ImageIO.read(_file);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);

            // 水印文件
            File _filebiao = new File(pressImg);
            Image src_biao = ImageIO.read(_filebiao);
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            g.drawImage(src_biao, wideth - wideth_biao - x, height
                    - height_biao - y, wideth_biao, height_biao, null);
            // /
            g.dispose();
            FileOutputStream out = new FileOutputStream(targetImg);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(image);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized static boolean compressImageWithNoqualityChange(String fileName,
                                                     String targetFileName, float compressRatio) {
        try {
            File _file = new File(fileName);
            Image src = ImageIO.read(_file);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
//			String size = "" + width + "x" + height;
//			targetFileName.replace("80x80", size);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);

            if (!new File(targetFileName).getParentFile().exists()) {
                new File(targetFileName).getParentFile().mkdirs();
            }

            FileOutputStream out = new FileOutputStream(targetFileName);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(compressRatio, true);// 默认0.75
            encoder.setJPEGEncodeParam(param);
            encoder.encode(image);
            out.close();
            return true;
//			return targetFileName;
        } catch (Exception e) {
            e.printStackTrace();
//			return targetFileName;
            return false;
        }
    }
    /**
     * 图片质量压缩，压缩后图片大小变小，图片的质量变差
     *
     * @param fileName       源图片文件
     * @param targetFileName 压缩后目的文件
     * @param compressRatio  压缩率
     * @return
     */
    public synchronized static boolean compressImage(String fileName,
                                                     String targetFileName, float compressRatio) {
        try {
            File _file = new File(fileName);
            Image src = ImageIO.read(_file);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
//			String size = "" + width + "x" + height;
//			targetFileName.replace("80x80", size);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);

            if (!new File(targetFileName).getParentFile().exists()) {
                new File(targetFileName).getParentFile().mkdirs();
            }

            FileOutputStream out = new FileOutputStream(targetFileName);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(compressRatio, true);// 默认0.75
            encoder.setJPEGEncodeParam(param);
            encoder.encode(image);
            out.close();
            return true;
//			return targetFileName;
        } catch (Exception e) {
            e.printStackTrace();
//			return targetFileName;
            return false;
        }
    }
    /**
     * 图片质量压缩，压缩后图片大小变小，图片的质量变差
     *
     * @param fileName       源图片文件
     * @param targetFileName 压缩后目的文件
     * @param compressRatio  压缩率
     * @return
     */
    public synchronized static boolean compressImageByWriter(String fileName,
                                                             String targetFileName, float compressRatio) {
        try {
            File src_file = new File(fileName);
            BufferedImage  src_image = ImageIO.read(src_file);
            int width = src_image.getWidth(null);
            int height = src_image.getHeight(null);
            String formate = StringUtil.getRight(targetFileName, ".");

            if(src_image.getTransparency() == Transparency.TRANSLUCENT)//说明图片里面有透明色，需要丢弃Alpha通道
//                src_image = get24BitImage(src_image, Color.white);
               src_image = get24BitImage(src_image);


            if (!new File(targetFileName).getParentFile().exists()) {
                new File(targetFileName).getParentFile().mkdirs();
            }

            ImageWriter image_writer = ImageIO.getImageWritersByFormatName(formate).next();
            ImageWriteParam writeParam = image_writer.getDefaultWriteParam();
            FileOutputStream image_out = new FileOutputStream(targetFileName);
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(compressRatio);
            image_writer.setOutput(ImageIO.createImageOutputStream(image_out));
            image_writer.write(null, new IIOImage(src_image, null, null), writeParam);

            image_out.flush();

            image_out.close();

            image_writer.dispose();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
//			return targetFileName;
            return false;
        }
    }

    private static BufferedImage get24BitImage(BufferedImage src_image, Color color) {

        int width = src_image.getWidth();
        int height = src_image.getHeight();
        BufferedImage __image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphic = __image.createGraphics();
        graphic.setColor(color);
        graphic.fillRect(0,0,width,height);
        graphic.drawRenderedImage(src_image, null);
        graphic.dispose();
        return __image;
    }

    protected static BufferedImage get24BitImage(BufferedImage $image) {

        int __w = $image.getWidth();
        int __h = $image.getHeight();
        int[] __imgARGB = getRGBs($image.getRGB(0, 0, __w, __h, null, 0, __w));
        BufferedImage __newImg = new BufferedImage(__w, __h, BufferedImage.TYPE_INT_RGB);
        __newImg.setRGB(0, 0, __w, __h, __imgARGB, 0, __w);
        return __newImg;

    }


    /** * 将32位色彩转换成24位色彩（丢弃Alpha通道） * @param $argb * @return */
    public static int[] getRGBs(int[] $argb) {

        int[] __rgbs = new int[$argb.length];
        for(int i=0;i<$argb.length;i++) {
            __rgbs[i] = $argb[i] & 0xFFFFFF;
        } return __rgbs;
    }



    public synchronized static boolean compress(String fileName,
                                                     String targetFileName, float compressRatio) {
        try {
            File _file = new File(fileName);
            Image src = ImageIO.read(_file);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);

            if (!new File(targetFileName).getParentFile().exists()) {
                new File(targetFileName).getParentFile().mkdirs();
            }

            FileOutputStream out = new FileOutputStream(targetFileName);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(compressRatio, true);// 默认0.75
//            encoder.setJPEGEncodeParam(param);
            encoder.encode(image,param);
            out.close();
            return true;
//			return targetFileName;
        } catch (Exception e) {
            e.printStackTrace();
//			return targetFileName;
            return false;
        }
    }

    /**
     * 压缩图片为指定大小，判断高宽并按高宽替换参数
     *
     * @param fileName
     * @param destFileName
     * @param width
     * @param height
     * @throws Exception
     */
    public synchronized static void createZoomSizeImage(String fileName,
                                                        String destFileName, int width, int height) throws Exception {
        Image image = ImageIO.read(new File(fileName));

        if (image.getWidth(null) > image.getHeight(null)) {// 高宽互换
            width += height;
            height = width - height;
            width -= height;
        }

        AreaAveragingScaleFilter areaAveragingScaleFilter = new AreaAveragingScaleFilter(
                width, height);
        FilteredImageSource filteredImageSource = new FilteredImageSource(
                image.getSource(), areaAveragingScaleFilter);
        BufferedImage bufferedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics graphics = bufferedImage.createGraphics();
        graphics.drawImage(component.createImage(filteredImageSource), 0, 0,
                null);
        File dest = new File(destFileName);
        if (!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();

        ImageIO.write(bufferedImage, "JPEG", dest);

        Console.info(ImageUtil2.class,"zoom：(" + width + " X " + height + ")" + fileName
                + "  to  " + destFileName);
    }

    /**
     * 压缩图片为指定大小，不判定高宽
     *
     * @param fileName
     * @param destFileName
     * @param width
     * @param height
     * @throws Exception
     */
    public synchronized static String createZoomSizeImageNoExchangeWH(
            String fileName, String destFileName, int width, int height)
            throws Exception {
        Image image = ImageIO.read(new File(fileName));
        int realWidth = image.getWidth(null);
        int realHeight = image.getHeight(null);
//	    width = (realWidth < width) ? realWidth : width;
//	    height = (realHeight < height) ? realHeight : height;
//		

        String size = "" + width + "x" + height;
        destFileName = destFileName.replaceAll("[0-9]+[x][0-9]+", size);

        AreaAveragingScaleFilter areaAveragingScaleFilter = new AreaAveragingScaleFilter(
                width, height);
        FilteredImageSource filteredImageSource = new FilteredImageSource(
                image.getSource(), areaAveragingScaleFilter);
        BufferedImage bufferedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics graphics = bufferedImage.createGraphics();
        graphics.drawImage(component.createImage(filteredImageSource), 0, 0,
                null);
        File dest = new File(destFileName);
        if (!dest.getParentFile().exists())
            dest.getParentFile().mkdirs();

        FileOutputStream out = new FileOutputStream(dest);
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);
        param.setQuality(1.0f, true);// 默认0.75
        encoder.setJPEGEncodeParam(param);
        encoder.encode(bufferedImage);
        out.close();

//		ImageIO.write(bufferedImage, "JPEG", dest);


        Console.info(ImageUtil2.class,"zoom：(" + width + " X " + height + ")" + fileName
                + "  to  " + destFileName);
        return size;
    }

    /**
     * 固定宽度，缩小高度
     *
     * @param fileName
     * @param destFileName
     * @param width
     * @param height
     * @throws Exception
     */
    public static String createZoomImageWithFixWith(String fileName,
                                                    String destFileName, int width, int height) throws Exception {
        Image image = ImageIO.read(new File(fileName));
        int realWidth = image.getWidth(null);
//		
        String size = createZoomSizeImageNoExchangeWH(fileName, destFileName, realWidth, height);
        return size;
    }


    /**
     * 固定高度，缩小宽度
     *
     * @param fileName
     * @param destFileName
     * @param width
     * @param height
     * @throws Exception
     */
    public static String createZoomImageWithFixHeight(String fileName,
                                                      String destFileName, int width, int height) throws Exception {

        Image image = ImageIO.read(new File(fileName));
        int realHeight = image.getWidth(null);

        String size = createZoomSizeImageNoExchangeWH(fileName, destFileName, width, realHeight);
        return size;
    }


    /**
     * 智能选择，宽和高，那个压缩比大选择哪个座位压缩 参数
     *
     * @param fileName
     * @param destFileName
     * @param width
     * @param height
     * @throws Exception
     */
    public static String createZoomImageWithSmart(String fileName,
                                                  String destFileName, int width, int height) throws Exception {

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

//		
        System.out.println("RATIO :" + ratio);
        int realWidth = (int) (sourceWidth / ratio);
        int realHeight = (int) (sourceHeight / ratio);


        String size = createZoomSizeImageNoExchangeWH(fileName, destFileName, realWidth, realHeight);

        return size;
    }

    public static String createZoomImageSwitch(String type, String fileName,
                                               String destFileName, int width, int height) throws Exception {

        String size = "";
        if (type.equals("fix_width")) {
            size = createZoomImageWithFixWith(fileName, destFileName, width, height);

        } else if (type.equals("fix_height")) {

            size = createZoomImageWithFixHeight(fileName, destFileName, width, height);

        } else if (type.equals("fix_smart")) {

            size = createZoomImageWithSmart(fileName, destFileName, width, height);

        } else if (type.equals("scale")) {

            size = createZoomSizeImageNoExchangeWH(fileName, destFileName, width, height);

        } else if (type.equals("cut")) {

            size = ImageUtils.smartCut(fileName, destFileName, 0, 0, width, height);

        }
//		Image image = ImageIO.read(new File(destFileName));
//		int sourceWidth = image.getWidth(null);
//		int sourceHeight = image.getHeight(null);
//		
        return size;
    }

    /**
     * 获取图片的宽高信息
     * @param fileName
     * @return
     */
    public static String getImageInfo(String fileName){

        File image = new File(fileName);
        String withAndHeight = null;
        try
        {
            FileInputStream stream = new FileInputStream(image);
            BufferedImage  buffer = ImageIO.read(stream);
            Integer width = buffer.getWidth();
            Integer height = buffer.getHeight();

            return  ""+width+"x"+height;


        } catch (Exception e)
        {
            e.printStackTrace();
            Console.info(ImageUtil2.class, "读取文件出错！！！");
        }


        return null;
    }

    /**
     * 获取图片的宽高信息
     * @param fileName
     * @return
     */
    public static String getImageInfo2(String fileName){

        File image = new File(fileName);
        String withAndHeight = null;
        try
        {
//            FileInputStream stream = new FileInputStream(image);
            BufferedImage  buffer = ImageIO.read(image);
            Integer width = buffer.getWidth();
            Integer height = buffer.getHeight();
            return width +"_"+height;

        } catch (Exception e)
        {
            e.printStackTrace();
            Console.info(ImageUtil2.class, "读取文件出错！！！");
        }finally {

        }


        return null;
    }
/**
     * 获取图片的宽高信息
     * @param fileName
     * @return
     */
    public static PictureParams getImageInfo3(String fileName){

        File image = new File(fileName);
        String withAndHeight = null;
        try
        {
//            FileInputStream stream = new FileInputStream(image);
            BufferedImage  buffer = ImageIO.read(image);
            Integer width = buffer.getWidth();
            Integer height = buffer.getHeight();
            return  new PictureParams.Builder(width,height).build();

        } catch (Exception e)
        {
            e.printStackTrace();
            Console.info(ImageUtil2.class, "读取文件出错！！！");
        }finally {

        }


        return null;
    }

    public static void main(String args[]) throws Exception {

        ImageUtil2.compressImageByWriter("D:\\temp\\image/aa.jpg", "D:\\temp\\image/ccc345ccccc9.jpg", 0.8F);

//        ImageUtil2.compressImage("D:\\temp\\image/aa.jpg", "D:\\temp\\image/bbbbbbbbb9.jpg", 0.8F);
//		ImageUtil2.createZoomSizeImageNoExchangeWH("D:/picture/old/desert.jpg", "D:/picture/old/new/desert1.jpg", 1024*2,768*2);
//		ImageUtil2.convertFormat("D:\\temp\\image/aa.jpg", "webp", "D:\\temp\\image/AAAAAAAAA.webp");
//		System.out.println(826/31.4);
//

//        convertFormat
//		System.out.println(""+ ImageUtil2.getFormatName("C:/Users/xxx/Pictures/13.png"));
//		System.out.println(""+ ImageUtil2.getFormatName("C:/Users/xxx/Pictures/12.bmp"));

//        System.out.println(StringUtil.getRight("hsalkdjf.jpg.phg.jjj","."));
    }
}

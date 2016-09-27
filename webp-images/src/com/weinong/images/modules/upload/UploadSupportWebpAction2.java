package com.weinong.images.modules.upload;

import com.weinong.base.ApiResult;
import com.weinong.base.BaseApiAction;
import com.weinong.images.Conf;
import com.weinong.images.ImageConsts;
import com.weinong.images.WebpBodyFormServer;
import com.weinong.images.core.ConstsForStatus;
import com.weinong.images.util.ImageUtil2;
import com.weinong.images.util.ImageUtils;
import com.weinong.images.util.WebpUtil;
import yao.util.file.FileUtil;
import yao.util.log.Console;
import yao.util.md5.MD5Util;
import yao.util.string.StringUtil;
import yao.util.web.UrlParamAssayer;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


/**
 * Created by xxx on 2016/1/20.
 */
public class UploadSupportWebpAction2 extends BaseApiAction {


    String sign;
    String app;
    Meta meta;
    WaterMark waterMark;
    List<Thumbnail> thumbnails;
    String format;

    Map<String, WebpBodyFormServer> resultBody = new HashMap<String, WebpBodyFormServer>();//返回的参数


    @Override
    protected void registResult(Map<Integer, String> map) {
        map.put(1, "上传成功");
        map.put(0, "系统异常");
        map.put(-1, "缺失sign参数");
        map.put(-2, "缺失app参数");
        map.put(-3, "缺失meta参数");
        map.put(-4, "缺失meta.md5参数");
        map.put(-5, "缺失meta.filename参数");

        map.put(-8, "文件内容超出5m");
        map.put(-9, "文件内容为空");

        map.put(-11, "应用编号不存在");
        map.put(-21, "签名错误");
        map.put(-22, "文件md5错误");
        map.put(-23, "文件名错误");
        map.put(-31, "水印内容长度不正确");
        map.put(-32, "水印坐标格式不规范");
        map.put(-33, "水印大小不在规定范围内");
        map.put(-41, "缩略图类型不正确");
        map.put(-42, "缩略图大小不正确");
        map.put(-43, "缩略图质量不正确");
        map.put(-43, "缩略图质量不正确");
        // map.put(-45, "水印内容为空"); // 根据需求自己加上去的，需要与老大核对
    }

    @Override
    public ApiResult doApi() throws Exception {
        ApiResult apiResult = getAndCheckParameterForImage();
        if (apiResult.getCode() != ConstsForStatus.SUCCESS) {
            return apiResult;
        }

        Console.info(this, "header is analysised and  the filename from the customer is : " + meta.fileName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 获取当前时间，座位存储图片的最后一级目录
        Date now = new Date();
        String date = dateFormat.format(now);
        String basePath = Conf.image_dir + "/" + "upimages" + "/" + app + "/" + date + "/";
        String sourceFilePath = basePath + meta.getMd5ForFile() +"original" + FileUtil.getSuffix(meta.getFileName());//此处为了保存原图增加了MD5之前原图会被覆盖
        String whole_group = getWholeGoup(thumbnails.size());


        ApiResult e = receiveImage(sourceFilePath);
        if (e != null) return e;
        //开始处理
        String imageWandH = ImageUtil2.getImageInfo2(sourceFilePath);//源图片的宽高信息"79_90"
        Console.info(this, "原图的宽高信息为 ： " + imageWandH);
        List<String> imagefilesforWater = new ArrayList<>();
        String[] thumb_source_files = createFiles(basePath, meta.getMd5ForFile()+"original", ".jpg", 0, thumbnails.size());//每个thumbnail生成webp所需要的原图，增加了MD5,避免被覆盖
        String[] thumb_jpg_files = createTargetFiles(basePath, whole_group, ".jpg", 0, thumbnails.size(), imageWandH);
        String[] thumb_webp_files = createWebpFiles(thumb_jpg_files);
        if (format.equals(ImageConsts.FormatConsts.need_webp)) {
            createBothImagesDependThumbnail(imagefilesforWater, sourceFilePath, thumb_source_files, thumb_jpg_files, thumb_webp_files, imageWandH);
            File lastWebp = new File(thumb_webp_files[thumb_webp_files.length - 1]);
//            while (!lastWebp.exists()) {
//                Thread.sleep(100);
//            }
            deleteThumbinalSourceFiles(thumb_source_files);

        } else {
            creatJpgImageOnly(imagefilesforWater, sourceFilePath, thumb_jpg_files, imageWandH);
        }
        if (waterMark != null) {

            pressWaterMark(imagefilesforWater);
        }

        return result_success(1, resultBody);
    }

    private void deleteThumbinalSourceFiles(String[] thumb_source_files) {

        for (String path : thumb_source_files) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void pressWaterMark(List<String> imagefilesforWater) {

        for (String path : imagefilesforWater) {

            int red = Conf.red;
            int green = Conf.green;
            int blue = Conf.blue;
            Color color = new Color(red, green, blue);

            String text = waterMark.getTextForWaterMark(); // 判断水印内容是否为空
            ImageUtils.pressText3(text, path, path, "宋体", Font.BOLD, color, waterMark.getSize(), waterMark.getCoordinateX(), waterMark.getCoordinateY(), 0.9f);
            Console.info(this, "添加水印完成");
        }
    }



    private void creatJpgImageOnly(List<String> imagefilesforWater, String originalFile, String[] thumb_jpg_files , String imageSize) throws Exception {

        int times = 0;
        for (Thumbnail oneThumbnail : thumbnails) {// 判断要生成几张缩略图，

            WebpBodyFormServer body = new WebpBodyFormServer();
//            String thumbnail_source = thumb_source_files[times];
            String targetPathJpg = thumb_jpg_files[times];
            String url = null;
            imagefilesforWater.add(targetPathJpg);
            if (oneThumbnail.getSize().equals("normal")) { // 因为size参数很重要，如果size参数没有的话，那么type参数就不起作用了，只进行压缩//

                ImageUtil2.compressImage(originalFile, targetPathJpg, oneThumbnail.getQuality() / 100.0f);
                if (!WebpUtil.compareSize(originalFile, targetPathJpg)) {
                    WebpUtil.copyFile(originalFile, targetPathJpg);
                }
                url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

                body.setSourcename(meta.getFileName());
                body.setSize(imageSize);
                body.setUrl(url);
                resultBody.put("thumbnail" + times, body);
            } else {

                String size = ImageUtil2.createZoomImageSwitch(oneThumbnail.getType(), originalFile, targetPathJpg,
                        Integer.parseInt(StringUtil.getLeft(oneThumbnail.getSize(), "*")), Integer.parseInt(StringUtil.getRight(oneThumbnail.getSize(), "*")));
                ImageUtil2.compressImage(targetPathJpg, targetPathJpg, oneThumbnail.getQuality() / 100.0f);
                if (!WebpUtil.compareSize(originalFile, targetPathJpg)) {
                    WebpUtil.copyFile(originalFile, targetPathJpg);
                }
                url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

                body.setSourcename(meta.getFileName());
                body.setSize(imageSize);
                body.setUrl(url);
                resultBody.put("thumbnail" + times, body);
            }
            times++;
        }

    }



    private String[] createWebpFiles(String[] thumb_jpg_files) {

        List<String> weppaths = new ArrayList<>();
        for (String jpgPath : thumb_jpg_files) {
            weppaths.add(jpgPath + ".webp");
            Console.info(this, "webp path is : " + jpgPath + ".webp");
        }
        return weppaths.toArray(new String[thumb_jpg_files.length]);
    }

    private String[] createTargetFiles(String basePath, String whole_group, String fileSuff, int startmark, int endmark, String originalWH) {

        List<String> filesList = new ArrayList<>();

        for (; startmark < endmark; startmark++) {
            String sizeInfo = null;
            if (thumbnails.get(startmark).getSize().equals(ImageConsts.ThumbnailConsts.size_no_scal)) {
                sizeInfo = originalWH;
            } else {
                sizeInfo = thumbnails.get(startmark).getSize().replace("*", "_");
            }
            String path = basePath + meta.getMd5ForFile() + "_"+1+"_" + whole_group + "_" + startmark + "_" + sizeInfo + "_" + format + fileSuff;//图片定为第一版
            Console.info(this, "生成的图片的路径为 ：" + path);
            filesList.add(path);
        }
        return filesList.toArray(new String[filesList.size()]);

    }



    private void createBothImagesDependThumbnail(List<String> imagefilesforWater, String originalFile,
                                                 String[] thumb_source_files, String[] thumb_jpg_files, String[] thumb_webp_files,
                                                 String imageSize) throws Exception {

        int times = 0;
        for (Thumbnail oneThumbnail : thumbnails) {// 判断要生成几张缩略图，

            WebpBodyFormServer body = new WebpBodyFormServer();
            String thumbnail_source = thumb_source_files[times];
            String targetPathJpg = thumb_jpg_files[times];
            String targetPathWebp = thumb_webp_files[times];
            String url = null;
            String webp_url = null;

            imagefilesforWater.add(targetPathJpg);
            imagefilesforWater.add(targetPathWebp);
            if (oneThumbnail.getSize().equals("normal")) { // 因为size参数很重要，如果size参数没有的话，那么type参数就不起作用了，只进行压缩//

                ImageUtil2.compressImageByWriter(originalFile, thumbnail_source, 1.0f);
                ImageUtil2.compressImageByWriter(thumbnail_source, targetPathJpg, oneThumbnail.getQuality() / 100.0f);
                if (!WebpUtil.compareSize(originalFile, targetPathJpg)) {
                    File target_image = new File(targetPathJpg);
                    target_image.delete();//先删除老的
                    targetPathJpg = targetPathJpg.replace("1.jpg", "0.jpg");
                    WebpUtil.copyFile(originalFile, targetPathJpg);
                } else {

                    WebpUtil.getWebp(thumbnail_source, targetPathWebp, oneThumbnail.getQuality()/ 100.0f);
                    webp_url = Conf.image_http_page + StringUtil.getRightOuter(targetPathWebp, Conf.imgee_dir_spilt);
                }
                url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

                body.setSourcename(meta.getFileName());
                body.setSize(imageSize);
                body.setUrl(url);
                body.setWebp_url(webp_url);
                resultBody.put("thumbnail" + times, body);
            } else {

                String size = ImageUtil2.createZoomImageSwitch(oneThumbnail.getType(), originalFile, thumbnail_source,
                        Integer.parseInt(StringUtil.getLeft(oneThumbnail.getSize(), "*")), Integer.parseInt(StringUtil.getRight(oneThumbnail.getSize(), "*")));
                ImageUtil2.compressImage(thumbnail_source, targetPathJpg, oneThumbnail.getQuality() / 100.0f);

                if (!WebpUtil.compareSize(thumbnail_source, targetPathJpg)) {
                    File target_image = new File(targetPathJpg);
                    target_image.delete();//先删除老的
                    targetPathJpg = targetPathJpg.replace("1.jpg", "0.jpg");
                    WebpUtil.copyFile(thumbnail_source, targetPathJpg);
                } else {

                    WebpUtil.getWebp(thumbnail_source, targetPathWebp, oneThumbnail.getQuality()/ 100.0f);
                    webp_url = Conf.image_http_page + StringUtil.getRightOuter(targetPathWebp, Conf.imgee_dir_spilt);
                }
                url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

                body.setSourcename(meta.getFileName());
                body.setSize(imageSize);
                body.setUrl(url);
                body.setWebp_url(webp_url);
                resultBody.put("thumbnail" + times, body);
            }
            times++;
        }
    }

    private String[] createFiles(String basePath, String fileMark, String fileSuff, int startmark, int endmark) {

        List<String> filesList = new ArrayList<>();

        for (; startmark < endmark; startmark++) {
            String path = basePath + fileMark + "_" + startmark + fileSuff;
            filesList.add(path);
        }
        return filesList.toArray(new String[filesList.size()]);

    }

    private ApiResult receiveImage(String filePath) {

        BufferedInputStream bfInput = null;
        try {
            bfInput = new BufferedInputStream(request.getInputStream());

            byte buffer[] = new byte[1024]; // 定义接受缓冲数组的大小为1Kb

            int len = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len = bfInput.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            int imageContentSize = baos.size();
            if (imageContentSize == 0) {
                return result_fail(ConstsForStatus.UNFOUND_CONTENT);
            }

            System.out.println("读取的上传的流的内容的大小 ： " + imageContentSize);
            if (baos.size() > 5 * 1024 * 1024) {
                return result_fail(ConstsForStatus.TOO_LARGE_FILE);
            }

            byte[] bs = baos.toByteArray();
            if (!meta.md5ForFile.equalsIgnoreCase(MD5Util.getMD5FromBytes(bs))) {
                Console.info(this, "这里是文件的MD5  :  " + MD5Util.getMD5FromBytes(bs));
                return result_fail(ConstsForStatus.WRONG_META_MD5);
            }

            File file = new File(filePath);
            if (!file.getParentFile().exists()) {

                file.getParentFile().mkdirs();
            }

            FileUtil.writeFile(file, bs);
            Console.debug(this, "复制文件完毕,这里是源文件的存储地址 ： " + file);
        } catch (Exception e) {
            Console.error(this, e.getMessage(), e);
            return result_fail(0, e.getMessage());

        }
        return null;
    }




    private String getWholeGoup(int whole_times) {

        StringBuffer str = new StringBuffer();

        for (int i = 0; i < whole_times; i++) {
            str.append("" + i);
        }
        return str.toString();
    }

    /**
     * 获取参数，并且判断参数是否合法，如果不合法，返回解析信息对应的状态码
     *
     * @return
     */
    public ApiResult getAndCheckParameterForImage() {

        this.sign = request.getHeader("sign");
        this.app = request.getHeader("app");
        this.meta = getMetaFromHeader();
        this.thumbnails = getThumbnialFromHeader();
        this.waterMark = getWaterMarkFromHeader();
        this.format = request.getHeader("format");

        if (StringUtil.isTrimEmpty(sign)) {
            return result_fail(ConstsForStatus.UNFOUND_SIGN);
        }

        if (StringUtil.isTrimEmpty(app)) {
            return result_fail(ConstsForStatus.UNFOUND_APP);
        }

        if (Meta.check(meta) != ConstsForStatus.SUCCESS) {
            return result_fail(Meta.check(meta));
        }

        String secret = Conf.getSecret(app); // 验证md5参数是否正确 后期需要打开
        if (StringUtil.isTrimEmpty(secret)) {
            return result_fail(ConstsForStatus.WRONG_APP);
        }
        String md5ForSign = MD5Util.getMD5(meta.getMd5ForFile() + secret);
        Console.info(this ,"这里是签名：" + md5ForSign);
        if (!sign.equals(md5ForSign)) {
            return result_fail(ConstsForStatus.WRONG_SIGN);
        }

        for (Thumbnail oneThumbnail : thumbnails) {
            if (Thumbnail.check(oneThumbnail) != ConstsForStatus.SUCCESS) {
                return result_fail(Thumbnail.check(oneThumbnail));
            }
        }

        if (WaterMark.check(waterMark) != ConstsForStatus.SUCCESS) {
            return result_fail(WaterMark.check(waterMark));
        }

        if (null == format || "".equals(format)) {
            format = ImageConsts.FormatConsts.default_format;
        }

        return result_success(ConstsForStatus.SUCCESS);
    }

    /**
     * 从header里面取出的数据是一个get请求的字符串格式，
     * 例如md5=37ee6d5e1b004bb27766f8ba95f686f7&length=5216 需要对其中的参数进行提取
     * 调用了URL的解析工具，但是必须要有"？",所以构造参数传入的时候添加了"?" 对于可有可无的参数，设置成默认值。对于必须有的参数不做处理
     *
     * @return
     */
    public List<Thumbnail> getThumbnialFromHeader() {

        List<Thumbnail> thumbnailList = new ArrayList<Thumbnail>();

        String[] thumbnailInHeader = new String[4];
        for (int i = 0; i < 4; i++) {
            thumbnailInHeader[i] = request.getHeader("thumbnail" + i);
            Thumbnail thumbnail = new Thumbnail();
            if (null != thumbnailInHeader[i] && !("".equals(thumbnailInHeader[i]))) {
                UrlParamAssayer urlParameter = new UrlParamAssayer("?" + thumbnailInHeader[i], "utf-8");
                thumbnail.setType(StringUtil.isTrimEmpty(urlParameter.getParam("type")) ? "fix_width" : urlParameter.getParam("type"));
                thumbnail.setSize(StringUtil.isTrimEmpty(urlParameter.getParam("size")) ? ImageConsts.ThumbnailConsts.size_no_scal : urlParameter.getParam("size"));
                thumbnail.setQuality(StringUtil.isTrimEmpty(urlParameter.getParam("quality")) ? ImageConsts.ThumbnailConsts.default_quality : Integer.parseInt(urlParameter.getParam("quality")));
                Console.info(this,"图片的尺寸是 ："+thumbnail.getSize());
                thumbnailList.add(thumbnail);
            } else {
                break; // 获取为空的话就可以跳出循环了
            }
        }
        if (thumbnailList.size() == 0) {
            //图片不进行压缩处理
            Thumbnail thumbnail = new Thumbnail();
            thumbnail.setType(ImageConsts.ThumbnailConsts.type_default_scale);
            thumbnail.setSize(ImageConsts.ThumbnailConsts.size_no_scal);
            thumbnail.setQuality(ImageConsts.ThumbnailConsts.default_quality);
            thumbnailList.add(thumbnail);
        }
        return thumbnailList;
    }

    /**
     * 从header里面取出的数据是一个get请求的字符串格式， 例如watermark:
     * text=wn518.com&coordinate=-40,-20&size=12 需要对其中的参数进行提取,,
     * 调用了URL的解析工具，但是必须要有"？",所以构造参数传入的时候添加了"?" 对于可有可无的参数，设置成默认值。对于必须有的参数不做处理
     *
     * @return
     */
    public WaterMark getWaterMarkFromHeader() {

        WaterMark waterMark = new WaterMark();
        String markStr = request.getHeader("watermark");
        if (StringUtil.isTrimEmpty(markStr)) {
            return null;
        }

        UrlParamAssayer urlParameter = new UrlParamAssayer("a?" + markStr, "utf-8");

        waterMark.setTextForWaterMark(urlParameter.getParam("text"));
        waterMark.setSize(StringUtil.isTrimEmpty(urlParameter.getParam("size")) ? ImageConsts.WaterMarkConsts.default_font_size : Integer.parseInt(urlParameter.getParam("size")));
        waterMark.setCoordinate(StringUtil.isTrimEmpty(urlParameter.getParam("coordinate")) ? ImageConsts.WaterMarkConsts.default_xy : urlParameter.getParam("coordinate"));

        return waterMark;
    }

    /**
     * 从header里面解析文件名，和文件内容的md5,是属于必须的参数
     *
     * @return
     */
    public Meta getMetaFromHeader() {

        Meta meta = new Meta();
        String metaStr = request.getHeader("meta");
        if (StringUtil.isTrimEmpty(metaStr)) {
            return null;
        }
        UrlParamAssayer urlParameter = new UrlParamAssayer("a?" + metaStr, "utf-8");//为了构造成URL的形式，采用工具进行解码
        meta.setMd5ForFile(urlParameter.getParam("md5"));
        meta.setFileName(urlParameter.getParam("filename"));

        return meta;
    }


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static void main(String srgs[]) throws IOException {

        // String str = "md5=37ee6d5e1b004bb27766f8ba95f686f7&length=5216";
        // String[] array = str.split("&");
        // System.out.println(array[0]);
        // System.out.println(array[1]);
        // File file = new File("D:/index/_1.cfe");
        // if(file.exists()){
        // System.out.println("true");
        // }
        // String md5 = getFileMD5(file);
        // System.out.println(md5);
        // File file = new File("D:\\min", "hkj");
        // System.out.println(file.mkdirs());
        // if (!file.exists()){
        // System.out.println(file.createNewFile());
        // }
        // System.out.println(file);

        // 创建文件
        // File f = new File("d:/io"); //创建文件的方法,
        // f.mkdirs();
        // File f = new File("d:/io/test.txt");
        // System.out.println(f.createNewFile());
        //

        // 创建日期
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 获取当前时间，座位存储图片的最后一级目录
//        Date now = new Date();
//        String date = dateFormat.format(now);
//        String date1 = dateFormat.format(now);
//        System.out.println(date1);

//         测试URL解析的情况 没有问题
        UrlParamAssayer urlParameter = new UrlParamAssayer("?"
                + "type=fix_width&size=180*180&quality=80", "utf-8");
        System.out.println(urlParameter.getParam("type"));
        System.out.println(urlParameter.getParam("size"));
        System.out.println(urlParameter.getParam("quality" +
                ""));
        //
        // 测试StringUtil类解析空字符串；有没有可能跑异常，为null的时候有异常，为“”的时候不会抛异常
        //
        // String str ="";
        // System.out.println(StringUtil.getLeft(str, ";"));
        //
        // 测试能否正确获取文件的后缀ok
        // System.out.println(FileUtil.getSuffix("file.kl()"));
//        System.out.println(FileUtil.getSuffix("name.nam"));
    }


/*
    private void creatJpgImageOnly(List<String> imagefilesforWater, String originalFile, String[] thumb_source_files, String[] thumb_jpg_files, String[] thumb_webp_files, String imageSize) throws Exception {

        int times = 0;
        for (Thumbnail oneThumbnail : thumbnails) {// 判断要生成几张缩略图，

            WebpBodyFormServer body = new WebpBodyFormServer();
            String thumbnail_source = thumb_source_files[times];
            String targetPathJpg = thumb_jpg_files[times];
            String url = null;
            imagefilesforWater.add(targetPathJpg);
            if (oneThumbnail.getSize().equals("normal")) { // 因为size参数很重要，如果size参数没有的话，那么type参数就不起作用了，只进行压缩//

                ImageUtil2.compressImage(originalFile, targetPathJpg, oneThumbnail.getQuality() / 100.0f);
                if (!WebpUtil.compareSize(originalFile, targetPathJpg)) {
                    WebpUtil.copyFile(originalFile, targetPathJpg);
                }
                url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

                body.setSourcename(meta.getFileName());
                body.setSize(imageSize);
                body.setUrl(url);
                resultBody.put("thumbnail" + times, body);
            } else {

                String size = ImageUtil2.createZoomImageSwitch(oneThumbnail.getType(), originalFile, thumbnail_source,
                        Integer.parseInt(StringUtil.getLeft(oneThumbnail.getSize(), "*")), Integer.parseInt(StringUtil.getRight(oneThumbnail.getSize(), "*")));
                ImageUtil2.compressImage(thumbnail_source, targetPathJpg, oneThumbnail.getQuality() / 100.0f);

                if (!WebpUtil.compareSize(originalFile, targetPathJpg)) {
                    WebpUtil.copyFile(originalFile, targetPathJpg);
                }
                url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

                body.setSourcename(meta.getFileName());
                body.setSize(imageSize);
                body.setUrl(url);
                resultBody.put("thumbnail" + times, body);
            }
            times++;
        }

    }
*/


}

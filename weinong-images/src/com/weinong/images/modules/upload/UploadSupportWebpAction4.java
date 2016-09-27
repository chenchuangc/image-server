package com.weinong.images.modules.upload;

import com.weinong.base.ApiDefined;
import com.weinong.base.ApiResult;
import com.weinong.base.BaseApiAction;
import com.weinong.images.Conf;
import com.weinong.images.ImageConsts;
import com.weinong.images.WebpBodyFormServer;
import com.weinong.images.base.PictureParams;
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
 * Created by xxx on 2016/2/23.
 */
@ApiDefined(label = "支持webp上传" , description = "当上传的图片最高档质量压缩后比原来的图要大的情况下不再生成webp格式的图片")

public class UploadSupportWebpAction4 extends BaseApiAction {


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
        String sourceFilePath = basePath + meta.getMd5ForFile() +"original_" +meta.getFileName();//此处为了保存原图增加了MD5之前原图会被覆盖
        String whole_group = getWholeGoup(thumbnails.size());


        ApiResult e = receiveImage(sourceFilePath);
        if (e != null) return e;
        //开始处理
        PictureParams pictureParams = ImageUtil2.getImageInfo3(sourceFilePath);//获取图片的真实宽高信息
        String imageWandH = pictureParams.getWidth() + "_" + pictureParams.getHeight();//源图片的宽高信息"79_90"
        Console.info(this, "原图的宽高信息为 ： " + imageWandH);
        initThumbnailSize(sourceFilePath, pictureParams);
//        if (waterMark.isHaveWaterMark()) {
//
//        }

        String testDir = basePath + meta.getMd5ForFile() + "test.jpg";//测试是否生成webp的文件目录
        if (format.equals(ImageConsts.FormatConsts.need_webp)) {
            //如果是已经压缩过的图片进行压缩，若生成的压缩图片比原来的大，则不再生成webp图片，JPG图片也采用原来图片的大小
            format = testNeedWebp(sourceFilePath,testDir);
        }
        String[] thumb_jpg_files = createTargetFiles(basePath, whole_group, ".jpg", 0, thumbnails.size(), imageWandH);
        String[] thumb_webp_files = createWebpFiles(thumb_jpg_files);


        if (format.equals(ImageConsts.FormatConsts.need_webp)) {
            if (waterMark.isHaveWaterMark()) {
                createBothImagesDependThumbnail( sourceFilePath, thumb_jpg_files, thumb_webp_files, imageWandH, true);
            }else {
                createBothImagesDependThumbnail( sourceFilePath, thumb_jpg_files, thumb_webp_files, imageWandH, false);
            }

        }else {
            if (waterMark.isHaveWaterMark()) {
                creatJpgImageOnly( sourceFilePath, thumb_jpg_files, imageWandH, true);
            }else {
                creatJpgImageOnly( sourceFilePath, thumb_jpg_files, imageWandH, false);
            }

        }
       /* if (waterMark != null) {

            pressWaterMark(imagefilesforWater);
        }*/

        return result_success(1, resultBody);
    }

    private void initThumbnailSize(String original_image,PictureParams pictureParams) throws IOException {
        for (Thumbnail thumbnail : thumbnails) {


            if (thumbnail.getSize().equals(ImageConsts.ThumbnailConsts.size_no_scal)) {
                thumbnail.setSize(pictureParams.getWidth() + "*" + pictureParams.getHeight());
            }else {
                PictureParams params = WebpUtil.getDestSizeWithDiffScaleWay(thumbnail.getType() ,original_image , thumbnail.getWidth(), thumbnail.getHeight());
            }
        }
    }

    private String testNeedWebp(String sourceFilePath, String targetPathJpg) throws IOException {
        int maxQulityThumbnailIndex = thumbnails.size()-1;
        WebpUtil.scalePartly(sourceFilePath,targetPathJpg,thumbnails.get(maxQulityThumbnailIndex).getWidth(), thumbnails.get(maxQulityThumbnailIndex).getHeight(), waterMark, thumbnails.get(maxQulityThumbnailIndex).getQuality() /100.0f ,false);//生成jpg
//        ImageUtil2.compressImageByWriter(sourceFilePath, targetPathJpg, thumbnails.get(maxQulityThumbnailIndex).getQuality() / 100.0f);
        boolean no_webp = !WebpUtil.compareSize(sourceFilePath, targetPathJpg);
        File target_image = new File(targetPathJpg);
        target_image.delete();//先删除老的
        if (no_webp) {
            Console.info(this , "图片 : "+sourceFilePath+" 已压缩，无法生成webp图片！！");
            return ImageConsts.FormatConsts.no_webp;
        }
        return ImageConsts.FormatConsts.need_webp;
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



    private void creatJpgImageOnly( String originalFile, String[] thumb_jpg_files , String imageSize, boolean havaWaterMark) throws Exception {

        int times = 0;
        for (Thumbnail oneThumbnail : thumbnails) {// 判断要生成几张缩略图，

            WebpBodyFormServer body = new WebpBodyFormServer();
            String targetPathJpg = thumb_jpg_files[times];
            String url = null;

//            PictureParams params = WebpUtil.getDestSizeWithDiffScaleWay(oneThumbnail.getType(),originalFile,oneThumbnail.getWidth(),oneThumbnail.getHeight());
            if (havaWaterMark) {
                //如果是带有水印的图片，图片已经被压缩的质量过大会影响生成的带有水印的图，并且无法用上传的原图进行替代，只能采用实验法
                WebpUtil.scalePartlyWithWaterMark(originalFile,targetPathJpg,oneThumbnail.getWidth(), oneThumbnail.getHeight(), waterMark, oneThumbnail.getQuality()/100.0f ,false);//生成jpg
            }else{
                WebpUtil.scalePartly(originalFile,targetPathJpg,oneThumbnail.getWidth(), oneThumbnail.getHeight(), waterMark, oneThumbnail.getQuality()/100.0f ,false);//生成jpg
                if (!WebpUtil.compareSize(originalFile, targetPathJpg)) {
                    WebpUtil.copyFile(originalFile, targetPathJpg);
                }
            }

            url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

            body.setSourcename(meta.getFileName());
            body.setSize(imageSize);
            body.setUrl(url);
            resultBody.put("thumbnail" + times, body);

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



    private void createBothImagesDependThumbnail(String sourceFile,
                                                  String[] thumb_jpg_files, String[] thumb_webp_files,
                                                 String imageSize, boolean havaWaterMark) throws Exception {

        int times = 0;
        for (Thumbnail oneThumbnail : thumbnails) {// 判断要生成几张缩略图，

            WebpBodyFormServer body = new WebpBodyFormServer();
            String originalFile = sourceFile;
            String targetPathJpg = thumb_jpg_files[times];
            String targetPathWebp = thumb_webp_files[times];
            String midBridgeFileForCut = originalFile.replace("original", "cutmiddle")+".jpg";
            String url = null;
            String webp_url = null;

            if (oneThumbnail.getType().equals("cut")) {
//                String midBridgeFile =  ;
                WebpUtil.cutjpg(originalFile, midBridgeFileForCut , oneThumbnail );
                originalFile = midBridgeFileForCut;
            }
//            PictureParams params = WebpUtil.getDestSizeWithDiffScaleWay(oneThumbnail.getType(),originalFile,oneThumbnail.getWidth(),oneThumbnail.getHeight());
            if (havaWaterMark) {
                WebpUtil.scalePartlyWithWaterMark(originalFile,targetPathJpg,oneThumbnail.getWidth(), oneThumbnail.getHeight(), waterMark, oneThumbnail.getQuality()/100.0f ,false);//生成jpg
                WebpUtil.scalePartlyWithWaterMark(originalFile,targetPathWebp,oneThumbnail.getWidth(), oneThumbnail.getHeight(), waterMark, oneThumbnail.getQuality()/100.0f ,true);//生成webp
            }else{
                WebpUtil.scalePartly(originalFile,targetPathJpg,oneThumbnail.getWidth(), oneThumbnail.getHeight(), waterMark, oneThumbnail.getQuality()/100.0f ,false);//生成jpg
                WebpUtil.scalePartly(originalFile,targetPathWebp,oneThumbnail.getWidth(), oneThumbnail.getHeight(), waterMark, oneThumbnail.getQuality()/100.0f ,true);//生成webp
            }

            webp_url = Conf.image_http_page + StringUtil.getRightOuter(targetPathWebp, Conf.imgee_dir_spilt);
            url = Conf.image_http_page + StringUtil.getRightOuter(targetPathJpg, Conf.imgee_dir_spilt);

            body.setSourcename(meta.getFileName());
            body.setSize(imageSize);
            body.setUrl(url);
            body.setWebp_url(webp_url);
            resultBody.put("thumbnail" + times, body);
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
            waterMark.setHaveWaterMark(false);
            return waterMark;
        }

        UrlParamAssayer urlParameter = new UrlParamAssayer("a?" + markStr, "utf-8");
//        Console.info(this, "水印参数是 ： " +urlParameter);

        waterMark.setTextForWaterMark( urlParameter.getParam("text"));
        Console.info(this, "水印参数是 ： " + waterMark.getTextForWaterMark());
        waterMark.setSize(StringUtil.isTrimEmpty(urlParameter.getParam("size")) ? ImageConsts.WaterMarkConsts.default_font_size : Integer.parseInt(urlParameter.getParam("size")));
        waterMark.setCoordinate(StringUtil.isTrimEmpty(urlParameter.getParam("coordinate")) ? ImageConsts.WaterMarkConsts.default_xy : urlParameter.getParam("coordinate"));
        waterMark.setHaveWaterMark(true);
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
}

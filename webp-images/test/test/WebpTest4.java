package test;

import yao.config.tool.SCM;
import yao.util.date.DateUtil;
import yao.util.header.Header;
import yao.util.http.HTTPUtil;
import yao.util.md5.MD5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xxx on 2016/2/24.
 */
public class WebpTest4 {

    public static void main(String args[]) throws Exception{

        SCM.init();
//        final boolean debug = BaseConf.debug;

        String dir = "D:/temp/image";
//		String file = "D:/temp/image/11.jpg";
        File[] fileNotDir = getFiles(dir);

//        boolean haveWaterMark = false;
        boolean haveWaterMark = true;
        boolean haveThumbnail = true;
//        boolean haveThumbnail = false;
        boolean haveWebpFormat = true;
//        boolean haveWebpFormat = false;


//        String url = "http://182.92.217.53:8680/upload_webp_images.wn";//正式服务器//
//        String url = "http://192.168.1.106:8008/upload_webp_images.wn";//106测试服务器
//        String url = "http://192.168.1.89:8800/upload_webp_images.wn";//本机测试服务器


//        String url = "http://123.57.85.221:8680/upload_webp_images_j.wn";//正式服务器

//        String url = "http://123.56.98.160:8680/upload_webp_images_j.wn";//正式服务器//pt

//        String url = "http://123.57.94.158:8680/upload_webp_images_j.wn";//wt
//        String url = "http://wt-upload.imgs.wn518.com/upload_webp_images_j.wn";//wt
        String url = "http://192.168.1.106:8008/upload_webp_images_j.wn";//106测试服务器
//
//        String url = "http://192.168.1.109:8008/upload_webp_images_j.wn";//109测试服务器
//        String url = "http://192.168.1.89:8800/upload_webp_images_j.wn";//本机测试服务器

//        Date now  = DateUtil.now();
        for(File everyFile : fileNotDir)
        {
            InputStream inputStream = new FileInputStream(everyFile);
            List<Header> headers = getEachFileHeaders(everyFile);
            if(haveWaterMark)
            {
                setWaterMark(headers);
            }
            if(haveThumbnail)
            {
                setThumbnail(headers);

            }

            if (!haveWebpFormat) {
                Header header = new Header("format", "0");
                headers.add(header);
            }

            System.out.println("-----------------------------");
            Date start  = DateUtil.now();
            System.out.println("开始传输的时间是 ： " + start);
            String result = HTTPUtil.SimpleUTF8.getStringByPost( headers,  url,  inputStream);
            Date end  = DateUtil.now();
            System.out.println("结束传输的时间是 ： " + end);

            System.out.println("经历的时间是 ：" + (end.getTime() - start.getTime()));

            System.out.println(result);
            System.out.println("-----------------------------");
            System.out.println("");
            System.out.println("");


        }


//
//		List<List<Header>> allFileHeaders = getAllFilesHeaders(dir);
//
////
//		List<Param> params= new ArrayList<Param>();
//
//		for(List<Header> headerList : allFileHeaders)
//		{
//
//			 String result =HTTPUtil.SimpleUTF8.getStringByPost( headerList,  url,  inputStream);
//			 System.out.println(result);
//		}



    }

    public static List<List<Header>> getAllFilesHeaders(String dir) throws Exception{

        List<List<Header>> headers =new ArrayList<List<Header>>();
        File[] fileNotDir = getFiles(dir);
        for( File file : fileNotDir){
            headers.add(getEachFileHeaders(file));
        }
        return headers;
    }

    public static File[] getFiles(String dir){

        List<File> filesNotDir = new ArrayList<File>();

        File root = new File(dir);
        File[] files = root.listFiles();
        int listSize = 0;
        for(File file : files)
        {
            if(file.isFile())
            {
                filesNotDir.add(file);
            }
        }
        listSize = 	filesNotDir.size();
        File[] finalFiles =  filesNotDir.toArray(new File[listSize]);

        return finalFiles;
    }

    /*
     * 填充每个文件的header参数 */
    public static List<Header> getEachFileHeaders(File fileNotDir) throws Exception{

        List<Header> headerList = new ArrayList<Header>();

        String sign = null;
        String app = null ;
        String meta = null;
//		String waterMark = null;
//		List<String> thumbNail = new ArrayList<String>();
//

        String  imageMD5 = null;
        String appSecreat = null;
        String fileName = null;
//		System.out.println(meta);



//        app = "jyh-cms";
        app = "ys-sales";

        fileName = fileNotDir.getName();
        imageMD5 = MD5Util.getMD5FromFile(fileNotDir);
        meta = "md5=" + imageMD5 + "&" + "filename=" + URLEncoder.encode(fileName, "UTF-8");

        appSecreat = SCM.getConfig().getString(app);
        sign = MD5Util.getMD5(imageMD5 + appSecreat) ;

        System.out.println("图片的MD5 : "+ imageMD5);
        System.out.println("APPsecreat : " + appSecreat);
        System.out.println("here print the filename and  its url encoder string  : " + fileName + "  :  "+  URLEncoder.encode(fileName, "UTF-8") );
        System.out.println("here is getEachFileheader : sign = " + sign);
        System.out.println("here is getEachFileheader : app = " +app);
        System.out.println("here is getEachFileheader : meta = " +meta);//


        headerList.add(new Header("sign",sign));
        headerList.add(new Header("app" , app));
        headerList.add(new Header("meta" , meta));

        return headerList;
    }

    public static void setWaterMark(List<Header> headerList) throws UnsupportedEncodingException {
//        String watermark =" text=wn518.com&coordinate=0,0&size=12";
        String text = "wn518.com我们都有一个家";
//        text = URLEncoder.encode(text, "UTF-8");
//        System.out.println(text);

//        String watermark =" text="+text + "&coordinate=20,20&size=12";
//        String watermark =" text="+text + "&coordinate=5,5&size=12";
        String watermark ="text="+text + "&size=12";
        watermark = URLEncoder.encode(watermark, "UTF-8");
        headerList.add(new Header("watermark" , watermark));
    }

    public static void setThumbnail(List<Header> headerList)
    {

//        String thumbnail0 = "type=scale&size=640*640&quality=20 ";
        String thumbnail0 = "type=cut&size=300*640&quality=20 ";
        String thumbnail1 = "type=scale&size=180*180&quality=40 ";
        String thumbnail2 = "type=scale&size=140*140&quality=45 ";
        String thumbnail3 = "type=scale&size=140*140&quality=90 ";

       /* String thumbnail0 = "quality=20";
        String thumbnail1 = "quality=40";
        String thumbnail2 = "quality=60";
        String thumbnail3 = "quality=99";*/

        headerList.add(new Header("thumbnail0" , thumbnail0));
        headerList.add(new Header("thumbnail1" , thumbnail1));
        headerList.add(new Header("thumbnail2" , thumbnail2));
        headerList.add(new Header("thumbnail3" , thumbnail3));
    }

}

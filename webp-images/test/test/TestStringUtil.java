package test;

import com.weinong.images.WebpBodyFormServer;
import yao.util.json.JSONUtil;
import yao.util.string.StringUtil;
import yao.util.web.UrlParamAssayer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by xxx on 2016/1/20.
 */
public class TestStringUtil {


    public static void main(String[] args) throws UnsupportedEncodingException {

        String text = "wn518.com我们都有一个家";
//        text = URLEncoder.encode(text, "UTF-8");
//        System.out.println(text);

//        String watermark =" text="+text + "&coordinate=20,20&size=12";
//        String watermark =" text="+text + "&coordinate=5,5&size=12";
        String watermark =" text="+text + "&size=12";
        watermark = URLEncoder.encode(watermark, "UTF-8");

        UrlParamAssayer urlParameter = new UrlParamAssayer("a?" + watermark, "utf-8");
        System.out.println(urlParameter.getParam("text"));


    }
}

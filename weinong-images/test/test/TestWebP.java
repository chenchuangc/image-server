package test;

import java.io.File;
import java.io.IOException;

/**
 * Created by xxx on 2016/1/20.
 */
public class TestWebP {


    public static void main(String[] args) throws IOException
    {

        /*Runtime.getRuntime().exec("cwebp -q 100 D:\\softWard\\webpUtilwindows\\libwebp-0.4.4-windows-x86\\bin\\Desert.jpg" +
                " -o D:\\softWard\\webpUtilwindows\\libwebp-0.4.4-windows-x86\\bin\\Desert.webp ");

        Runtime.getRuntime().exec("cwebp -q 100 D:\\softWard\\webpUtilwindows\\libwebp-0.4.4-windows-x86\\bin\\Desert.jpg" +
                " -o D:\\softWard\\webpUtilwindows\\libwebp-0.4.4-windows-x86\\bin\\Desert.webp ");
*/
/*
        creatWebpImage("D:\\softWard\\webpUtilwindows\\libwebp-0.4.4-windows-x86\\bin\\Desert.jpg",
                "D:\\softWard\\webpUtilwindows\\libwebp-0.4.4-windows-x86\\bin\\chenchuangchuang.jpg.webp",100);*/

//        creatWebpImage("D:\\temp\\image/11.png",
//                "D:\\temp\\image/111111111111..jpg.webp",100);

creatWebpImage("D:\\temp\\image/aa.jpg",
                "D:\\temp\\image/111BBBBBBB.jpg.webp",80);


    }


    private static void creatWebpImage(String srcpath, String despath, int quality) throws IOException {

//        File dest = new File(destFileName);
//        if (!dest.getParentFile().exists())
//            dest.getParentFile().mkdirs();
        File imageFolders = new File(despath);
//        File imagePath = new File(despath);
        if (!imageFolders.getParentFile().exists()) {
            System.out.println("开始创建文件夹" + despath);
            imageFolders.getParentFile().mkdirs();
            System.out.println("创建文件夹成功" + despath);
        }

        String mander = "cwebp -q "+quality +" "+ srcpath +
                " -o " + despath;
        System.out.println(mander);
        Runtime.getRuntime().exec(mander);
    }


}

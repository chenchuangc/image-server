package test;

import com.luciad.imageio.webp.WebPImageReaderSpi;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import com.luciad.imageio.webp.WebPReadParam;
import com.luciad.imageio.webp.WebPWriteParam;
//import com.luciad.imageio.webp.WebPWriter;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by xxx on 2016/1/27.
 */
public class TestJarWay {




   /* public static void main(String[] args) {

        File file1 = new File("E:\\my_work\\image_server/111.jpg");
//        File file1 = new File("D://workspace//demo//Test//unnamed.webp");
        File file2 = new File("E:\\my_work\\image_server/222.webp");



//        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("webp-imageio");
        try {
            //FileUtils.copyFile(file1, file2);
            BufferedImage im = ImageIO.read(file1);
            ImageIO.write(im, "png", file2);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/


    public static void main(String[] args) throws IOException {

//        getWebp();
//        getWebp2();
        List<String> list = new ArrayList<>();
        List list3 = list;
        list3.add(5);
        System.out.println(list3.get(0));

        String[] arr = new String[5];
        Object[] arr1 = arr;
        arr1[0]=5;
        System.out.println(arr1[0]);


    }

    private static void getWebp() throws IOException {
        System.loadLibrary("webp-imageio");
        String srcfile = "D:\\temp\\image/aa.jpg";
        String desfile = "D:\\temp\\image/rrrrrrrrrrr1111111111.jpg.webp";
        File src_file = new File(srcfile);
        File des_file = new File(desfile);

        FileImageOutputStream __out = new FileImageOutputStream(des_file);
        BufferedImage image = ImageIO.read(src_file);
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        ImageWriter imageWriter = findWriter(ImageIO.getImageWritersByMIMEType("image/webp"));
//         new WebPReadParam();
        WebPWriteParam webPWriteParam = (WebPWriteParam) imageWriter.getDefaultWriteParam();
        webPWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        webPWriteParam.setCompressionType("Lossy");
        webPWriteParam.setCompressionQuality(0.8f);

//        ImageWriter imageWriter = new ImageWriter(new ImageWriterSpi() );
        imageWriter.setOutput(__out);
        imageWriter.write(null, new IIOImage(image, null, null), webPWriteParam);
        __out.flush();
        __out.close();
        imageWriter.dispose();
    }

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


//        new WebPWriteParam(Locale.getDefault());
//        WebPWriter w = new WebPWriter(new WebPImageWriterSpi());
//        FileImageOutputStream out = new FileImageOutputStream(new File("test.webp"));
//        w.setOutput(out);
//        w.write(i);
//        out.close();


}

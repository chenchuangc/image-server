package com.weinong.images;

import yao.util.constants.ConstantsFieldDefined;
import yao.util.constants.IConstants;

/**
 * Created by xxx on 2016/1/21.
 */
public class ImageConsts {

    public static class WaterMarkConsts implements IConstants {

        @ConstantsFieldDefined(name = "水印内容为空" , description = "没有添加水印的需求")
        public static final String no_watermark = "no_mark";

        @ConstantsFieldDefined(name = "水印默认xy值" , description = "默认的水印x坐标")
        public static final String default_xy = "5,-20";

        @ConstantsFieldDefined(name = "水印默认y" , description = "默认的水印y坐标")
        public static final int default_y = -20;

        @ConstantsFieldDefined(name = "水印文字默认大小" , description = "默认的字号")
        public static final int default_font_size = 12;

        @ConstantsFieldDefined(name = "水印文字默认最大值" , description = "")
        public static final int default_max_font = 16;

        @ConstantsFieldDefined(name = "水印文字默认最小值" , description = "")
        public static final int default_min_font = 6;

    }


    public static class ThumbnailConsts implements IConstants {

        @ConstantsFieldDefined(name = "图片不进行大小的缩放处理", description = "针对size参数的默认值")
        public static final String size_no_scal = "normal";

        @ConstantsFieldDefined(name = "默认的质量压缩处理", description = "上传的原图比较大，要进行高质量的压缩")
        public static final int default_quality = 70;

        @ConstantsFieldDefined(name = "默认的压缩方式", description = "")
        public static final String type_default_scale = "fix_width ";

    }

    public static class FormatConsts implements IConstants{


        @ConstantsFieldDefined(name = "需要提供webp的图片", description = "")
        public static final String need_webp = "1";

        @ConstantsFieldDefined(name = "不需要提供webp的图片", description = "")
        public static final String no_webp = "0";

        @ConstantsFieldDefined(name = "不需要提供webp的图片", description = "")
        public static final String default_format = "1";

    }



}

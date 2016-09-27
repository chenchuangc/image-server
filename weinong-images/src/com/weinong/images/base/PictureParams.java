package com.weinong.images.base;

/**
 * Created by xxx on 2016/2/23.
 */
public class PictureParams {

    private final int width;//宽
    private final int height;//高

    public static class Builder{

        private int width1;
        private int height1;

        public Builder(int width1, int height1) {
            this.width1 = width1;
            this.height1 = height1;
        }

        public Builder setWidth1(int width1) {
            this.width1 = width1;
            return this;
        }

        public Builder setHeight1(int height1) {
            this.height1 = height1;
            return this;
        }

        public PictureParams build() {

            return new PictureParams(this);
        }
    }

    private PictureParams(Builder builder) {
        this.width = builder.width1;
        this.height = builder.height1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

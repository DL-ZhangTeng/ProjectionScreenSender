package com.zhangteng.projectionscreensender.configuration;

/**
 * Created by swing on 2018/8/21.
 */
public final class VideoConfiguration {

    //--------------------默认高清时的设置参数--------------------------------
    public static final int DEFAULT_HEIGHT = 640;
    public static final int DEFAULT_WIDTH = 360;
    public static final int DEFAULT_FPS = 15;
    public static final int DEFAULT_MAX_BPS = 1500;
    public static final int DEFAULT_MIN_BPS = 400;
    public static final int DEFAULT_IFI = 1;
    public static final String DEFAULT_MIME = "video/avc";
    //--------------------默认标清时的设置参数--------------------------------
    public static final int STANDARD_HEIGHT = 640;
    public static final int STANDARD_WIDTH = 360;
    public static final int STANDARD_FPS = 10;
    public static final int STANDARD_MAX_BPS = 600;
    public static final int STANDARD_MIN_BPS = 300;
    public static final int STANDARD_IFI = 3;
    public static final String STANDARD_MIME = "video/avc";

    public int height;
    public int width;
    public int minBps;
    public int maxBps;
    public int fps;
    public int ifi;
    public String mime;

    public VideoConfiguration(HighBuilder builder) {
        height = builder.height;
        width = builder.width;
        minBps = builder.minBps;
        maxBps = builder.maxBps;
        fps = builder.fps;
        ifi = builder.ifi;
        mime = builder.mime;
    }

    public VideoConfiguration(StandardBuilder builder) {
        height = builder.height;
        width = builder.width;
        minBps = builder.minBps;
        maxBps = builder.maxBps;
        fps = builder.fps;
        ifi = builder.ifi;
        mime = builder.mime;
    }

    public static VideoConfiguration getDefaultVideoConfiguration() {
        return new HighBuilder().build();
    }

    public static class HighBuilder {
        public int height = DEFAULT_HEIGHT;
        public int width = DEFAULT_WIDTH;
        public int minBps = DEFAULT_MIN_BPS;
        public int maxBps = DEFAULT_MAX_BPS;
        public int fps = DEFAULT_FPS;
        public int ifi = DEFAULT_IFI;
        public String mime = DEFAULT_MIME;

        public HighBuilder setHeight(int height) {
            this.height = height;
            return this;
        }

        public HighBuilder setWidth(int width) {
            this.width = width;
            return this;
        }

        public HighBuilder setMinBps(int minBps) {
            this.minBps = minBps;
            return this;
        }

        public HighBuilder setMaxBps(int maxBps) {
            this.maxBps = maxBps;
            return this;
        }

        public HighBuilder setFps(int fps) {
            this.fps = fps;
            return this;
        }

        public HighBuilder setIfi(int ifi) {
            this.ifi = ifi;
            return this;
        }

        public HighBuilder setMime(String mime) {
            this.mime = mime;
            return this;
        }

        public VideoConfiguration build() {
            return new VideoConfiguration(this);
        }
    }

    public static class StandardBuilder {
        public int height = STANDARD_HEIGHT;
        public int width = STANDARD_WIDTH;
        public int minBps = STANDARD_MIN_BPS;
        public int maxBps = STANDARD_MAX_BPS;
        public int fps = STANDARD_FPS;
        public int ifi = STANDARD_IFI;
        public String mime = STANDARD_MIME;

        public StandardBuilder setHeight(int height) {
            this.height = height;
            return this;
        }

        public StandardBuilder setWidth(int width) {
            this.width = width;
            return this;
        }

        public StandardBuilder setMinBps(int minBps) {
            this.minBps = minBps;
            return this;
        }

        public StandardBuilder setMaxBps(int maxBps) {
            this.maxBps = maxBps;
            return this;
        }

        public StandardBuilder setFps(int fps) {
            this.fps = fps;
            return this;
        }

        public StandardBuilder setIfi(int ifi) {
            this.ifi = ifi;
            return this;
        }

        public StandardBuilder setMime(String mime) {
            this.mime = mime;
            return this;
        }

        public VideoConfiguration build() {
            return new VideoConfiguration(this);
        }
    }
}

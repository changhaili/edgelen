package com.lichanghai.edgelen.foundation;

/**
 * Created by lichanghai on 2018/3/22.
 */
public abstract class PixelColorFilter implements ColorFilter {

    public final static PixelColorFilter Red = new PixelColorFilter() {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            return isSingleCompSame(r, g, b);

        }
    };

    public final static PixelColorFilter Green = new PixelColorFilter() {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            return isSingleCompSame(g, b, r);
        }
    };

    public final static PixelColorFilter Blue = new PixelColorFilter() {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            return isSingleCompSame(b, r, g);
        }
    };

    public final static PixelColorFilter White = new PixelColorFilter() {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            if (r < 128 || b < 128 || g < 128) return false;
            if (Math.abs(r - b) > 64 || Math.abs(g - b) > 64 || Math.abs(r - b) > 64) return false;

            return true;
        }
    };

    public final static PixelColorFilter Yellow = new PixelColorFilter() {

        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            if (b > 100) return false;

            if (r < 128 || b < 128) return false;
            if (Math.abs(r - g) > 64) return false;

            if (Math.abs(r - b) < 64 && Math.abs(g - b) < 64) return false;

            return true;
        }
    };

    public final static PixelColorFilter Black = new PixelColorFilter() {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            if (r > 100 || b > 100 || g > 100) return false;
            if (Math.abs(r - b) > 32 || Math.abs(g - b) > 32 || Math.abs(r - b) > 32) return false;

            return true;
        }
    };

    protected boolean isSingleCompSame(int main, int other1, int other2) {

        if (main < other1 + 64 || main < other2 + 64) return false;

        if (other1 > 100 || other2 > 100) return false;

        return Math.max(main - other1, main - other2) > 64;
    }

    public abstract boolean isSame(int color);

    public static int getRed(int color) {
        return ((color >> 16) & 0xff);
    }

    public static int getGreen(int color) {
        return ((color >> 8) & 0xff);
    }

    public static int getBlue(int color) {
        return (color & 0xff);
    }


    //public abstract  boolean isSame(int color);
}

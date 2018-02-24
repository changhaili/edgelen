package com.lichanghai.edgelen.foundation.math;

/**
 * Created by lichanghai on 2018/2/10.
 */
public enum SupportColor {

    Red {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            return isSingleCompSame(r, g, b);

        }
    },

    Green {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            return isSingleCompSame(g, b, r);
        }
    },


    Blue {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            return isSingleCompSame(b, r, g);
        }
    },


    White {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            if (r < 128 || b < 128 || g < 128) return false;
            if (Math.abs(r - b) > 64 || Math.abs(g - b) > 64 || Math.abs(r - b) > 64) return false;

            return true;
        }
    },

    Yellow {
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
    },

    Black {
        @Override
        public boolean isSame(int color) {

            int r = getRed(color);
            int g = getGreen(color);
            int b = getBlue(color);

            if (r > 100 || b > 100 || g > 100) return false;
            if (Math.abs(r - b) > 32 || Math.abs(g - b) > 32 || Math.abs(r - b) > 32) return false;

            return true;
        }
    },

    ;

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
}

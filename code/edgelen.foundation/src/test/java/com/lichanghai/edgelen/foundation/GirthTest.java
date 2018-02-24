package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.girth.GirthResult;
import com.lichanghai.edgelen.foundation.pixelholder.ImagePixelHolder;
import com.lichanghai.edgelen.foundation.pixelholder.PixelImage;
import com.lichanghai.edgelen.foundation.math.SupportColor;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by lichanghai on 2018/1/11.
 */
public class GirthTest {

    @Test
    public void testGirth1() throws IOException {

        // String pre = imgPath.substring(0, imgPath.lastIndexOf(".")) + ".";

        String imgPath = "/Users/lichanghai/BaiduNetDisk/1.jpg";
        //String imgPath = "/Users/lichanghai/Mine/die/1.jpg";

        SupportColor backColor = SupportColor.Red;
        SupportColor foreColor = SupportColor.White;
        int clusterCount = 2;

        final BufferedImage img = ImageIO.read(new File(imgPath));

        final int width = img.getWidth();
        final int height = img.getHeight();

        final int[] pixels = new int[width * height];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, width);

        TimeRecord timeRecord = TimeRecord.begin();

        ImagePixelHolder pixelHolder = new ImagePixelHolder(2, new PixelImage() {

            private int [] rows;
            private int curRow = -1;

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public int getColor(int x, int y) {


                return pixels[y * width+x];

                //return img.getRGB(x, y);
            }
        });

        EdgeCurve[] curves  = LatticeUtils.getEdgeCurves(pixelHolder, 1052, 730,
                 backColor, foreColor, clusterCount, true);

        timeRecord.forceRecord("hierical time: {0}");

//
//        curves = new LatticeUtils().getEdgeCurves(width, height, 1052, 730,
//                pixels, backColor, foreColor, clusterCount, false);
//
//        timeRecord.record("enctpy time: {0}");


//        curves = new LatticeUtils().getEdgeCurves(width, height, 1052, 730,
//                pixels, SupportColor.Red, SupportColor.White, clusterCount, true);
//
//        timeRecord.forceRecord("hierical time: {0}");


        for (EdgeCurve curve : curves) {

            System.out.println("---------------------");

            GirthResult result = curve.getGirth();

            // GirthResult result = FoundationUtils.calculate(url, zipBytes);

            System.out.println("default : " + result.getRecommender());

            for (double d : result.getOthers()) {
                System.out.println("girth : " + d);

            }
        }

    }


}
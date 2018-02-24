package com.lichanghai.edgelen.foundation.utils;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.math.MathUtils;
import com.lichanghai.edgelen.foundation.math.Point3;
import com.lichanghai.edgelen.foundation.math.Rect4i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by lichanghai on 2018/2/11.
 */
public class ImageUtils {

    public static void drawLine(String path, Point3[] point3s) {

        try {

            Rect4i rect = MathUtils.getRectangle(point3s);


            BufferedImage img = new BufferedImage(rect.getWidth(), rect.getHeight(), BufferedImage.TYPE_INT_RGB);

            final Graphics2D g2 = (Graphics2D) img.getGraphics();

            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, rect.getWidth(), rect.getHeight());

            g2.setColor(new Color(255, 0, 0));

            for (Point3 p : point3s) {

                int x = (int) p.x - rect.left;
                int y = (int) p.y - rect.top;

                g2.drawLine(x, y, x + 1, y);
            }

            ImageIO.write(img, "png", new File(path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void drawLine(String path, AbstractPixelHolder pixelHolder) {

        java.util.List<Point3> point3s = new ArrayList<>();

        int[] pixels = pixelHolder.getPixelIndices().toArray();

        for (int p : pixels) {

            point3s.add(new Point3(pixelHolder.getX(p), pixelHolder.getY(p)));
        }

        drawLine(path, point3s.toArray(new Point3[point3s.size()]));
    }

}

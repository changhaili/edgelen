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

    public final static boolean DEBUG = true;

    public static void drawPoints(String path, Point3... point3s) {

        if(!DEBUG) return;

        try {

            Rect4i rect = MathUtils.getRectangle(point3s);

            BufferedImage img = new BufferedImage(rect.getWidth(), rect.getHeight(), BufferedImage.TYPE_INT_RGB);

            final Graphics2D g2 = (Graphics2D) img.getGraphics();

            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, rect.getWidth(), rect.getHeight());

            g2.setColor(new Color(255, 0, 0));

            BasicStroke bs_1=new BasicStroke(14,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
            g2.setStroke(bs_1);

            for (Point3 p : point3s) {

                if(p == null) continue;

                int x = (int) p.x - rect.left;
                int y = (int) p.y - rect.top;


                g2.drawLine(x, y, x +2, y);
                //g2.drawLine(x, y+1, x + 1, y+1);

               // g2.drawLine(x+1, y, x + 2, y);
                //g2.drawLine(x+1, y+1, x + 2, y+1);
            }

            ImageIO.write(img, "png", new File(path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void drawPoints(String path, AbstractPixelHolder ... pixelHolders) {
        if(!DEBUG) return;

        java.util.List<Point3> point3s = new ArrayList<>();

        for(AbstractPixelHolder pixelHolder : pixelHolders) {

            int[] pixels = pixelHolder.getPixelIndices().toArray();

            for (int p : pixels) {

                point3s.add(new Point3(pixelHolder.getX(p), pixelHolder.getY(p)));
            }
        }

        drawPoints(path, point3s.toArray(new Point3[point3s.size()]));
    }

    public static void drawPoints(String path, java.util.List<Point3> point3List) {
        if(!DEBUG) return;

        drawPoints(path, point3List.toArray(new Point3[point3List.size()]));
    }

    public static void drawLines(String path, Point3... point3s) {
        if(!DEBUG) return;

        try {

            Rect4i rect = MathUtils.getRectangle(point3s);


            BufferedImage img = new BufferedImage(rect.getWidth(), rect.getHeight(), BufferedImage.TYPE_INT_RGB);

            final Graphics2D g2 = (Graphics2D) img.getGraphics();

            g2.setBackground(Color.WHITE);
            g2.clearRect(0, 0, rect.getWidth(), rect.getHeight());

            g2.setColor(new Color(255, 0, 0));

            BasicStroke bs=new BasicStroke(14,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
            g2.setStroke(bs);

            for(int i=0;i<point3s.length;i+=2){

                Point3 p1 = point3s[i];
                Point3 p2 = point3s[i+1];

                int x1 = (int) p1.x - rect.left;
                int y1 = (int) p1.y - rect.top;

                int x2 = (int) p2.x - rect.left;
                int y2 = (int) p2.y - rect.top;

                g2.drawLine(x1, y1, x2 , y2);
            }

            ImageIO.write(img, "png", new File(path));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

package com.lichanghai.edgelen.foundation;

import com.lichanghai.edgelen.foundation.pixelholder.AbstractPixelHolder;
import com.lichanghai.edgelen.foundation.pixelholder.IndexPixelHolder;
import com.lichanghai.edgelen.foundation.utils.IntList;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lichanghai on 2018/1/14.
 *
 * 试验品
 * 使用laplace滤波
 */
@Deprecated
public class EdgeDetection {

    private final static int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private final static ExecutorService BORDER_EXECUTOR = Executors.newFixedThreadPool(CPU_COUNT);

    private IntList getMouldBorderUsingExecutor(final IndexPixelHolder pixels) {

        final AbstractPixelHolder.IntIterable pixelIndices = pixels.getPixelIndices();
        final int[] arounds = new int[pixelIndices.getLength()];

        final int threadCount = CPU_COUNT;

        final Callable[] calls = new Callable[threadCount];

        for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {

            final int threadIndex2 = threadIndex;

            calls[threadIndex] = new Callable() {

                public Object call() throws Exception {

                    int count = pixelIndices.getLength() / threadCount;
                    int begin = count * threadIndex2;

                    count = threadIndex2 == threadCount - 1 ? pixelIndices.getLength() - begin : count;

                    for (int i = 0; i < count; i++) {

                        int pixelIndex = pixelIndices.getAt(i + begin);

                        int x = pixels.getX(pixelIndex);
                        int y = pixels.getY(pixelIndex);

                        int a0 = pixels.getIndex(x - 1, y - 1);
                        int a1 = a0 + 1;// naiveLattice.getIndex(x, y - 1);
                        int a2 = a0 + 2;// naiveLattice.getIndex(x + 1, y - 1);

                        int a3 = pixels.getIndex(x - 1, y);
                        int a4 = a3 + 2;// naiveLattice.getIndex(x + 1, y);

                        int a5 = pixels.getIndex(x - 1, y + 1);
                        int a6 = a5 + 1;// naiveLattice.getIndex(x, y + 1);
                        int a7 = a5 + 2;// naiveLattice.getIndex(x + 1, y + 1);

                        int bs = 0;

                        if (pixels.hasPixel(a0)) {
                            ++bs;
                        }
                        if (pixels.hasPixel(a1)) {
                            ++bs;
                        }
                        if (pixels.hasPixel(a2)) {
                            ++bs;
                        }
                        if (pixels.hasPixel(a3)) {
                            ++bs;
                        }
                        if (pixels.hasPixel(a4)) {
                            ++bs;
                        }
                        if (pixels.hasPixel(a5)) {
                            ++bs;
                        }
                        if (pixels.hasPixel(a6)) {
                            ++bs;
                        }
                        if (pixels.hasPixel(a7)) {
                            ++bs;
                        }

                        arounds[i + begin] = bs;
                    }

                    return null;

                }

            };

        }

        try {
            BORDER_EXECUTOR.invokeAll((Collection) Arrays.asList(calls));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        IntList pnts = new IntList();

        for (int i = 0; i < pixelIndices.getLength(); i++) {

            int arround = arounds[i];

            if (arround < 8 && arround > 1) {

                int pixelIndex = pixelIndices.getAt(i);

                pnts.append(pixelIndex);
            }
        }

        return pnts;

    }

    private IntList getMouldBorderSequence(IndexPixelHolder pixels) {

        AbstractPixelHolder.IntIterable pixelIndices = pixels.getPixelIndices();
        int[] arounds = new int[pixelIndices.getLength()];

        for (int i = 0; i < pixelIndices.getLength(); i++) {

            int index = pixelIndices.getAt(i);

            int x = pixels.getX(index);
            int y = pixels.getY(index);

            int a0 = pixels.getIndex(x - 1, y - 1);
            int a1 = pixels.getIndex(x - 1, y);
            int a2 = pixels.getIndex(x - 1, y + 1);

            int a3 = pixels.getIndex(x, y - 1);
            int a5 = pixels.getIndex(x, y + 1);

            int a6 = pixels.getIndex(x + 1, y - 1);
            int a7 = pixels.getIndex(x + 1, y);
            int a8 = pixels.getIndex(x + 1, y + 1);

            int bs = 0;

            if (pixels.hasPixel(a0)) {
                bs++;
            }
            if (pixels.hasPixel(a1)) {
                bs++;
            }
            if (pixels.hasPixel(a2)) {
                bs++;
            }
            if (pixels.hasPixel(a3)) {
                bs++;
            }
            if (pixels.hasPixel(a5)) {
                bs++;
            }
            if (pixels.hasPixel(a6)) {
                bs++;
            }
            if (pixels.hasPixel(a7)) {
                bs++;
            }
            if (pixels.hasPixel(a8)) {
                bs++;
            }

            arounds[i] = bs;
        }

        IntList pnts = new IntList();

        for (int i = 0; i < pixelIndices.getLength(); i++) {

            int arround = arounds[i];

            if (arround < 8 && arround > 1) {

                int pixelIndex = pixelIndices.getAt(i);

                pnts.append(pixelIndex);
            }
        }

        return pnts;
    }

    public IndexPixelHolder getMouldBorder(IndexPixelHolder pixels) {

        IntList indices;
        if (CPU_COUNT < 3) {
            indices = getMouldBorderSequence(pixels);
        } else {
            indices = getMouldBorderUsingExecutor(pixels);
        }

        return new IndexPixelHolder(pixels.getWidth(), pixels.getHeight(), indices);
    }

}

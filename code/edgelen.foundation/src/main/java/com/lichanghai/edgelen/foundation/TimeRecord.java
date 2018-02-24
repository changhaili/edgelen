package com.lichanghai.edgelen.foundation;

import java.text.MessageFormat;

/**
 * Created by lichanghai on 2018/2/11.
 */
public class TimeRecord {

    private volatile long lastTime;

    private final String messageFormat;

    private boolean output = true;

    private TimeRecord(String messageFormat) {

        lastTime = System.currentTimeMillis();
        this.messageFormat = messageFormat;
    }

    public static TimeRecord begin(String messageFormat) {
        return new TimeRecord(messageFormat);
    }

    public static TimeRecord begin() {
        return new TimeRecord("{0");
    }

    public void record() {

        if (output) {
            System.out.println(MessageFormat.format(messageFormat, System.currentTimeMillis() - lastTime));

            lastTime = System.currentTimeMillis();
        }
    }

    public void record(String messageFormat) {

        if (output) {
            System.out.println(MessageFormat.format(messageFormat, System.currentTimeMillis() - lastTime));
            lastTime = System.currentTimeMillis();
        }
    }

    public void forceRecord(String messageFormat) {

        System.out.println(MessageFormat.format(messageFormat, System.currentTimeMillis() - lastTime));

        lastTime = System.currentTimeMillis();
    }
}

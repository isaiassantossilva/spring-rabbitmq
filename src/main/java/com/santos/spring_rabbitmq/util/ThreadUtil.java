package com.santos.spring_rabbitmq.util;

public abstract class ThreadUtil {

    public static void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}

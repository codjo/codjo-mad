package net.codjo.mad.server.util;

public class Chronometer {
    private long startTime;
    private long endTime;


    public Chronometer() {
    }


    public long getStartTime() {
        return startTime;
    }


    public long getEndTime() {
        return endTime;
    }


    public long getDelay() {
        return endTime - startTime;
    }


    public void start() {
        startTime = System.currentTimeMillis();
    }


    public void stop() {
        endTime = System.currentTimeMillis();
    }
}

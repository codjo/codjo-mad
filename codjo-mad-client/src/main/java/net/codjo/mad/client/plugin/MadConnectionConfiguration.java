package net.codjo.mad.client.plugin;
/**
 *
 */
public interface MadConnectionConfiguration {
    long DEFAULT_TIME_OUT = 60000;


    long getTimeout();


    void setTimeout(long timeout);
}

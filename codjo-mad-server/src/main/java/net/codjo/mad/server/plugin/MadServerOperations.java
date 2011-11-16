package net.codjo.mad.server.plugin;
import net.codjo.mad.server.handler.AspectLauncher;
import net.codjo.mad.server.handler.HandlerListener;

public interface MadServerOperations {

    void addHandlerListener(HandlerListener listener);


    void removeHandlerListener(HandlerListener listener);


    AspectLauncher createAspectLauncher();
}

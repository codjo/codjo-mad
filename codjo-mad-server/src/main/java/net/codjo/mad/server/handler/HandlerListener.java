package net.codjo.mad.server.handler;
import java.util.EventListener;

public interface HandlerListener extends EventListener {

    void handlerStarted(Handler handler, HandlerContext handlerContext);


    void handlerStopped(Handler handler, HandlerContext handlerContext);
}

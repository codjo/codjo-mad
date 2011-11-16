package net.codjo.mad.server.handler;
import net.codjo.aspect.Aspect;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectException;
import net.codjo.aspect.JoinPoint;
import net.codjo.test.common.LogString;
/**
 *
 */
public class MyAspect implements Aspect {
    @SuppressWarnings({"StaticNonFinalField"})
    static AspectContext aspectContext = null;
    @SuppressWarnings({"StaticNonFinalField"})
    static LogString log = new LogString();


    public void setUp(AspectContext context, JoinPoint joinPoint) throws AspectException {
        log.call("setUp");
    }


    public void run(AspectContext context) throws AspectException {
        aspectContext = context;
        log.call("run");
    }


    public void cleanUp(AspectContext context) throws AspectException {
        log.call("cleanUp");
    }
}

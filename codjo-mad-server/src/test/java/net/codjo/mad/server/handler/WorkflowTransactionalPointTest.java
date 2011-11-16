package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectManager;
import net.codjo.aspect.JoinPoint;
import static net.codjo.aspect.JoinPoint.CALL_AFTER;
import static net.codjo.aspect.JoinPoint.CALL_BEFORE;
import net.codjo.aspect.util.PointRunner;
import net.codjo.aspect.util.PointRunnerException;
import net.codjo.aspect.util.TransactionalManagerMock;
import net.codjo.aspect.util.TransactionalPoint;
import static net.codjo.mad.server.handler.WorkflowTransactionalPoint.HANDLER_JOIN_POINT;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class WorkflowTransactionalPointTest {
    private LogString log = new LogString();
    private AspectManager aspectManager = new AspectManager();
    private WorkflowTransactionalPoint point;


    @Test
    public void test_run_oneBeforeAspect() throws Exception {
        aspectManager.addAspect("myaspect",
                                joinPoint(CALL_BEFORE, HANDLER_JOIN_POINT, "a-handler-id"),
                                MyAspect.class);

        point.run(createAspectContext("a-handler-id"), new PointRunnerMock());

        log.assertContent("myaspect.setUp(), "
                          + "transaction.begin(), "
                          + "myaspect.run(), "
                          + "pointRunner.run(), "
                          + "transaction.flush(), "
                          + "transaction.commit(), "
                          + "transaction.end(), "
                          + "myaspect.cleanUp()");
    }


    @Test
    public void test_run_oneForkedAspect() throws Exception {
        aspectManager.addAspect("myaspect",
                                joinPoint(CALL_AFTER, HANDLER_JOIN_POINT, "a-handler-id", true),
                                MyAspect.class);

        point.run(createAspectContext("a-handler-id"), new PointRunnerMock());

        log.assertContent("transaction.begin(), "
                          + "pointRunner.run(), "
                          + "transaction.flush(), "
                          + "transaction.commit(), "
                          + "transaction.end(), "
                          + "trigger-forked-mode-for(myaspect)");
    }


    @Before
    public void setUp() {
        MyAspect.log = new LogString("myaspect", log);
        point = new WorkflowTransactionalPoint(aspectManager,
                                               new TransactionalManagerMock(new LogString("transaction",
                                                                                          log)),
                                               new AspectBranchLauncherFactoryMock(log));
    }


    private JoinPoint[] joinPoint(int call, String handlerJoinPoint, String argument) {
        return new JoinPoint[]{
              new JoinPoint(call, handlerJoinPoint, argument)};
    }


    private JoinPoint[] joinPoint(int call, String handlerJoinPoint, String argument, boolean fork) {
        return new JoinPoint[]{new JoinPoint(call, handlerJoinPoint, argument, fork)};
    }


    private AspectContext createAspectContext(String handlerId) {
        AspectContext context = new AspectContext();
        context.put(TransactionalPoint.CONNECTION, new ConnectionMock());
        context.put(TransactionalPoint.ARGUMENT, handlerId);
        return context;
    }


    private class PointRunnerMock implements PointRunner {
        public void run() throws PointRunnerException {
            log.call("pointRunner.run");
        }
    }
}

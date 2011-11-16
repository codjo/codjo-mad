package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectManager;
import net.codjo.aspect.JoinPoint;
import static net.codjo.aspect.JoinPoint.CALL_AFTER;
import net.codjo.aspect.util.TransactionalManagerMock;
import net.codjo.aspect.util.TransactionalPoint;
import static net.codjo.mad.server.handler.WorkflowTransactionalPoint.HANDLER_JOIN_POINT;
import net.codjo.mad.server.handler.aspect.AspectBranchId;
import net.codjo.security.common.api.UserMock;
import net.codjo.sql.server.ConnectionPoolMock;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import org.exolab.castor.jdo.JDO;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class AspectLauncherTest {
    private LogString log = new LogString();
    private AspectManager aspectManager = new AspectManager();
    private AspectLauncher launcher;


    @Test
    public void test_oneAspect() throws Exception {
        JoinPoint joinPoint = new JoinPoint(CALL_AFTER, HANDLER_JOIN_POINT, "a-handler-id", true);

        aspectManager.addAspect("myaspect", new JoinPoint[]{joinPoint}, MyAspect.class);

        launcher.run(createAspectContext("a-handler-id"),
                     new AspectBranchId(joinPoint, "myaspect"),
                     new ConnectionPoolMock(new LogString("pool", log)),
                     new UserMock());

        log.assertContent("pool.getConnection(), "
                          + "myaspect.setUp(), "
                          + "transaction.begin(), "
                          + "transaction.flush(), "
                          + "myaspect.run(), "
                          + "transaction.commit(), "
                          + "transaction.end(), "
                          + "myaspect.cleanUp()");
    }


    @Test
    public void test_aspectOnAspect() throws Exception {
        JoinPoint joinPoint = new JoinPoint(CALL_AFTER, HANDLER_JOIN_POINT, "a-handler-id", true);

        aspectManager.addAspect("myaspect", new JoinPoint[]{joinPoint}, MyAspect.class);
        aspectManager.addAspect("aspectOnAspect",
                                joinPoint(CALL_AFTER, JoinPoint.ON_ASPECT, "myaspect", true),
                                MyAspect.class);

        launcher.run(createAspectContext("a-handler-id"),
                     new AspectBranchId(joinPoint, "myaspect"),
                     new ConnectionPoolMock(new LogString("pool", log)),
                     new UserMock());

        log.assertContent("pool.getConnection(), "
                          + "myaspect.setUp(), "
                          + "transaction.begin(), "
                          + "transaction.flush(), "
                          + "myaspect.run(), "
                          + "transaction.commit(), "
                          + "transaction.end(), "
                          + "myaspect.cleanUp(), "
                          + "trigger-forked-mode-for(aspectOnAspect)");
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


    @Before
    public void setUp() throws Exception {
        MyAspect.log = new LogString("myaspect", log);
        launcher = new AspectLauncher(aspectManager,
                                      new JDO(),
                                      new AspectBranchLauncherFactoryMock(log),
                                      new TransactionalManagerMock(new LogString("transaction",
                                                                                 log)));
    }
}

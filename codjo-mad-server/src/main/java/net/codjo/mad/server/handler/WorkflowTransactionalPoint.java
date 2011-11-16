package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectException;
import net.codjo.aspect.AspectHelper;
import net.codjo.aspect.AspectManager;
import net.codjo.aspect.util.PointRunner;
import net.codjo.aspect.util.PointRunnerException;
import net.codjo.aspect.util.TransactionException;
import net.codjo.aspect.util.TransactionalManager;
import net.codjo.aspect.util.TransactionalPoint;
import net.codjo.mad.server.handler.aspect.ForkAspectFilter;
/**
 *
 */
public class WorkflowTransactionalPoint extends AbstractTransactionalPoint {
    static final String HANDLER_JOIN_POINT = "handler.execute";
    public static final String CONNECTION = "connection";
    public static final String ARGUMENT = "argument";
    private AspectManager manager;


    public WorkflowTransactionalPoint(AspectManager manager,
                                      TransactionalManager txManager,
                                      AspectBranchLauncherFactory aspectLauncherFactory) {
        super(txManager, aspectLauncherFactory);
        this.manager = manager;
    }


    public void run(AspectContext context, PointRunner pointRunner)
          throws TransactionException, AspectException, PointRunnerException {
        assertNotNull(context, TransactionalPoint.CONNECTION);

        ForkAspectFilter filter = new ForkAspectFilter();
        AspectHelper aspectHelper = buildHelper(context, filter);

        runNonForkedAspects(context, pointRunner, aspectHelper);

        runForkedAspects(context, filter);
    }


    private static void assertNotNull(AspectContext context, String key) {
        Object obj = context.get(key);
        if (null == obj) {
            throw new IllegalArgumentException("Objet manquant dans le context sous la clé : " + key);
        }
    }


    private AspectHelper buildHelper(AspectContext context, ForkAspectFilter filter) throws AspectException {
        Object arg = context.get(TransactionalPoint.ARGUMENT);
        if (arg instanceof String[]) {
            return manager.createHelper(HANDLER_JOIN_POINT, (String[])arg, filter);
        }
        else {
            return manager.createHelper(HANDLER_JOIN_POINT, (String)arg, filter);
        }
    }
}

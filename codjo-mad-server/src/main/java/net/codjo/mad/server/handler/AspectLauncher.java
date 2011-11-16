package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectException;
import net.codjo.aspect.AspectHelper;
import net.codjo.aspect.AspectManager;
import net.codjo.aspect.util.PointRunner;
import net.codjo.aspect.util.PointRunnerException;
import net.codjo.aspect.util.TransactionException;
import net.codjo.aspect.util.TransactionalManager;
import net.codjo.mad.server.handler.aspect.AspectBranchId;
import net.codjo.mad.server.handler.aspect.ForkAspectFilter;
import net.codjo.mad.server.plugin.AgentMadRequestContext;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import java.sql.SQLException;
import org.exolab.castor.jdo.JDO;
/**
 *
 */
public class AspectLauncher extends AbstractTransactionalPoint {
    private AspectManager aspectManager;
    private JDO jdo;


    public AspectLauncher(AspectManager aspectManager,
                          JDO jdo,
                          AspectBranchLauncherFactory aspectLauncherFactory) {
        this(aspectManager, jdo, aspectLauncherFactory, new TxManagerWrapper());
    }


    AspectLauncher(AspectManager aspectManager,
                   JDO jdo,
                   AspectBranchLauncherFactory aspectLauncherFactory,
                   TransactionalManager transactionalManager) {
        super(transactionalManager, aspectLauncherFactory);
        this.aspectManager = aspectManager;
        this.jdo = jdo;
    }


    public void run(AspectContext context, AspectBranchId branchId, ConnectionPool pool, User user)
          throws AspectException, TransactionException, SQLException, PointRunnerException {

        ForkAspectFilter filter = new ForkAspectFilter();
        AspectHelper aspectHelper = buildHelper(branchId, filter);

        HandlerContext handlerContext = new HandlerContext(new AgentMadRequestContext(pool, jdo, user));
        HandlerManager.fillAspectContext(context,
                                         handlerContext,
                                         new String[]{branchId.getJoinPoint().getArgument()});

        try {
            runNonForkedAspects(context, new EmptyPointRunner(), aspectHelper);
        }
        finally {
            handlerContext.getTxConnection().close();
        }

        runForkedAspects(context, filter);
    }


    private AspectHelper buildHelper(AspectBranchId id, ForkAspectFilter filter) throws AspectException {
        return aspectManager.createHelper(id.getJoinPoint(), id.getAspectId(), filter);
    }


    private static class EmptyPointRunner implements PointRunner {
        public void run() throws PointRunnerException {
        }
    }
}

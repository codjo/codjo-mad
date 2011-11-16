package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectException;
import net.codjo.aspect.AspectHelper;
import net.codjo.aspect.util.PointRunner;
import net.codjo.aspect.util.PointRunnerException;
import net.codjo.aspect.util.TransactionException;
import net.codjo.aspect.util.TransactionalManager;
import net.codjo.mad.server.handler.aspect.AspectBranchId;
import net.codjo.mad.server.handler.aspect.ForkAspectFilter;
import java.util.List;
/**
 *
 */
abstract class AbstractTransactionalPoint {
    protected TransactionalManager txManager;
    protected AspectBranchLauncherFactory aspectLauncherFactory;


    protected AbstractTransactionalPoint(TransactionalManager txManager,
                                         AspectBranchLauncherFactory aspectLauncherFactory) {
        this.txManager = txManager;
        this.aspectLauncherFactory = aspectLauncherFactory;
    }


    protected void runNonForkedAspects(AspectContext context,
                                       PointRunner pointRunner,
                                       AspectHelper aspectHelper)
          throws AspectException, TransactionException, PointRunnerException {
        try {
            aspectHelper.setUp(context);
            txManager.begin(context);

            try {
                aspectHelper.runBefore(context);
                pointRunner.run();
                txManager.flush(context);
                aspectHelper.runAfter(context);
                txManager.commit(context);
            }
            catch (PointRunnerException ex) {
                // On rollback d'abord la transaction, puis la même connexion en autocommit
                // est utilisée par runError.
                txManager.rollback(context);
                runError(aspectHelper, context);
                throw ex;
            }
            catch (AspectException ex) {
                // Idem
                txManager.rollback(context);
                runError(aspectHelper, context);
                throw ex;
            }
            catch (Throwable e) {
                txManager.rollback(context);
                throw new PointRunnerException(e.getLocalizedMessage(), e);
            }
            finally {
                txManager.end(context);
            }
        }
        finally {
            aspectHelper.cleanUp(context);
        }
    }


    protected void runForkedAspects(AspectContext context, ForkAspectFilter filter) throws AspectException {
        List<AspectBranchId> forkedAspects = filter.getForkedAspectIds();
        if (!forkedAspects.isEmpty()) {
            AspectBranchLauncher aspectLauncher = aspectLauncherFactory.create();
            for (AspectBranchId forkedAspect : forkedAspects) {
                aspectLauncher.run(forkedAspect, context);
            }
        }
    }


    protected void runError(AspectHelper aspectHelper, AspectContext context) {
        try {
            txManager.end(context);
            aspectHelper.runError(context);
        }
        catch (Exception e) {
            // En cas d'erreur, on ignore : c'est l'exception d'origine (qui a
            // provoqué l'appel de l'aspect d'erreur) qui sera remontée au code
            // appelant.
            ;
        }
    }
}

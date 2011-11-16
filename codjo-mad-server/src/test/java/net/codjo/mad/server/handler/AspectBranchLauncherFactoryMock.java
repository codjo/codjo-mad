package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectException;
import net.codjo.mad.server.handler.aspect.AspectBranchId;
import net.codjo.test.common.LogString;
/**
 *
 */
class AspectBranchLauncherFactoryMock
      implements AspectBranchLauncherFactory, AspectBranchLauncher {
    private LogString log;


    AspectBranchLauncherFactoryMock() {
        this(new LogString());
    }


    AspectBranchLauncherFactoryMock(LogString log) {
        this.log = log;
    }


    public AspectBranchLauncher create() {
        return this;
    }


    public void run(AspectBranchId branchId, AspectContext context) throws AspectException {
        log.call("trigger-forked-mode-for", branchId.getAspectId());
    }
}

package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectException;
import net.codjo.mad.server.handler.aspect.AspectBranchId;
/**
 *
 */
public interface AspectBranchLauncher {
    void run(AspectBranchId branchId, AspectContext context) throws AspectException;
}

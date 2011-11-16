package net.codjo.mad.server.handler.aspect;
import net.codjo.aspect.AspectFilter;
import net.codjo.aspect.JoinPoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 *
 */
public class ForkAspectFilter implements AspectFilter {
    private List<AspectBranchId> forkedAspectIds = new ArrayList<AspectBranchId>();


    public boolean accept(JoinPoint joinPoint, String aspectId) {
        if (joinPoint.isFork()) {
            forkedAspectIds.add(new AspectBranchId(joinPoint, aspectId));
            return false;
        }
        return true;
    }


    public List<AspectBranchId> getForkedAspectIds() {
        return Collections.unmodifiableList(forkedAspectIds);
    }
}

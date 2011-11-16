package net.codjo.mad.server.handler.aspect;
import net.codjo.aspect.JoinPoint;
/**
 *
 */
public class AspectBranchId {
    private JoinPoint joinPoint;
    private String aspectId;


    public AspectBranchId(JoinPoint joinPoint, String aspectId) {
        this.joinPoint = joinPoint;
        this.aspectId = aspectId;
    }


    public JoinPoint getJoinPoint() {
        return joinPoint;
    }


    public String getAspectId() {
        return aspectId;
    }


    @Override
    public String toString() {
        return "AspectBranchId{"
               + joinPoint +
               ", aspectId='" + aspectId + '\'' +
               '}';
    }
}

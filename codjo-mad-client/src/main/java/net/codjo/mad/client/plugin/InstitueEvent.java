package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage;
/**
 *
 */
class InstitueEvent {
    private AclMessage failureMessage;
    static final String UNEXPECTED_INTERNAL_ERROR = "unexpected internal error";


    InstitueEvent() {
    }


    InstitueEvent(AclMessage failureMessage) {
        this.failureMessage = failureMessage;
    }


    public static InstitueEvent failureEvent(AclMessage aclMessage) {
        return new InstitueEvent(aclMessage);
    }


    public static InstitueEvent successEvent() {
        return new InstitueEvent();
    }


    boolean hasFailed() {
        return failureMessage != null;
    }


    public String getErrorMessage() {
        if (failureMessage == null) {
            return null;
        }
        return extractErrorFromMessage();
    }


    private String extractErrorFromMessage() {
        try {
            return ((Throwable)failureMessage.getContentObject()).getLocalizedMessage();
        }
        catch (Throwable e) {
            return UNEXPECTED_INTERNAL_ERROR;
        }
    }
}

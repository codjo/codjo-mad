package net.codjo.mad.common.message;
import net.codjo.agent.UserId;
import net.codjo.security.common.api.User;
import java.io.Serializable;

/**
 *
 */
public class LoginEvent implements Serializable {
    private final String ambassadorName;
    private final User user;
    private final Throwable loginFailureException;
    private final UserId userId;


    public LoginEvent(Throwable loginFailureException) {
        ambassadorName = null;
        user = null;
        userId = null;
        this.loginFailureException = loginFailureException;
    }


    public LoginEvent(String ambassadorName, User user, UserId userId) {
        this.ambassadorName = ambassadorName;
        this.user = user;
        this.userId = userId;
        loginFailureException = null;
    }


    public String getAmbassadorName() {
        return ambassadorName;
    }


    public User getUser() {
        return user;
    }


    @Override
    public String toString() {
        if (hasFailed()) {
            return "LoginEvent[loginFailureException='"
                   + loginFailureException.getMessage() + "']";
        }
        return "LoginEvent[ambassadorName='" + ambassadorName + "']";
    }


    public boolean hasFailed() {
        return loginFailureException != null;
    }


    public Throwable getLoginFailureException() {
        return loginFailureException;
    }


    public UserId getUserId() {
        return userId;
    }
}

package net.codjo.mad.common.message;

import net.codjo.agent.UserId;
import java.io.Serializable;

/**
 * Action pour demander la création d'un ambassadeur.
 *
 * @see InstituteAmbassadorProtocol
 */
public class InstituteAmbassadorAction implements Serializable {
    private final UserId userId;


    public InstituteAmbassadorAction(UserId userId) {
        this.userId = userId;
    }


    public UserId getUserId() {
        return userId;
    }
}

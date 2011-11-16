package net.codjo.mad.common.message;
import net.codjo.agent.UserId;
import net.codjo.security.common.api.User;
import net.codjo.security.common.api.UserMock;
import junit.framework.TestCase;
/**
 * Classe de test de {@link LoginEvent}.
 */
public class LoginEventTest extends TestCase {
    public void test_constructor_loginOK() throws Exception {
        UserId userId = UserId.createId("l", "p");
        User user = new UserMock();
        LoginEvent event = new LoginEvent("Bobo", user, userId);

        assertEquals("Bobo", event.getAmbassadorName());
        assertSame(user, event.getUser());
        assertSame(userId, event.getUserId());
        assertEquals("LoginEvent[ambassadorName='Bobo']", event.toString());
    }


    public void test_constructor_loginFailed() throws Exception {
        final Throwable throwable = new Throwable("error message");

        LoginEvent event = new LoginEvent(throwable);

        assertNull(event.getAmbassadorName());
        assertTrue(event.hasFailed());
        assertSame(throwable, event.getLoginFailureException());
        assertEquals("LoginEvent[loginFailureException='error message']", event.toString());
    }
}

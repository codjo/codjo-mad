package net.codjo.mad.client.request;
import junit.framework.TestCase;
/**
 *
 */
public class RequestIdManagerTest extends TestCase {
    public RequestIdManagerTest(String str) {
        super(str);
    }


    /**
     * Le RequestIdManager etant utilise déjà dans d'autres test on ne peut pas savoir quel numero va sortir
     * !
     */
    public void test_getNewRequestId() {
        RequestIdManager manager = RequestIdManager.getInstance();
        int lastId = Integer.valueOf(manager.getNewRequestId());

        assertEquals(String.valueOf(++lastId), manager.getNewRequestId());
        assertEquals(String.valueOf(++lastId), manager.getNewRequestId());

        RequestIdManager manager2 = RequestIdManager.getInstance();

        assertEquals(String.valueOf(++lastId), manager2.getNewRequestId());
        assertEquals(String.valueOf(++lastId), manager2.getNewRequestId());
    }
}

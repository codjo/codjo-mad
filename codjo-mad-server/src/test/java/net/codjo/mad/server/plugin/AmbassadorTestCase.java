package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.MessageTemplate.MatchExpression;
import net.codjo.agent.test.Story;
import net.codjo.mad.common.ZipUtil;
import net.codjo.test.common.LogString;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
/**
 *
 */
public class AmbassadorTestCase {
    protected Story story = new Story();
    protected LogString log = new LogString();
    protected ProcessorMock processorMock;


    @Before
    public void setUp() throws Exception {
        story.doSetUp();
        processorMock = new ProcessorMock();
    }


    @After
    public void tearDown() throws Exception {
        story.doTearDown();
    }


    protected MessageTemplate matchRequest(final String request, final String senderPrefix) {
        return MessageTemplate.matchWith(new MatchExpression() {
            public boolean match(AclMessage aclMessage) {
                String content = null;
                try {
                    content = ZipUtil.unzip(aclMessage.getByteSequenceContent());
                }
                catch (IOException e) {
                    ;
                }
                return request.equals(content)
                       && aclMessage.getSender().getName().startsWith(senderPrefix);
            }
        });
    }
}

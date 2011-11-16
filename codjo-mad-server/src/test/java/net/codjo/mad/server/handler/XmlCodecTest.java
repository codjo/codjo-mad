package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.JoinPoint;
import net.codjo.aspect.util.TransactionalPoint;
import net.codjo.mad.server.handler.aspect.AspectBranchId;
import net.codjo.mad.server.handler.aspect.Keys;
import net.codjo.mad.server.handler.aspect.QueryManager;
import net.codjo.mad.server.handler.aspect.QueryManagerMock;
import net.codjo.test.common.AssertUtil;
import net.codjo.test.common.XmlUtil;
import junit.framework.TestCase;
/**
 *
 */
public class XmlCodecTest extends TestCase {
    public void test_aspectContextToXml() throws Exception {
        AspectContext aspectContext = new AspectContext();
        aspectContext.put(HandlerManager.MAD_TX_MANAGER, "madTxManager");
        aspectContext.put(Keys.USER_NAME, "user");
        aspectContext.put(Keys.USER, "userProfil");
        aspectContext.put(TransactionalPoint.CONNECTION, "txConnection");
        aspectContext.put(TransactionalPoint.ARGUMENT, new String[]{"updateToto", "insertTata", "deleteIt"});
        aspectContext.put(QueryManager.class.getName(), new QueryManagerMock());

        XmlUtil.assertEquivalent("<net.codjo.aspect.AspectContext>"
                                 + "  <map>"
                                 + "    <entry>"
                                 + "      <string>argument</string>"
                                 + "      <string-array>"
                                 + "        <string>updateToto</string>"
                                 + "        <string>insertTata</string>"
                                 + "        <string>deleteIt</string>"
                                 + "      </string-array>"
                                 + "    </entry>"
                                 + "    <entry>"
                                 + "      <string>USER_NAME_mad_context_key</string>"
                                 + "      <string>user</string>"
                                 + "    </entry>"
                                 + "    <entry>"
                                 + "      <string>"
                                 + "        net.codjo.mad.server.handler.aspect.QueryManager"
                                 + "      </string>"
                                 + "      <net.codjo.mad.server.handler.aspect.QueryManagerMock/>"
                                 + "    </entry>"
                                 + "  </map>"
                                 + "</net.codjo.aspect.AspectContext>",
                                 XmlCodec.toXml(aspectContext));
    }


    public void test_aspectContextFromXml() throws Exception {
        AspectContext expected = new AspectContext();
        expected.put(Keys.USER_NAME, "user");
        expected.put(TransactionalPoint.ARGUMENT, new String[]{"updateToto", "insertTata", "deleteIt"});

        AspectContext decodedContext = XmlCodec.extractAspectContext(
              "<net.codjo.aspect.AspectContext>"
              + "  <map>"
              + "    <entry>"
              + "      <string>argument</string>"
              + "      <string-array>"
              + "        <string>updateToto</string>"
              + "        <string>insertTata</string>"
              + "        <string>deleteIt</string>"
              + "      </string-array>"
              + "    </entry>"
              + "    <entry>"
              + "      <string>USER_NAME_mad_context_key</string>"
              + "      <string>user</string>"
              + "    </entry>"
              + "  </map>"
              + "</net.codjo.aspect.AspectContext>");

        assertEquals(expected.get(Keys.USER_NAME), decodedContext.get(Keys.USER_NAME));
        AssertUtil.assertEquals(((String[])expected.get(TransactionalPoint.ARGUMENT)),
                                ((String[])decodedContext.get(TransactionalPoint.ARGUMENT)));
    }


    public void test_aspectBranchId() throws Exception {
        AspectBranchId branchId = new AspectBranchId(new JoinPoint(JoinPoint.CALL_AFTER, "my-point", "arg1"),
                                                     "aspect-id");

        String xml = XmlCodec.toXml(branchId);

        AspectBranchId result = XmlCodec.extractAspectBranchId(xml);

        assertEquals(result.getAspectId(), branchId.getAspectId());
        assertEquals(result.getJoinPoint().toString(), branchId.getJoinPoint().toString());
    }
}

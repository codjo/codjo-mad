package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.test.common.XmlUtil;
import java.util.Map;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @author $Author: duclosm $
 * @version $Revision: 1.4 $
 */
public class DeleteFactoryTest extends TestCase {
    protected RequestFactory deleteFactory;
    protected Map fields;

    public DeleteFactoryTest(String str) {
        super(str);
    }

    /**
     * Test que le comportement construit la bonne request lorsque des champs sont
     * définit dans l'IHM.
     *
     * @throws Exception
     */
    public void test_buildRequest() throws Exception {
        deleteFactory.init(getPk());

        Request request = deleteFactory.buildRequest(null);

        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    /**
     * Test que le comportement construit la bonne request (plusieurs PK).
     *
     * @throws Exception
     */
    public void test_buildRequest_multiPk() throws Exception {
        deleteFactory.init(getMultiplePk());

        Request request = deleteFactory.buildRequest(null);

        String xml = getRequestMultiplePkAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    @Override
    protected void setUp() {
        deleteFactory = new DeleteFactory("deleteCodificationPtf");
    }


    protected FieldsList getMultiplePk() {
        FieldsList fieldsList = new FieldsList();
        fieldsList.addField("pimsCode", "999");
        fieldsList.addField("sicovamCode", "12");
        return fieldsList;
    }


    protected FieldsList getPk() {
        FieldsList fieldsList = new FieldsList();
        fieldsList.addField("pimsCode", "999");
        return fieldsList;
    }


    protected String getRequestAsXml(String requestId) {
        return "<delete request_id=\"" + requestId + "\">"
        + "<id>deleteCodificationPtf</id>" + "<primarykey>"
        + "<field name='pimsCode'>999</field>" + "</primarykey>" + "</delete>";
    }


    protected String getRequestMultiplePkAsXml(String requestId) {
        return "<delete request_id=\"" + requestId + "\">"
        + "<id>deleteCodificationPtf</id>" + "<primarykey>"
        + "<field name='pimsCode'>999</field>"
        + "<field name='sicovamCode'>12</field>" + "</primarykey>" + "</delete>";
    }
}

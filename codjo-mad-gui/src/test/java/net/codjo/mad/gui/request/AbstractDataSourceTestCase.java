package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.security.common.api.UserMock;
import org.uispec4j.UISpecTestCase;
/**
 *
 */
public abstract class AbstractDataSourceTestCase extends UISpecTestCase {
    protected Preference pref;
    protected DefaultGuiContext guiContext;


    @Override
    protected void setUp() {
        pref = new Preference();
        pref.setSelectByPkId("selectCodificationPtfById");
        pref.setSelectAllId("selectAllCodificationPtf");
        pref.setUpdateId("updateCodificationPtf");
        pref.setInsertId("insertCodificationPtf");

        guiContext = new DefaultGuiContext();
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
    }


    private FieldsList getSelectors(int selectorNb) {
        FieldsList selector = new FieldsList();
        if (selectorNb == 0) {
            selector.addField("pimsCode", "999");
        }
        return selector;
    }


    protected DetailDataSource buildDataSource(int row) {
        return buildDataSource(row, pref.getSelectByPk(), null);
    }


    protected DetailDataSource buildDataSource(int row,
                                               RequestFactory loadFactory,
                                               RequestFactory saveFactory) {
        return new DetailDataSource(guiContext, new RequestSender(), getSelectors(row), loadFactory,
                                    saveFactory);
    }
}

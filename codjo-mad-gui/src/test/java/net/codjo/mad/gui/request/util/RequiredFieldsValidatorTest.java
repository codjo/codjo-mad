package net.codjo.mad.gui.request.util;
import net.codjo.mad.common.structure.DefaultStructureReader;
import net.codjo.mad.gui.request.AbstractDataSourceTestCase;
import net.codjo.mad.gui.request.DetailDataSource;
import javax.swing.JTextField;
/**
 *
 */
public class RequiredFieldsValidatorTest extends AbstractDataSourceTestCase {

    public void test_declare_requiredFields() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.setEntityName("tableData");
        dataSource.setStructureReader(new DefaultStructureReader(getClass().getResourceAsStream(
              "structure.xml")));

        dataSource.declare("portfolio", new JTextField());
        dataSource.declare("sensi", new JTextField());

        RequiredFieldsValidator validator = new RequiredFieldsValidator(dataSource);

        assertFalse(validator.isValid());

        dataSource.setFieldValue("portfolio", "POPO");
        assertTrue(validator.isValid());
    }
}

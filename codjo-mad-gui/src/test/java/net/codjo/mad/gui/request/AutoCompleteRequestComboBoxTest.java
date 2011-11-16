package net.codjo.mad.gui.request;

import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.JFCTestHelper;
import junit.extensions.jfcunit.TestHelper;
import junit.extensions.jfcunit.eventdata.KeyEventData;
import junit.extensions.jfcunit.eventdata.StringEventData;
import org.junit.Test;

public class AutoCompleteRequestComboBoxTest extends JFCTestCase {
    private JFrame window;
    private AutoCompleteRequestComboBox combo;



    @Override
    protected void setUp() {
        setHelper(new JFCTestHelper());
        window = new JFrame("Test " + getName());
    }


    @Override
    protected void tearDown() {
        TestHelper.disposeWindow(window, this);
        window = null;
        TestHelper.cleanUp(this);
    }


    @Test
    public void test_nom() throws Exception {
        combo = new AutoCompleteRequestComboBox();
        combo.setSortEnabled(false);
        String[] columns = new String[]{"LABEL", "CODE"};
        Result loadResult = buildResult(columns, new String[][]{{"a", "1"},
                                                                {"b", "2"},
                                                                {"ab", "3"},
                                                                {"cc", "4"}});
        window.getContentPane().add(combo, BorderLayout.CENTER);
        window.pack();
        window.setVisible(true);
        combo.getDataSource().setLoadResult(loadResult);
        combo.setModelFieldName("CODE");
        combo.setRendererFieldName("LABEL");

        Component editorComponent = combo.getEditor().getEditorComponent();
        getHelper().sendKeyAction(new KeyEventData(this, editorComponent, KeyEvent.VK_A));
        assertEquals(0, combo.getSelectedIndex());

        getHelper().sendKeyAction(new KeyEventData(this, editorComponent, KeyEvent.VK_B));
        assertEquals(2, combo.getSelectedIndex());
        getHelper().sendKeyAction(new KeyEventData(this, editorComponent, KeyEvent.VK_C));
        assertEquals(2, combo.getSelectedIndex());
    }


    @Test
    public void test_scroll() throws Exception {
        combo = new AutoCompleteRequestComboBox();
        combo.setSortEnabled(false);
        combo.setModelFieldName("CODE");
        combo.setRendererFieldName("LABEL");
        Result loadResult = buildResult(new String[]{"LABEL", "CODE"},
                                        new String[][]{{"d1", "Vd1"},
                                                       {"A A AGF TACTIQUE", "12794"},
                                                       {"A DIFFUSION", "2915"},
                                                       {"A NE PAS UNTILISER : UTILISER", "795"},
                                                       {"A NOVO", "6614"},
                                                       {"A P MOLLER - MAERSK A/S", "8158"},
                                                       {"A.A. AGF OBLIG EURO", "26353"},
                                                       {"AA SSGA ACTIONS INDICE EURO", "27613"},
                                                       {"AAA ACTIONS AGR ALIMENTAIR-D", "26858"},
                                                       {"AAADVISORS EUROPEAN EQUITIE", "27829"},
                                                       {"AAADVISORS MULTI ABSOLU-C", "27545"},
                                                       {"AAADVISORS WORLD", "23247"},
                                                       {"AALBERTS INDUSTRIES NV", "11765"},
                                                       {"AAON INC", "58322"},
                                                       {"AAR CORP", "58323"},
                                                       {"AAREAL BANK AG", "777"},
                                                       {"AARHUSKARLSHAMN AB", "61995"},
                                                       {"AARON RENTS INC", "58324"},
                                                       {"AAZ CAPITALISATION", "7975"},
                                                       {"AB INTL TECHNOLOGY PT-A$", "26222"},
                                                       {"AB S.A. (GROUPE)", "5189"},
                                                       {"ABARIS", "26759"},
                                                       {"ABAXIS INC", "58325"},
                                                       {"ABB INTERNATIONAL FINANCE LTD", "7389"},
                                                       {"ABB LTD", "5413"},
                                                       {"ABBEY NAT. TREASURY SERVICES", "4854"},
                                                       {"ABBEY NATIONAL PLC", "1352"},
                                                       {"ABBOTT LABORATORIES", "3772"},
                                                       {"z", "Vz"}});

        combo.getDataSource().setLoadResult(loadResult);
        combo.setModelFieldName("CODE");
        combo.setRendererFieldName("LABEL");

        window.getContentPane().add(combo, BorderLayout.CENTER);
        window.pack();
        window.setVisible(true);

        Component editorComponent = combo.getEditor().getEditorComponent();
        getHelper().sendKeyAction(new KeyEventData(this, editorComponent, KeyEvent.VK_A));

        assertEquals(1, combo.getSelectedIndex());

        getHelper().sendKeyAction(new KeyEventData(this, editorComponent, KeyEvent.VK_Z));
        assertEquals(1, combo.getSelectedIndex());

        getHelper().sendKeyAction(new KeyEventData(this, editorComponent, KeyEvent.VK_B));
        flushAWT();
        assertEquals(19, combo.getSelectedIndex());
    }


    public static void main(String[] args) {
        final JFrame frame = new JFrame(AutoCompleteRequestComboBoxTest.class.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        AutoCompleteRequestComboBox rc = new AutoCompleteRequestComboBox();
        rc.setSortEnabled(true);
        rc.setModelFieldName("CODE");
        rc.setRendererFieldName("LABEL");
        rc.setContainsNullValue(false);
        Result loadResult = buildResult(new String[]{"LABEL", "CODE"},
                                        new String[][]{{"d1", "Vd1"},
                                                       {"A A AGF TACTIQUE", "12794"},
                                                       {"A DIFFUSION", "2915"},
                                                       {"A NE PAS UNTILISER : UTILISER", "795"},
                                                       {"A NOVO", "6614"},
                                                       {"A P MOLLER - MAERSK A/S", "8158"},
                                                       {"A.A. AGF OBLIG EURO", "26353"},
                                                       {"AA SSGA ACTIONS INDICE EURO", "27613"},
                                                       {"AAA ACTIONS AGR ALIMENTAIR-D", "26858"},
                                                       {"AAADVISORS EUROPEAN EQUITIE", "27829"},
                                                       {"AAADVISORS MULTI ABSOLU-C", "27545"},
                                                       {"AAADVISORS WORLD", "23247"},
                                                       {"AALBERTS INDUSTRIES NV", "11765"},
                                                       {"AAON INC", "58322"},
                                                       {"AAR CORP", "58323"},
                                                       {"AAREAL BANK AG", "777"},
                                                       {"AARHUSKARLSHAMN AB", "61995"},
                                                       {"AARON RENTS INC", "58324"},
                                                       {"AAZ CAPITALISATION", "7975"},
                                                       {"AB INTL TECHNOLOGY PT-A$", "26222"},
                                                       {"AB S.A. (GROUPE)", "5189"},
                                                       {"ABARIS", "26759"},
                                                       {"ABAXIS INC", "58325"},
                                                       {"ABB INTERNATIONAL FINANCE LTD", "7389"},
                                                       {"ABB LTD", "5413"},
                                                       {"ABBEY NAT. TREASURY SERVICES", "4854"},
                                                       {"ABBEY NATIONAL PLC", "1352"},
                                                       {"ABBOTT LABORATORIES", "3772"},
                                                       {"z", "Vz"}});

        rc.getDataSource().setLoadResult(loadResult);
        rc.setPreferredSize(new Dimension(500, 25));

        mainPanel.add(rc);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static Result buildResult(String[] columnNames, String[][] rows) {
        Result loadResult = new Result();

        for (String[] row : rows) {
            Map<String, String> fields = new HashMap<String, String>();

            for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
                String columnName = columnNames[columnIndex];
                fields.put(columnName, row[columnIndex]);
            }

            loadResult.addRow(new Row(fields));
        }

        return loadResult;
    }
    
}

package net.codjo.mad.gui.request.util;
import net.codjo.gui.toolkit.date.NoNullDateField;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import org.uispec4j.Button;
import org.uispec4j.CheckBox;
import org.uispec4j.Key;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;
/**
 *
 */
public class UndoRedoTest extends UISpecTestCase {
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private Panel dateFieldWrapper;
    private TextBox textFieldWrapper;
    private CheckBox checkBoxWrapper;


    public void test_undoRedoSnapshotMode() throws Exception {
        DetailDataSource dataSource = new DetailDataSource(new DefaultGuiContext());

        final JCheckBox checkBox = new JCheckBox();
        final JTextField textField = new JTextField();
        final NoNullDateField dateField = new NoNullDateField();
        dateFieldWrapper = new Panel(dateField);
        textFieldWrapper = new TextBox(textField);
        checkBoxWrapper = new CheckBox(checkBox);

        dataSource.declare("checkbox", checkBox);
        dataSource.declare("textfield", textField);
        dataSource.declare("date", dateField);

        buttonPanelLogic.setMainDataSource(dataSource);

        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                if (checkBox.isSelected()) {
                    buttonPanelLogic.startSnapshotMode();
                    textField.setText("OK");
                    dateField.setDate(java.sql.Date.valueOf("2007-08-24"));
                    buttonPanelLogic.stopSnapshotMode();
                }
            }
        });

        assertGuiComponents(false, "", "");

        checkBoxWrapper.click();
        Thread.sleep(200);
        assertGuiComponents(true, "OK", "24-08-2007");

        textFieldWrapper.pressKey(Key.N);
        assertGuiComponents(true, "NOK", "24-08-2007");

        dateField.setDate(java.sql.Date.valueOf("2007-08-30"));
        assertGuiComponents(true, "NOK", "30-08-2007");

        undo();
        assertGuiComponents(true, "NOK", "24-08-2007");

        undo();
        assertGuiComponents(true, "OK", "24-08-2007");

        undo();
        assertGuiComponents(false, "", "");

        redo();
        assertGuiComponents(true, "OK", "24-08-2007");

        redo();
        assertGuiComponents(true, "NOK", "24-08-2007");

        redo();
        assertGuiComponents(true, "NOK", "30-08-2007");
    }


    private void assertGuiComponents(boolean expectedCheckBox, String expectedText, String expectedDate) {
        assertEquals(expectedCheckBox, checkBoxWrapper.isSelected());
        assertTrue(textFieldWrapper.textEquals(expectedText));
        assertDateField(expectedDate);
    }


    private void undo() throws InterruptedException {
        new Button(buttonPanelLogic.getGui().getUndoButton()).click();
        Thread.sleep(200);
    }


    private void redo() throws InterruptedException {
        new Button(buttonPanelLogic.getGui().getRedoButton()).click();
        Thread.sleep(200);
    }


    private void assertDateField(String expectedDate) {
        String[] expected = "".equals(expectedDate) ?
                            new String[]{"", "", ""} :
                            expectedDate.split("-");
        assertEquals(expected[0], dateFieldWrapper.getTextBox("date.dayField").getText());
        assertEquals(expected[1], dateFieldWrapper.getTextBox("date.monthField").getText());
        assertEquals(expected[2], dateFieldWrapper.getTextBox("date.yearField").getText());
    }
}

package net.codjo.mad.gui.structure;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
/**
 * Combo pour afficher une structure.
 */
public class StructureCombo extends JComboBox {
    public StructureCombo(Map structures) {
        setModel(new DefaultComboBoxModel(getKeySortedByValue(structures).toArray()));
        setRenderer(new StructureRenderer(structures));
    }


    private List<String> getKeySortedByValue(Map<?, ?> structures) {
        List<Map.Entry> entryList = new ArrayList<Map.Entry>(structures.entrySet());
        Collections.sort(entryList, new StructureComparatorByValue(structures));

        List<String> keySortedByValue = new ArrayList<String>();
        for (Map.Entry entry : entryList) {
            keySortedByValue.add(entry.getKey().toString());
        }
        return keySortedByValue;
    }


    private class StructureComparatorByValue extends StructureRenderer implements Comparator<Map.Entry> {

        StructureComparatorByValue(Map structures) {
            super(structures);
        }


        public int compare(Map.Entry entry1, Map.Entry entry2) {
            return transformValue(entry1.getValue()).compareToIgnoreCase(transformValue(entry2.getValue()));
        }
    }
}

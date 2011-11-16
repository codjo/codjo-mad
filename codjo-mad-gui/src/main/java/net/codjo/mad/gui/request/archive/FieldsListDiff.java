package net.codjo.mad.gui.request.archive;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/**
 * Classe utilitaire pour faire un diff entre 2 FieldsList.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class FieldsListDiff {
    public static final String FIELD_NAME_COLUMN = "fieldName";
    public static final String LEFT_VALUE_COLUMN = "leftValue";
    public static final String NOT_DEFINED = "n/a";
    public static final String RIGHT_VALUE_COLUMN = "rightValue";
    public static final String[] AUDIT_FIELDS =
        new String[] {
            "creationBy", "creationDatetime", "updateBy", "updateDatetime", "comment"
        };
    private FieldsList leftFields = new FieldsList();
    private FieldsList rightFields = new FieldsList();

    public void setLeftFields(FieldsList leftFields) {
        this.leftFields = leftFields;
    }


    public void setRightFields(FieldsList rightFields) {
        this.rightFields = rightFields;
    }


    public FieldsList getLeftFields() {
        return leftFields;
    }


    public FieldsList getRightFields() {
        return rightFields;
    }


    public Result diff() {
        return diff(new String[] {});
    }


    public Result diff(String[] exceptsFieldName) {
        Result result = new Result();

        TreeSet treeAllFieldNames = new TreeSet();
        addAllFieldNames(treeAllFieldNames, leftFields);
        addAllFieldNames(treeAllFieldNames, rightFields);

        treeAllFieldNames.removeAll(Arrays.asList(exceptsFieldName));

        // push audit fields to the end of diff list.
        List allFieldNames = new ArrayList(treeAllFieldNames);
        allFieldNames.removeAll(Arrays.asList(AUDIT_FIELDS));
        allFieldNames.addAll(Arrays.asList(AUDIT_FIELDS));

        for (Iterator i = allFieldNames.iterator(); i.hasNext();) {
            String fieldName = (String)i.next();
            String leftValue = getValue(leftFields, fieldName);
            String rightValue = getValue(rightFields, fieldName);

            if (isDifferent(leftValue, rightValue)) {
                addDiffResult(result, fieldName, leftValue, rightValue);
            }
        }
        return result;
    }


    private boolean isDifferent(String leftValue, String rightValue) {
        if (leftValue == null && (rightValue != null || !"".equals(rightValue))) {
            return true;
        }
        else if (rightValue != null && !leftValue.equals(rightValue)) {
            return true;
        }
        return false;
    }


    private String getValue(final FieldsList list, final String fieldName) {
        String value = NOT_DEFINED;
        if (list.contains(fieldName)) {
            value = list.getFieldValue(fieldName);
        }
        return value;
    }


    private void addAllFieldNames(final Set allFieldNames, final FieldsList list) {
        for (Iterator i = list.fieldNames(); i.hasNext();) {
            allFieldNames.add(i.next());
        }
    }


    private void addDiffResult(final Result result, final String fieldName,
        final String leftValue, final String rightValue) {
        Row row = new Row();
        row.addField(FIELD_NAME_COLUMN, fieldName);
        row.addField(LEFT_VALUE_COLUMN, "null".equals(leftValue) ? "" : leftValue);
        row.addField(RIGHT_VALUE_COLUMN, "null".equals(rightValue) ? "" : rightValue);
        result.addRow(row);
    }
}

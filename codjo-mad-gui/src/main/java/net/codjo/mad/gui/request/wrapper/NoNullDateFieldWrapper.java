package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.date.NoNullDateField;
import java.util.Date;
/**
 *
 */
public class NoNullDateFieldWrapper extends DateFieldWrapper {
    private final NoNullDateField noNullDateField;


    NoNullDateFieldWrapper(String fieldName, NoNullDateField noNullDateField) {
        super(fieldName, noNullDateField);
        this.noNullDateField = noNullDateField;
    }


    @Override
    public String getDisplayValue() {
        if (isNoNullDate()) {
            return "";
        }
        return super.getDisplayValue();
    }


    private boolean isNoNullDate() {
        Date currentDate = noNullDateField.getDate();
        Date infiniteDate = noNullDateField.getNoNullDate();
        return ((currentDate == null) && (infiniteDate == null) ||
                (currentDate != null) && currentDate.equals(infiniteDate));
    }
}

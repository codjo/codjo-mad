package net.codjo.mad.gui.request.util;
import net.codjo.mad.gui.request.DetailDataSource;
import java.util.Set;
/**
 *
 */
public class RequiredFieldsValidator implements ButtonLogicValidator {
    private DetailDataSource dataSource;


    public RequiredFieldsValidator(DetailDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public boolean isValid() {
        Set<String> fieldNames = dataSource.getDeclaredFields().keySet();
        for (String fieldName : fieldNames) {
            if (dataSource.isFieldRequired(fieldName) && dataSource.isFieldNull(fieldName)) {
                return false;
            }
        }
        return true;
    }
}

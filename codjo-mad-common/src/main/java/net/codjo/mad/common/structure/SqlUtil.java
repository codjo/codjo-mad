package net.codjo.mad.common.structure;
import net.codjo.mad.common.Log;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class SqlUtil {
    private Map<Integer, String> sqlToString;
    private Map<String, Integer> stringToSql;

    public SqlUtil() {}

    public String sqlTypeToString(int sqlTypes) {
        if (sqlToString == null) {
            initConvertionMap();
        }
        return sqlToString.get(sqlTypes);
    }


    public int stringToSqlType(String str) {
        String upperStr = str.toUpperCase();
        if (stringToSql == null) {
            initConvertionMap();
        }
        if (!stringToSql.containsKey(upperStr)) {
            String errorMsg = "Le type SQL " + str + " est inconnue !";
            Log.error(errorMsg);
            Log.info("Type SQL connue  = " + stringToSql.keySet());
            throw new IllegalArgumentException(errorMsg);
        }
        return stringToSql.get(upperStr);
    }


    private void initConvertionMap() {
        sqlToString = new HashMap<Integer, String>();
        stringToSql = new HashMap<String, Integer>();
        try {
            Field[] field = Types.class.getDeclaredFields();
            for (Field aField : field) {
                Integer sqlType = (Integer)aField.get(null);
                sqlToString.put(sqlType, aField.getName());
                stringToSql.put(aField.getName(), sqlType);
            }
        }
        catch (Exception ex) {
            Log.error(ex);
            throw new RuntimeException(ex.getLocalizedMessage());
        }
    }
}

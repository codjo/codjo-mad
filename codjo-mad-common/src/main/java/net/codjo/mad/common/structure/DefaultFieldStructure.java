package net.codjo.mad.common.structure;
/**
 * Description de la structure d'un champ.
 */
public class DefaultFieldStructure implements FieldStructure {
    private String label;
    private String referentialTypeName;
    private String sqlName;
    private String javaName;
    private int sqlType;
    private String sqlPrecision;
    private boolean sqlRequired;
    private boolean sqlPrimaryKey;
    private boolean functionalKey;


    public DefaultFieldStructure(String label, String sqlName, String referentialTypeName) {
        this(label, null, sqlName, referentialTypeName, java.sql.Types.JAVA_OBJECT);
    }


    public DefaultFieldStructure(String label,
                                 String javaName,
                                 String sqlName,
                                 String referentialTypeName,
                                 int sqlType) {

        this(label, javaName, sqlName, referentialTypeName, sqlType, null, false, false);
    }


    public DefaultFieldStructure(String label,
                                 String javaName,
                                 String sqlName,
                                 String referentialTypeName,
                                 int sqlType,
                                 String sqlPrecision,
                                 boolean sqlRequired,
                                 boolean sqlPrimaryKey,
                                 boolean functionalKey) {
        this.label = label;
        this.javaName = javaName;
        this.sqlName = sqlName;
        this.referentialTypeName = referentialTypeName;
        this.sqlType = sqlType;
        this.sqlPrecision = sqlPrecision;
        this.sqlRequired = sqlRequired;
        this.sqlPrimaryKey = sqlPrimaryKey;
        this.functionalKey = functionalKey;
    }


    public DefaultFieldStructure(String label,
                                 String javaName,
                                 String sqlName,
                                 String referentialTypeName,
                                 int sqlType,
                                 String sqlPrecision,
                                 boolean sqlRequired,
                                 boolean sqlPrimaryKey) {
        this(label,
             javaName,
             sqlName,
             referentialTypeName,
             sqlType,
             sqlPrecision,
             sqlRequired,
             sqlPrimaryKey,
             false);
    }


    public String getJavaName() {
        return javaName;
    }


    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public boolean isLinkedToPerson() {
        return PERSON_REF.equals(referentialTypeName);
    }


    public boolean isLinkedToReferential() {
        return REFERENTIAL_REF.equals(referentialTypeName);
    }


    public String getReferentialTypeName() {
        return referentialTypeName;
    }


    public void setReferentialTypeName(String referentialTypeName) {
        this.referentialTypeName = referentialTypeName;
    }


    public String getSqlName() {
        return sqlName;
    }


    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }


    public int getSqlType() {
        return sqlType;
    }


    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }


    public String getSqlPrecision() {
        return sqlPrecision;
    }


    public void setSqlPrecision(String sqlPrecision) {
        this.sqlPrecision = sqlPrecision;
    }


    public boolean isSqlRequired() {
        return sqlRequired;
    }


    public void setSqlRequired(boolean sqlRequired) {
        this.sqlRequired = sqlRequired;
    }


    public boolean isSqlPrimaryKey() {
        return sqlPrimaryKey;
    }


    public boolean isFunctionalKey() {
        return functionalKey;
    }


    public void setSqlPrimaryKey(boolean sqlPrimaryKey) {
        this.sqlPrimaryKey = sqlPrimaryKey;
    }


    @Override
    public String toString() {
        if (getLabel() != null) {
            return getLabel();
        }
        else {
            return getSqlName();
        }
    }


    public int compareTo(Object obj) {
        return getLabel().compareTo(((Structure)obj).getLabel());
    }
}

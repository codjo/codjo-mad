package net.codjo.mad.common.structure;

public interface FieldStructure extends Structure {
    public static final String PERSON_REF = "Person";
    public static final String REFERENTIAL_REF = "Referential";


    String getJavaName();


    boolean isLinkedToPerson();


    boolean isLinkedToReferential();


    String getReferentialTypeName();


    int getSqlType();


    String getSqlPrecision();


    boolean isSqlRequired();


    boolean isSqlPrimaryKey();


    boolean isFunctionalKey();
}

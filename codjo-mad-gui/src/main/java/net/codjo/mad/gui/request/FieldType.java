package net.codjo.mad.gui.request;
/**
 * Type de Champ.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public final class FieldType {
    public static final String EDIT_MODE = "EDIT";
    /** Le champ n'est pas editable par le User, mais est enregistré */
    public static final FieldType NOT_EDITABLE = new FieldType("NO_EDIT");
    /**
     * Le champ n'est pas editable par le User, mais inseré lors de la création. Cas
     * typique, les PK.
     */
    public static final FieldType NOT_UPDATABLE = new FieldType("NO_UPD");
    /** Le type d'est pas spécifié. Cas standard */
    public static final FieldType N_A = new FieldType("N_A");
    /** Le champ n'est pas editable par le User. Cas typique, les champs calculés. */
    public static final FieldType READ_ONLY = new FieldType("READ_ONLY");
    private String name;

    private FieldType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

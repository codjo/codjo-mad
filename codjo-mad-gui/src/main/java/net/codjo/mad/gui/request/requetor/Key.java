package net.codjo.mad.gui.request.requetor;
/**
 * Une clef de la jointure.
 */
public class Key {
    private String fromField = null;
    private String toField = null;
    private String operatorField = "=";


    public Key(String from, String to) {
        this.fromField = from;
        this.toField = to;
    }


    public Key(String from, String to, String operator) {
        this(from, to);
        if (operator != null) {
            this.operatorField = operator;
        }
    }


    public String getFromField() {
        return fromField;
    }


    public String getToField() {
        return toField;
    }


    public String getOperatorField() {
        return operatorField;
    }
}

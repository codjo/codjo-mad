package net.codjo.mad.client.request;
/**
 * Classe décrivant la balise field.
 */
public class Field {
    private String name;
    private String value;


    public Field(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public Field() {
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "<name=" + name + " - value=" + value + ">";
    }

/*

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Field field = (Field)object;
        boolean comparaison = (name != null ? name.equals(field.name) : field.name == null);
        comparaison = comparaison && (value == null ? field.value == null : value.equals(field.value));
        return comparaison;
    }


    @Override
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
*/
}

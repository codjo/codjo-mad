package net.codjo.mad.gui.request.requetor;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
/**
 * Cette classe gère 6 listes en parallèle de la sqlList de la classe SqlRequetor. Pour un index donné,
 * l'ensemble des éléments de ces listes correspond à la ligne de même index de la sqlList.
 */
class SqlRequetorRequest {
    static final int EQUAL = 0;
    static final int SUP = 1;
    static final int SUP_EQUAL = 2;
    static final int INF = 3;
    static final int INF_EQUAL = 4;
    static final int DIFFERENT = 5;
    static final int BEGIN_BY = 6;
    static final int NOT_BEGIN_BY = 7;
    static final int END_BY = 8;
    static final int NOT_END_BY = 9;
    static final int CONTAIN = 10;
    static final int NOT_CONTAIN = 11;
    static final int NULL = 12;
    static final int NOT_NULL = 13;

    private static final String QUOTE = "'";
    private static final String PERCENT_QUOTE = "%'";
    private static final String QUOTE_PERCENT = "'%";

    //L'opérateur logique (and ou or)
    private List<String> logicalOper = new ArrayList<String>();

    //L'objet links
    private List<Link> links = new ArrayList<Link>();

    //Le nom physique du champ de la links courante
    private List<String> field = new ArrayList<String>();

    //L'opérateur de comparaison (=, like, ...)
    private List<Integer> compareOper = new ArrayList<Integer>();

    //Le préfixe de la valeur du champ(', '%, ...)
    private List<String> prefixValue = new ArrayList<String>();

    //La valeur du champ
    private List<String> value = new ArrayList<String>();

    //Le suffixe de la valeur du champ(', %', ...)
    private List<String> suffixValue = new ArrayList<String>();

    //La jointure gauche
    private boolean leftJoin;


    SqlRequetorRequest() {
    }


    /**
     * Constructor par copie
     *
     * @param req Description of Parameter
     */
    SqlRequetorRequest(SqlRequetorRequest req) {
        logicalOper = new ArrayList<String>(req.logicalOper);
        links = new ArrayList<Link>(req.links);
        field = new ArrayList<String>(req.field);
        compareOper = new ArrayList<Integer>(req.compareOper);
        prefixValue = new ArrayList<String>(req.prefixValue);
        value = new ArrayList<String>(req.value);
        suffixValue = new ArrayList<String>(req.suffixValue);
        leftJoin = req.leftJoin;
    }


    /**
     * Met à jour l'opérateur logique à l'index donné.
     *
     * @param newLogicalOper La valeur de l'opérateur
     * @param idx            L'index
     */
    public void setLogicalOper(String newLogicalOper, int idx) {
        logicalOper.set(idx, newLogicalOper);
    }


    /**
     * Met à jour le nom physique du champ à l'index donné.
     *
     * @param newField Le nom du champ
     * @param idx      L'index
     */
    public void setField(String newField, int idx) {
        field.set(idx, newField);
    }


    /**
     * Met à jour l'opérateur de comparaison à l'index donné.
     *
     * @param newCompareOper La valeur de l'opérateur
     * @param idx            L'index
     */
    public void setCompareOper(int newCompareOper, int idx) {
        compareOper.set(idx, newCompareOper);
    }


    /**
     * Met à jour le préfixe de la valeur du champ à l'index donné.
     *
     * @param newPrefixValue La valeur du préfixe
     * @param idx            L'index
     */
    public void setPrefixValue(String newPrefixValue, int idx) {
        prefixValue.set(idx, newPrefixValue);
    }


    /**
     * Met à jour la valeur du champ à l'index donné.
     *
     * @param newValue La valeur du champ
     * @param idx      L'index
     */
    public void setValue(String newValue, int idx) {
        value.set(idx, addQuote(newValue));
    }


    /**
     * Met à jour le suffixe de la valeur du champ à l'index donné.
     *
     * @param newSuffixValue La valeur du suffixe
     * @param idx            L'index
     */
    public void setSuffixValue(String newSuffixValue, int idx) {
        suffixValue.set(idx, newSuffixValue);
    }


    /**
     * Met à jour si la requête utilise une jointure gauche ou pas.
     *
     * @param isLeftJoin True sijointure gauche, False sinon
     */
    public void setLeftJoin(boolean isLeftJoin) {
        leftJoin = isLeftJoin;
    }


    /**
     * Retourne l'opérateur logique à l'index donné.
     *
     * @param idx L'index
     *
     * @return L'opérateur
     */
    public String getLogicalOper(int idx) {
        return logicalOper.get(idx);
    }


    /**
     * Retourne le nom physique du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return Le nom du champ
     */
    public String getField(int idx) {
        return field.get(idx);
    }


    /**
     * Retourne la taille des listes de la requête.
     *
     * @return La taille des listes.
     */
    public int getRequestListSize() {
        return field.size();
    }


    /**
     * Retourne l'opérateur de comparaison à l'index donné.
     *
     * @param idx L'index
     *
     * @return L'opérateur
     */
    public int getCompareOperValue(int idx) {
        return compareOper.get(idx);
    }


    /**
     * Retourne l'opérateur de comparaison à l'index donné.
     *
     * @param idx L'index
     *
     * @return L'opérateur
     */
    public String getCompareOperTraducValue(int idx) {
        int oper = getCompareOperValue(idx);
        if (oper != -1) {
            return traductOperator(oper);
        }
        else {
            return "";
        }
    }


    /**
     * Retourne le préfixe de la valeur du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return Le préfixe
     */
    public String getPrefixValue(int idx) {
        return prefixValue.get(idx);
    }


    /**
     * Retourne la valeur du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return La valeur du champ
     */
    public String getValue(int idx) {
        return value.get(idx);
    }


    /**
     * Retourne le suffixe de la valeur du champ à l'index donné.
     *
     * @param idx L'index
     *
     * @return Le suffixe
     */
    public String getSuffixValue(int idx) {
        return suffixValue.get(idx);
    }


    /**
     * Retourne si la requête utilise une jointure gauche ou pas.
     *
     * @return True si jointure gauche, False sinon
     */
    public boolean getLeftJoin() {
        return leftJoin;
    }


    /**
     * Retourne l'ensemble des éléments des listes à l'index donné.
     *
     * @param idx L'index
     *
     * @return Les éléments des listes
     */
    public String getRequest(int idx) {
        StringBuilder str = new StringBuilder();
        if (!logicalOper.isEmpty() && logicalOper.size() > idx) {
            str.append(getLogicalOper(idx));
        }
        if ((!links.isEmpty() && links.size() > idx) && (!field.isEmpty() && field.size() > idx)) {
            Link link = getLink(idx);
            if (link != null) {
                str.append(link.completeSqlFieldName(getField(idx)));
            }
            else {
                str.append(getField(idx));
            }
        }

        if (!compareOper.isEmpty() && compareOper.size() > idx) {
            str.append(getCompareOperTraducValue(idx));
        }
        if (!prefixValue.isEmpty() && prefixValue.size() > idx) {
            str.append(getPrefixValue(idx));
        }
        if (!value.isEmpty() && value.size() > idx) {
            str.append(getValue(idx));
        }
        if (!suffixValue.isEmpty() && suffixValue.size() > idx) {
            str.append(getSuffixValue(idx));
        }
        return str.toString();
    }


    /**
     * Overview.
     *
     * <p> Description </p>
     *
     * @param idx Description of Parameter
     */
    public void removeElements(int idx) {
        logicalOper.remove(idx);
        field.remove(idx);
        compareOper.remove(idx);
        prefixValue.remove(idx);
        value.remove(idx);
        suffixValue.remove(idx);
        links.remove(idx);
    }


    /**
     * Ajoute un élément vide à chacune des listes pour l'index donné.
     *
     * @param idx L'index
     */
    public void addElements(int idx) {
        logicalOper.add(idx, "");
        field.add(idx, "");
        compareOper.add(idx, -1);
        prefixValue.add(idx, "");
        value.add(idx, "");
        suffixValue.add(idx, "");
        links.add(idx, null);
    }


    /**
     * Supprime tous les éléments des listes.
     */
    public void removeAllElements() {
        for (int i = 0; i < value.size(); i++) {
            removeElements(i);
        }
    }


    /**
     * Met à jour l'objet links à l'index donné.
     *
     * @param newLink L'objet links
     * @param idx     L'index
     */
    void setLink(Link newLink, int idx) {
        links.set(idx, newLink);
    }


    /**
     * Retourne l'objet links à l'index donné
     *
     * @param linkIndex L'index
     *
     * @return L'objet links
     */
    Link getLink(int linkIndex) {
        return links.get(linkIndex);
    }


    /**
     * Met à jour le préfixe et le suffixe d'une valeur pour l'index donné en fonction de l'opérateur de
     * comparaison et du type SQL du champ.
     *
     * @param oper    L'opérateur de comparaison.
     * @param idx     L'index.
     * @param sqlType Le type SQL du champ.
     */
    void updatePrefSuffValue(int oper, int idx, int sqlType) {
        if (oper == CONTAIN || oper == NOT_CONTAIN) {
            setPrefixValue(QUOTE_PERCENT, idx);
            setSuffixValue(PERCENT_QUOTE, idx);
        }
        else if (oper == BEGIN_BY || oper == NOT_BEGIN_BY) {
            setPrefixValue(QUOTE, idx);
            setSuffixValue(PERCENT_QUOTE, idx);
        }
        else if (oper == END_BY || oper == NOT_END_BY) {
            setPrefixValue(QUOTE_PERCENT, idx);
            setSuffixValue(QUOTE, idx);
        }
        else if (sqlType == Types.BIT
                 || isNumeric(sqlType)
                 || oper == NULL
                 || oper == NOT_NULL) {
            setPrefixValue("", idx);
            setSuffixValue("", idx);
        }
        else {
            setPrefixValue(QUOTE, idx);
            setSuffixValue(QUOTE, idx);
        }
    }


    /**
     * Adds a feature to the Quote attribute of the SqlRequetorRequest object
     *
     * @param param The feature to be added to the Quote attribute
     *
     * @return Description of the Returned Value
     */
    private String addQuote(String param) {
        StringBuilder tmp = new StringBuilder(param);
        char quote = '\'';
        int index = 0;
        while (index < tmp.length()) {
            if (tmp.charAt(index) == quote) {
                tmp.insert(index, quote);
                index++;
            }
            index++;
        }
        return tmp.toString();
    }


    /**
     * Traduit les opérateurs de comparaison en "langage Sybase".
     *
     * @param oper L'opérateur sélectioné dans la liste.
     *
     * @return La valeur traduite.
     *
     * @throws IllegalArgumentException si <code>oper</code> est un opérateur inconnu
     */
    private String traductOperator(int oper) {
        String strOper;
        switch (oper) {
            case EQUAL:
                strOper = " = ";
                break;
            case SUP:
                strOper = " > ";
                break;
            case SUP_EQUAL:
                strOper = " >= ";
                break;
            case INF:
                strOper = " < ";
                break;
            case INF_EQUAL:
                strOper = " <= ";
                break;
            case DIFFERENT:
                strOper = " <> ";
                break;
            case BEGIN_BY:
                strOper = " like ";
                break;
            case NOT_BEGIN_BY:
                strOper = " not like ";
                break;
            case END_BY:
                strOper = " like ";
                break;
            case NOT_END_BY:
                strOper = " not like ";
                break;
            case CONTAIN:
                strOper = " like ";
                break;
            case NOT_CONTAIN:
                strOper = " not like ";
                break;
            case NULL:
                strOper = " is null ";
                break;
            case NOT_NULL:
                strOper = " is not null ";
                break;
            default:
                throw new IllegalArgumentException("Operateur inconnu");
        }
        return strOper;
    }


    /**
     * Indique si le type SQL est de format numerique.
     *
     * @param sqlType type sql.
     *
     * @return <code>true</code> si oui.
     */
    private static boolean isNumeric(int sqlType) {
        return sqlType == Types.INTEGER || sqlType == Types.NUMERIC
               || sqlType == Types.FLOAT || sqlType == Types.DOUBLE || sqlType == Types.DECIMAL
               || sqlType == Types.BIGINT || sqlType == Types.REAL || sqlType == Types.SMALLINT
               || sqlType == Types.TINYINT;
    }
}

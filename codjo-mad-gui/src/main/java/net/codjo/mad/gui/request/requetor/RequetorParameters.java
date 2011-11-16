package net.codjo.mad.gui.request.requetor;
import net.codjo.mad.common.structure.FieldStructure;
import net.codjo.mad.common.structure.StructureReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Paramétrages nécessaires au fonctionnement de requêteur. Ces paramétres proviennent des fichiers structure,
 * datagen et préférence.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.11 $
 */
class RequetorParameters {
    private LinkFamily family;
    private String[] colNamesForSelect;
    private final Link rootLink;


    /**
     * Constructeur
     *
     * @param family            la famille que l'on requete.
     * @param colNamesForSelect Tableau des noms des champs (format Java) à retourner par le select (champs
     *                          issus du <code>preference.xml</code>)
     */
    RequetorParameters(StructureReader structureReader, LinkFamily family,
                       String[] colNamesForSelect) {
        this.family = family;
        this.colNamesForSelect = colNamesForSelect;
        rootLink = new Link(null, family.getRoot(), structureReader);
    }


    /**
     * Retourne la structure de la table <code>tableName</code>.
     *
     * @param tableName nom de la table
     *
     * @return la structure de la table
     *
     * @throws IllegalArgumentException Erreur
     */
    public Link getLink(String tableName) {
        if (tableName.equals(getRootLink().getTo())) {
            return getRootLink();
        }

        Link[] links = getLinks();
        for (Link link : links) {
            if (tableName.equals(link.getTo())) {
                return link;
            }
        }

        throw new IllegalArgumentException("Structure de la table " + tableName
                                           + " est inconnue");
    }


    public Link getRootLink() {
        return rootLink;
    }


    /**
     * Retourne les jointures liées.
     *
     * @return Les jointures liées
     */
    public Link[] getLinks() {
        Link[] links = new Link[family.size()];
        for (int i = 0; i < family.size(); i++) {
            links[i] = family.getLink(i);
        }
        return links;
    }


    /**
     * Retourne les clés de jointure de la table maître vers la table liée.
     *
     * @param link table liée.
     *
     * @return Colonne de gauche : table maître, colonne de droite : table liée.
     */
    public String[][] getJoinKeyToRootTableFor(Link link) {
        return link.keysToArray();
    }


    /**
     * Génère la clause SELECT de la requête du requêteur.
     *
     * @return la clause SELECT.
     */
    String getSelectClause() {
        StringBuffer buffer = new StringBuffer("SELECT ");
        for (int i = 0; i < colNamesForSelect.length; i++) {
            buffer.append(getFullSqlFieldName(colNamesForSelect[i]));
            if (i + 1 < colNamesForSelect.length) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }


    /**
     * Retourne le nom SQL du champ Java passé en paramétre préfixé par le nom de la table à laquelle il
     * appartient. Si ce champ est présent dans la table maître, on retourne celle-ci, sinon on retourne la
     * première des tables liées auxquelles il appartient.
     *
     * @param javaFieldName Le nom Java du champ
     *
     * @return Le nom SQL complet (TABLE.CHAMP)
     */
    private String getFullSqlFieldName(String javaFieldName) {
        Link link;
        if (isRootTableField(javaFieldName)) {
            link = getRootLink();
        }
        else {
            link = getLinkContainingField(javaFieldName);
        }

        return link.getFullSqlFieldName(javaFieldName);
    }


    /**
     * Renvoie la collection des tables liées utilisées dans la clause SELECT
     *
     * @return une collection de {@link Link}
     */
    Collection<Link> getLinksUsedInSelectClause() {
        Set<Link> linkSet = new HashSet<Link>();
        for (String javaFieldName : colNamesForSelect) {
            if (!isRootTableField(javaFieldName)) {
                Link link = getLinkContainingField(javaFieldName);
                linkSet.add(link);
            }
        }
        return linkSet;
    }


    /**
     * Renvoie la collection des champs des tables liées utilisés dans la clause SELECT
     *
     * @return une collection de {@link FieldStructure}
     */
    Collection<Object> getLinkedFieldsUsedInSelectClause() {
        List<Object> fieldSet = new ArrayList<Object>(colNamesForSelect.length);
        for (String javaFieldName : colNamesForSelect) {
            if (!isRootTableField(javaFieldName)) {
                Link link = getLinkContainingField(javaFieldName);
                fieldSet.add(link.getFieldByJava(javaFieldName));
            }
        }
        return fieldSet;
    }


    /**
     * Détermine si un champ appartient à la table maître.
     *
     * @param javaFieldName Nom Java du champ
     *
     * @return true si le champ appartient à la table maître, sinon false.
     */
    private boolean isRootTableField(String javaFieldName) {
        FieldStructure fieldRoot =
              getRootLink().getToTable().getFieldByJava(javaFieldName);
        return fieldRoot != null;
    }


    public String[] getColNamesForSelect() {
        return colNamesForSelect;
    }


    /**
     * Retourne la Link de la première table liée contenant le champ javaFieldName.
     *
     * @param javaFieldName Le champ de recherche.
     *
     * @return Une {@link Link}
     *
     * @throws IllegalArgumentException si le champ est inconnu.
     */
    private Link getLinkContainingField(String javaFieldName) {
        Link[] links = getLinks();
        for (Link currentLink : links) {
            if (currentLink.containsField(javaFieldName)) {
                return currentLink;
            }
        }

        throw new IllegalArgumentException("Le champ '" + javaFieldName
                                           + "' est inconnu.");
    }
}

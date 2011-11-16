package net.codjo.mad.gui.request.requetor;
import java.util.ArrayList;
import java.util.List;
/**
 * Famille de lien.
 */
public class LinkFamily {
    private String id;
    private List links = new ArrayList();
    private String root;

    public LinkFamily(String id, String root) {
        this.id = id;
        this.root = root;
    }

    public Link getLink(int idx) {
        return (Link)links.get(idx);
    }


    public String getId() {
        return id;
    }


    public int size() {
        return links.size();
    }


    public String getRoot() {
        return root;
    }


    public void addLink(Link link) {
        links.add(link);
    }
}

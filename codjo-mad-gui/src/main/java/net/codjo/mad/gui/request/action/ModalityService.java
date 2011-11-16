package net.codjo.mad.gui.request.action;
import net.codjo.gui.toolkit.util.Modal;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

public class ModalityService {
    public void apply(JComponent componentInFather, final JInternalFrame modalFrame) {
        JInternalFrame root = findFirstAncestor(JInternalFrame.class, componentInFather);
        if (root != null) {
            Modal.applyModality(root, modalFrame);
        }
    }


    protected <T extends Component> T findFirstAncestor(Class<T> searchedComponentType,
                                                        Component component) {
        for (Container parent = component.getParent(); parent != null;
             parent = parent.getParent()) {
            if (searchedComponentType.isInstance(parent)) {
                //noinspection unchecked
                return (T)parent;
            }
        }
        return null;
    }
}

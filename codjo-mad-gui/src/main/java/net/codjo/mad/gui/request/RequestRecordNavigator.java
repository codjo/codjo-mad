package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.framework.GuiContext;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class RequestRecordNavigator extends JButton {
    private RequestTable table;
    private MenuSelectionListener menuSelectionListener = new MenuSelectionListener();
    private MyDataSourceListener dataSourceListener = new MyDataSourceListener();
    private boolean enableComboListener = true;
    private JPopupMenu popupList = new JPopupMenu();
    private int selectedPage;
    private int numberOfPages;
    private JTextField pageTextField = new JTextField();
    private static final int MAX_ITEM_DISPLAYED = 10;


    public RequestRecordNavigator() {
        initGui();

        addMouseListener(new ClickOnButtonListener());
        pageTextField.addKeyListener(new PageTextFieldKeyListener());
    }


    public void initialize(RequestTable requestTable, GuiContext guiContext) {
        if (table != null) {
            table.getDataSource().removeDataSourceListener(dataSourceListener);
        }

        table = requestTable;

        table.getDataSource().addDataSourceListener(dataSourceListener);
        TranslationNotifier notifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        notifier.addInternationalizableComponent(this,
                                                 null,
                                                 RequestRecordNavigator.class.getName() + ".tooltip");

        computePagesPosition();
        setName(requestTable.getName() + ".RequestRecordNavigator");
        popupList.setName(requestTable.getName() + ".RequestRecordNavigator.List");
    }


    public void setSelectedPage(int selectedPage) {
        table.getDataSource().setCurrentPage(selectedPage);
        selectedPage = table.getDataSource().getCurrentPage();
        try {
            table.getDataSource().load();
        }
        catch (RequestException exception) {
            throw new UnsupportedOperationException(exception.getMessage());
        }
        this.selectedPage = selectedPage;
    }


    public int getSelectedPage() {
        return selectedPage;
    }


    public int getNumberOfPages() {
        return numberOfPages;
    }


    private void initGui() {
        Dimension minimumSize = new Dimension(25, 50);
        this.setMinimumSize(minimumSize);
        this.setPreferredSize(minimumSize);
        this.setOpaque(false);
        Icon icon = UIManager.getIcon("mad.gotopage");
        setIcon(icon);
        setToolTipText("Aller à la page");
    }


    private void computePagesPosition() {
        ListDataSource listDataSource = table.getDataSource();
        int totalRowCount = listDataSource.getTotalRowCount();

        enableComboListener = false;
        if (table.getRowCount() == 0) {
            this.setEnabled(false);
        }
        else {
            int pageSize = listDataSource.getPageSize();
            this.setEnabled(true);
            int nbPage = totalRowCount / pageSize;
            nbPage = nbPage + (totalRowCount % pageSize > 0 ? 1 : 0);

            numberOfPages = nbPage;
            selectedPage = listDataSource.getCurrentPage();

            popupList.removeAll();
            ButtonGroup group = new ButtonGroup();
            if (nbPage > MAX_ITEM_DISPLAYED) {
                nbPage = MAX_ITEM_DISPLAYED;
            }
            for (int page = 1; page <= nbPage; page++) {
                JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(String.valueOf(page),
                                                                   (page == selectedPage));
                menuItem.addActionListener(menuSelectionListener);
                menuItem.setName(popupList.getName() + "." + page);
                popupList.add(menuItem);
                group.add(menuItem);
            }
            if (numberOfPages > MAX_ITEM_DISPLAYED) {
                popupList.add(pageTextField);
                String strValue = String.valueOf(selectedPage);
                pageTextField.setText(strValue);
                pageTextField.setSelectionStart(0);
                pageTextField.setSelectionEnd(strValue.length());
            }
        }
        enableComboListener = true;
    }


    private class ClickOnButtonListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            if (popupList.isVisible()) {
                popupList.setVisible(false);
            }
            else {
                popupList.show(event.getComponent(), 0, event.getComponent().getHeight());
                pageTextField.requestFocusInWindow();
            }
        }
    }

    private class MenuSelectionListener implements ActionListener {
        public synchronized void actionPerformed(ActionEvent event) {
            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)event.getSource();

            if (menuItem != null && enableComboListener) {
                String value = menuItem.getText();
                setSelectedPage(Integer.parseInt(value));
                pageTextField.setText(value);
                pageTextField.setSelectionStart(0);
                pageTextField.setSelectionEnd(value.length());
            }
        }
    }

    private class PageTextFieldKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent event) {
            char character = event.getKeyChar();
            if (character == KeyEvent.VK_ENTER) {
                try {
                    int page = Integer.parseInt(pageTextField.getText());
                    if (page < 1) {
                        setSelectedPage(1);
                    }
                    else if (page > getNumberOfPages()) {
                        setSelectedPage(getNumberOfPages());
                    }
                    else {
                        setSelectedPage(page);
                    }
                    popupList.setVisible(false);
                }
                catch (NumberFormatException ex) {
                    ErrorDialog.show(null, "Erreur de saisie", ex);
                    pageTextField.setText(String.valueOf(getSelectedPage()));
                }
            }
        }
    }

    private class MyDataSourceListener extends DataSourceAdapter {
        @Override
        public void loadEvent(DataSourceEvent event) {
            computePagesPosition();
        }
    }
}

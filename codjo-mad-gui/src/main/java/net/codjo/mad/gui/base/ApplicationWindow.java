package net.codjo.mad.gui.base;
import net.codjo.gui.toolkit.progressbar.ProgressBarLabel;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.Internationalizable;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.GuiEvent;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.util.ApplicationData;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import org.apache.log4j.Logger;

class ApplicationWindow extends JFrame implements Observer, Internationalizable {
    private static final Logger LOGGER =
          Logger.getLogger(ApplicationWindow.class.getName());
    private final ApplicationData applicationData;
    private DefaultGuiContext guiContext;
    private JPanel statusBarPanel;
    private ProgressBarLabel statusBarLabel;
    private TranslationNotifier notifier;
    private TranslationManager translationManager;


    ApplicationWindow(String title, ApplicationData applicationData) {
        super(title);
        configureLookAndFeel();
        this.applicationData = applicationData;
        setSize(1200, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        loadCommonIcon("exit", "exit.png");
        loadCommonIcon("cut", "cut.png");
        loadCommonIcon("copy", "copy.png");
        loadCommonIcon("paste", "paste.png");
        loadCommonIcon("undo", "undo.png");
        loadCommonIcon("redo", "redo.png");
        loadCommonIcon("find", "find.png");
        loadCommonIcon("delete", "delete.png");

        setApplicationIcon();

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        statusBarLabel = new ProgressBarLabel();
        initStatusBar();

        JDesktopPane desktop = createMiddleComponent(contentPane);
        createGuiContext(desktop);
    }


    public JPanel getStatusBar() {
        return statusBarPanel;
    }


    protected JDesktopPane createMiddleComponent(JPanel contentPane) {
        JDesktopPane desktop = new JDesktopPane();
        contentPane.add(desktop, BorderLayout.CENTER);
        return desktop;
    }


    public void update(Observable observable, Object argument) {
        if (argument == GuiEvent.QUIT) {
            // begin WARNING : Astuce de liberation de la mémoire
            if (getJMenuBar() != null) {
                getJMenuBar().removeAll();
                setJMenuBar(null);
            }
            dispose();
            // end WARNING
        }
    }


    protected final DefaultGuiContext getGuiContext() {
        return guiContext;
    }


    private void loadCommonIcon(String propertyName, String iconName) {
        UIManager.put(propertyName, GuiUtil.getIcon(iconName));
    }


    private void initStatusBar() {
        statusBarLabel.setName("progressBar");
        statusBarLabel.setRequestFocusEnabled(false);

        Border statusBorder =
              BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(
                    BevelBorder.LOWERED,
                    Color.white,
                    Color.white,
                    new Color(93, 93, 93),
                    new Color(134, 134, 134)), BorderFactory.createEmptyBorder(2, 5, 2, 0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(statusBorder);
        panel.add(statusBarLabel, BorderLayout.WEST);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
        flowLayout.setVgap(0);
        statusBarPanel = new JPanel(flowLayout);
        panel.add(statusBarPanel, BorderLayout.EAST);

        getContentPane().add(panel, BorderLayout.SOUTH);
    }


    private void updateStatusBar() {
        statusBarLabel.setToolTipText(
              translationManager.translate("ApplicationWindow.statusBarLabel.tooltip",
                                           notifier.getLanguage()));
        statusBarLabel.setText(translationManager.translate("ApplicationWindow.statusBarLabel",
                                                            notifier.getLanguage())
                               + " " + getTitle());
    }


    protected void createGuiContext(JDesktopPane desktop) {
        guiContext = new DefaultGuiContext();
        guiContext.putAllProperties(applicationData.getData());
        guiContext.setDesktopPane(desktop);
        guiContext.setInfoLabel(statusBarLabel);
        guiContext.addObserver(this);
        guiContext.setMainFrame(this);

        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent event) {
                removeWindowListener(this);
                guiContext.sendEvent(GuiEvent.QUIT);
            }


            @Override
            public void windowClosing(WindowEvent event) {
                removeWindowListener(this);
                guiContext.sendEvent(GuiEvent.QUIT);
            }
        });
    }


    private void setApplicationIcon() {
        Icon icon = applicationData.getIcon();
        if (icon != null) {
            UIManager.put("icon", icon);
            setIconImage(((ImageIcon)icon).getImage());
        }
    }


    private void configureLookAndFeel() {
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        Plastic3DLookAndFeel.setCurrentTheme(new ExperienceBlue());
        try {
            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        }
        catch (UnsupportedLookAndFeelException e) {
            LOGGER.warn("Erreur lors de l'initialisation de JGoodies.", e);
        }

        // Nécessaire lorsque un Editor Combo se trouve sur une table, cf :
        //   net.codjo.gabi.gui.referential.GroupDetailGui
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.focusCellBackground", new JTable().getSelectionBackground());
        UIManager.put("Table.focusCellForeground", Color.WHITE);
    }


    public void initI18nComponents(GuiContext context) {
        translationManager = InternationalizationUtil.retrieveTranslationManager(context);
        notifier = InternationalizationUtil.retrieveTranslationNotifier(context);
        notifier.addInternationalizable(this);

        updateStatusBar();
    }


    public void updateTranslation(Language language, TranslationManager translator) {
        updateStatusBar();
    }
}

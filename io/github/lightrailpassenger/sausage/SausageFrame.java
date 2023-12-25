package io.github.lightrailpassenger.sausage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.github.lightrailpassenger.sausage.constants.SettingKeys;
import io.github.lightrailpassenger.sausage.utils.ReadWriteUtils;

import static io.github.lightrailpassenger.sausage.constants.SausageConstants.*;

class SausageFrame extends JFrame implements ChangeListener {
    private final Map<JComponent, File> tabToFileMap = new HashMap<>();
    private final JMenuBar menuBar = new JMenuBar();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Settings settings;

    SausageFrame(Settings settings) {
        super(SAUSAGE_FRAME_TITLE);

        this.settings = settings;
        this.setLayout(new BorderLayout());
        this.setJMenuBar(this.menuBar);
        this.constructMenu();
        this.add(tabbedPane, BorderLayout.CENTER);
        this.tabbedPane.addChangeListener(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        settings.addListener(new SettingChangeListener() {
            @Override
            public void settingChanged(SettingChangeEvent ev) {
                Font font = new Font(
                    settings.getProperty(SettingKeys.FONT_NAME),
                    Font.PLAIN,
                    settings.getInt(SettingKeys.FONT_SIZE, 13)
                );

                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    JScrollPane pane = (JScrollPane)(tabbedPane.getComponentAt(i));
                    JTextArea textArea = (JTextArea)(pane.getViewport().getView());

                    textArea.setFont(font);
                }
            }
        });
    }

    @Override
    public void stateChanged(ChangeEvent ev) {
        if (ev.getSource() instanceof JTabbedPane) {
            File file = tabToFileMap.get(tabbedPane.getSelectedComponent());

            this.setTitle(SAUSAGE_FRAME_TITLE + (file == null ? "" : " - " + file.getPath()));
        }
    }

    private static JMenuItem constructMenuItemWithAction(String name, Runnable action) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                action.run();
            }
        });

        return item;
    }

    void constructAndAddTab(File file, String content) {
        String title = file == null ? "Untitled - " + PreferenceStore.getInstance().getAndIncrementUntitledCounter() : file.getName();

        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font(
            settings.getProperty(SettingKeys.FONT_NAME),
            Font.PLAIN,
            settings.getInt(SettingKeys.FONT_SIZE, 13)
        ));

        JComponent newTab = new JScrollPane(textArea);
        this.tabbedPane.addTab(title, newTab);
        this.tabToFileMap.put(newTab, file);
        this.tabbedPane.setSelectedComponent(newTab);

        int index = this.tabbedPane.indexOfComponent(newTab);
        JComponent tabComponent = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tabComponent.setOpaque(false);
        tabComponent.add(new JLabel(title));
        tabComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent ev) {
                SausageFrame.this.tabbedPane.setSelectedComponent(newTab);
            }
        });
        this.tabbedPane.setTabComponentAt(index, tabComponent);
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(constructMenuItemWithAction("Close", new Runnable() {
            @Override
            public void run() {
                SausageFrame.this.tabToFileMap.remove(newTab);
                SausageFrame.this.tabbedPane.remove(newTab);
            }
        }));
        tabComponent.setComponentPopupMenu(popupMenu);
    }

    void constructAndAddUntitledTab() {
        this.constructAndAddTab(null, "");
    }

    void constructMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(constructMenuItemWithAction("New", new Runnable() {
            @Override
            public void run() {
                SausageFrame.this.constructAndAddUntitledTab();
            }
        }));
        fileMenu.add(constructMenuItemWithAction("Open", new Runnable() {
            @Override
            public void run() {
                File file = ReadWriteUtils.getFileFromUI(SausageFrame.this);
                try {
                    if (file != null) {
                        String content = ReadWriteUtils.readFile(file, Charset.forName("UTF-8"));

                        SausageFrame.this.constructAndAddTab(file, content);
                    }
                } catch (IOException ex) {
                    // TODO
                }
            }
        }));
        fileMenu.add(constructMenuItemWithAction("Duplicate", new Runnable() {
            @Override
            public void run() {
                JScrollPane selectedTab = (JScrollPane)(tabbedPane.getSelectedComponent());
                File file = tabToFileMap.get(selectedTab);
                String content = ((JTextArea)(selectedTab.getViewport().getView())).getText();

                SausageFrame.this.constructAndAddTab(file, content);
            }
        }));
        fileMenu.add(constructMenuItemWithAction("Save as", new Runnable() {
            @Override
            public void run() {
                File file = ReadWriteUtils.getFileFromUI(SausageFrame.this);
                try {
                    if (file != null && !file.exists()) {
                        JScrollPane selectedTab = (JScrollPane)(tabbedPane.getSelectedComponent());
                        String content = ((JTextArea)(selectedTab.getViewport().getView())).getText();

                        ReadWriteUtils.writeFile(file, Charset.forName("UTF-8"), content);
                    }
                } catch (IOException ex) {
                    // pass
                }
            }
        }));
        fileMenu.addSeparator();
        fileMenu.add(constructMenuItemWithAction("Settings", new Runnable() {
            @Override
            public void run() {
                SettingsDialog dialog = new SettingsDialog(SausageFrame.this, SausageFrame.this.settings);
                dialog.setVisible(true);
            }
        }));

        this.menuBar.add(fileMenu);
    }
}

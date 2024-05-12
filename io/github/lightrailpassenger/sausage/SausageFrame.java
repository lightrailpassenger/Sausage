package io.github.lightrailpassenger.sausage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.OverlayLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import io.github.lightrailpassenger.sausage.constants.SettingKeys;
import io.github.lightrailpassenger.sausage.history.HistoryIntervalRecorder;
import io.github.lightrailpassenger.sausage.indent.AutoIndentDocumentFilter;
import io.github.lightrailpassenger.sausage.utils.ReadWriteUtils;
import io.github.lightrailpassenger.sausage.utils.StringUtil;

import static io.github.lightrailpassenger.sausage.constants.SausageConstants.*;

class SausageFrame extends JFrame implements ChangeListener {
    private static final Map<String, String> defaultMapCoercerCache = new HashMap<>();
    static {
        defaultMapCoercerCache.put("TYPE_TO_EXTENSION", "java:java;javascript:js,ts,jsx,tsx,cjs,mjs,cts,mts");
        defaultMapCoercerCache.put("TYPE_TO_INDENT_START", "java:{;javascript:{");
        defaultMapCoercerCache.put("TYPE_TO_INDENT_END", "java:};javascript:}");
    }

    private final Map<JComponent, File> tabToFileMap = new HashMap<>();
    private final Map<JComponent, HistoryIntervalRecorder<String>> tabToHistoryIntervalRecorderMap = new HashMap<>();
    private final JMenuBar menuBar = new JMenuBar();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final Settings settings;
    private final MapCoercer mapCoercer = new MapCoercer(defaultMapCoercerCache);

    SausageFrame(Settings settings) {
        super(SAUSAGE_FRAME_TITLE);

        this.settings = settings;
        this.setLayout(new BorderLayout());
        this.setJMenuBar(this.menuBar);
        this.constructMenu();
        this.add(tabbedPane, BorderLayout.CENTER);
        this.tabbedPane.addChangeListener(this);

        settings.addListener(new SettingChangeListener() {
            @Override
            public void settingChanged(SettingChangeEvent ev) {
                // TODO: Refactor this to remove duplicate.
                Font font = new Font(
                    settings.getProperty(SettingKeys.FONT_NAME),
                    Font.PLAIN,
                    settings.getInt(SettingKeys.FONT_SIZE, DEFAULT_FONT_SIZE)
                );
                boolean shouldEnableVerticalLine = "true".equals(settings.getProperty(SettingKeys.SHOULD_ENABLE_VERTICAL_LINE));
                int verticalLineWidth = settings.get(SettingKeys.VERTICAL_LINE_WIDTH, new NumericRangeCoercer(20, 2000, 80));

                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    Component component = tabbedPane.getComponentAt(i);
                    JTextArea textArea = SausageFrame.getTextAreaByTabbedPaneComponent(component);
                    VerticalLineLayer verticalLineLayer = (VerticalLineLayer)(SausageFrame.getLayerByTabbedPaneComponent(component, 2));

                    textArea.setFont(font);
                    verticalLineLayer.setFont(font);
                    verticalLineLayer.setWidth(shouldEnableVerticalLine ? verticalLineWidth : -1);
                    verticalLineLayer.repaint();
                }
            }
        });
    }

    private static Component getLayerByTabbedPaneComponent(Component c, int layer) {
        JScrollPane scrollPane = (JScrollPane)c;

        return ((JLayeredPane)(scrollPane.getViewport().getView())).getComponentsInLayer(layer)[0];
    }

    private static JTextArea getTextAreaByTabbedPaneComponent(Component c) {
        return (JTextArea)(SausageFrame.getLayerByTabbedPaneComponent(c, 1));
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

    private void configureFileTypeSpecificLogic(File file, JTextArea textArea) {
        if (file == null) {
            return;
        }

        Map<String, String[]> typeToExtensionMap = settings.get(SettingKeys.TYPE_TO_EXTENSION, mapCoercer);
        Map<String, String[]> typeToIndentStartMap = settings.get(SettingKeys.TYPE_TO_INDENT_START, mapCoercer);
        Map<String, String[]> typeToIndentEndMap = settings.get(SettingKeys.TYPE_TO_INDENT_END, mapCoercer);

        String fileName = file.getName();
        int dotIndex = fileName.indexOf('.');

        if (dotIndex <= 0) {
            // Include: no `.`, or `.` at the start e.g. `.DS_Store`
            return;
        }

        String extension = fileName.substring(dotIndex + 1).toLowerCase();
        String type = null;

        for (Map.Entry<String, String[]> typeToExtensionMapEntry: typeToExtensionMap.entrySet()) {
            if (Arrays.asList(typeToExtensionMapEntry.getValue()).contains(extension)) {
                type = typeToExtensionMapEntry.getKey();
                break;
            }
        }

        if (type == null) {
            return;
        }

        String[] indentStartChars = typeToIndentStartMap.get(type);
        String[] indentEndChars = typeToIndentEndMap.get(type);

        List<Character> indentStartCharList = new ArrayList<>();
        List<Character> indentEndCharList = new ArrayList<>();

        if (indentStartChars != null) {
            for (char indentStartChar: indentStartChars[0].toCharArray()) {
                indentStartCharList.add((Character)indentStartChar);
            }
        }

        if (indentEndChars != null) {
            for (char indentEndChar: indentEndChars[0].toCharArray()) {
                indentEndCharList.add((Character)indentEndChar);
            }
        }

        int indentation = StringUtil.deriveIndentation(textArea.getText());

        AbstractDocument document = (AbstractDocument)(textArea.getDocument());
        document.setDocumentFilter(new AutoIndentDocumentFilter(
            textArea,
            indentStartCharList,
            indentEndCharList,
            indentation == 0 ? 2 : indentation, // TODO: Change default by reading properties
            ' '
        ));
    }

    void constructAndAddTab(File file, String content) {
        String title = file == null ? "Untitled - " + PreferenceStore.getInstance().getAndIncrementUntitledCounter() : file.getName();

        JTextArea textArea = new JTextArea(content);
        Font font = new Font(
            settings.getProperty(SettingKeys.FONT_NAME),
            Font.PLAIN,
            settings.getInt(SettingKeys.FONT_SIZE, DEFAULT_FONT_SIZE)
        );
        boolean shouldEnableVerticalLine = "true".equals(settings.getProperty(SettingKeys.SHOULD_ENABLE_VERTICAL_LINE));
        int verticalLineWidth = settings.get(SettingKeys.VERTICAL_LINE_WIDTH, new NumericRangeCoercer(20, 2000, 80));

        textArea.setFont(font);

        JLayeredPane layeredPane = new LayeredScrollablePane(textArea);
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        JScrollPane newTab = new JScrollPane(layeredPane);
        VerticalLineLayer verticalLineLayer = new VerticalLineLayer(font, shouldEnableVerticalLine ? verticalLineWidth : -1);

        layeredPane.add(textArea, Integer.valueOf(1));
        layeredPane.add(verticalLineLayer, Integer.valueOf(2));

        this.configureFileTypeSpecificLogic(file, textArea);
        this.tabbedPane.addTab(title, newTab);
        this.tabToFileMap.put(newTab, file);
        this.tabToHistoryIntervalRecorderMap.put(newTab, new HistoryIntervalRecorder<String>(new HistoryIntervalRecorder.State<String>() {
            @Override
            public String get() {
                return textArea.getText();
            }

            @Override
            public void set(String text) {
                textArea.setText(text);
            }

            @Override
            public String getInitialState() {
                return "";
            }
        }));
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
                SausageFrame.this.tabToHistoryIntervalRecorderMap.remove(newTab);
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
                    JOptionPane.showMessageDialog(SausageFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }));
        fileMenu.add(constructMenuItemWithAction("Duplicate", new Runnable() {
            @Override
            public void run() {
                JScrollPane selectedTab = (JScrollPane)(tabbedPane.getSelectedComponent());
                File file = tabToFileMap.get(selectedTab);
                String content = SausageFrame.getTextAreaByTabbedPaneComponent(selectedTab).getText();

                SausageFrame.this.constructAndAddTab(file, content);
            }
        }));
        fileMenu.add(constructMenuItemWithAction("Save as", new Runnable() {
            @Override
            public void run() {
                File file = ReadWriteUtils.getFileFromUI(SausageFrame.this);
                try {
                    if (file != null && !file.exists()) {
                        JTextArea textArea = SausageFrame.getTextAreaByTabbedPaneComponent(tabbedPane.getSelectedComponent());
                        String content = textArea.getText();

                        ReadWriteUtils.writeFile(file, Charset.forName("UTF-8"), content);
                        SausageFrame.this.configureFileTypeSpecificLogic(file, textArea);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(SausageFrame.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

        JMenu editMenu = new JMenu("Edit");

        editMenu.add(constructMenuItemWithAction("Undo", new Runnable() {
            @Override
            public void run() {
                JComponent tab = (JComponent)(tabbedPane.getSelectedComponent());
                HistoryIntervalRecorder<String> history = tabToHistoryIntervalRecorderMap.get(tab);

                if (history.canUndo()) {
                    history.undo();
                }
            }
        }));

        editMenu.add(constructMenuItemWithAction("Redo", new Runnable() {
            @Override
            public void run() {
                JComponent tab = (JComponent)(tabbedPane.getSelectedComponent());
                HistoryIntervalRecorder<String> history = tabToHistoryIntervalRecorderMap.get(tab);

                if (history.canRedo()) {
                    history.redo();
                }
            }
        }));

        this.menuBar.add(fileMenu);
        this.menuBar.add(editMenu);
    }
}

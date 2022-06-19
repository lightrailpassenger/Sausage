package io.github.lightrailpassenger.sausage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

import io.github.lightrailpassenger.sausage.utils.ReadWriteUtils;

import static io.github.lightrailpassenger.sausage.constants.SausageConstants.*;

class SausageFrame extends JFrame {
    private final JMenuBar menuBar = new JMenuBar();
    private final JTabbedPane tabbedPane = new JTabbedPane();

    SausageFrame() {
        super(SAUSAGE_FRAME_TITLE);

        this.setLayout(new BorderLayout());
        this.setJMenuBar(this.menuBar);
        this.constructMenu();
        this.add(tabbedPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    void constructAndAddTab(String title, String content) {
        JComponent newTab = new JScrollPane(new JTextArea(content));
        this.tabbedPane.addTab(title, newTab);
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
                SausageFrame.this.tabbedPane.remove(newTab);
            }
        }));
        tabComponent.setComponentPopupMenu(popupMenu);
    }

    void constructAndAddUntitledTab() {
        String title = "Untitled - " + PreferenceStore.getInstance().getAndIncrementUntitledCounter();

        this.constructAndAddTab(title, "");
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
                    String content = ReadWriteUtils.readFile(file, Charset.forName("US-ASCII"));

                    SausageFrame.this.constructAndAddTab(file.getName(), content);
                } catch (IOException ex) {
                    // TODO
                }
            }
        }));
        fileMenu.add(new JMenuItem("Duplicate"));

        this.menuBar.add(fileMenu);
    }
}

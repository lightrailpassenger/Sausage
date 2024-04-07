package io.github.lightrailpassenger.sausage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.util.Arrays;
import java.util.Comparator;

import static io.github.lightrailpassenger.sausage.constants.SausageConstants.*;
import io.github.lightrailpassenger.sausage.constants.SettingKeys;

public class SettingsDialog extends JDialog {
    private final Settings settings;

    public SettingsDialog(Frame owner, Settings settings) {
        super(owner, true);
        this.settings = settings;
        this.setLayout(new BorderLayout());
        this.setTitle("Settings");
        this.initialize();
        this.setLocationRelativeTo(owner);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                try {
                    settings.save();
                } catch (IOException ex) {
                    // pass
                }
            }}
        );
    }

    private JPanel createFontSection() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Font"));

        Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        Arrays.sort(
            allFonts,
            new Comparator<Font>() {
                @Override
                public int compare(Font f1, Font f2) {
                    return f1.getFontName().compareTo(f2.getFontName());
                }
            }
        );
        JComboBox<Font> comboBox = new JComboBox<>(allFonts);
        comboBox.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean isFocused
            ) {
                JLabel comp = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, isFocused);
                Font font = (Font)value;
                comp.setFont(font.deriveFont(16.5f));
                comp.setText(font.getFontName());

                return comp;
            }
        });

        int selectedIndex = 0;
        String currentFontName = settings.getProperty(SettingKeys.FONT_NAME, null);

        if (currentFontName != null) {
            for (int i = 0; i < allFonts.length; i++) {
                if (currentFontName.equals(allFonts[i].getFontName())) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        comboBox.setSelectedIndex(selectedIndex);
        comboBox.setFont(allFonts[selectedIndex].deriveFont(16.5f));
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                Font font = (Font)comboBox.getSelectedItem();
                settings.setProperty(SettingKeys.FONT_NAME, font.getFontName());
                comboBox.setFont(font.deriveFont(16.5f));
            }
        });

        JSpinner spinner = new JSpinner(
            new SpinnerNumberModel(settings.getInt(SettingKeys.FONT_SIZE, DEFAULT_FONT_SIZE), 8, 50, 1)
        );

        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ev) {
                settings.setProperty(SettingKeys.FONT_SIZE, (int)spinner.getValue());
            }
        });

        panel.add(comboBox);
        panel.add(spinner);

        return panel;
    }

    private JPanel createVerticalLineSection() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Vertical line"));

        boolean shouldEnableVerticalLine = "true".equals(settings.getProperty(SettingKeys.SHOULD_ENABLE_VERTICAL_LINE));
        int verticalLineWidth = settings.get(SettingKeys.VERTICAL_LINE_WIDTH, new NumericRangeCoercer(20, 2000, 80));

        final JCheckBox checkBox = new JCheckBox("Show", shouldEnableVerticalLine);
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(verticalLineWidth, 20, 2000, 1));

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                boolean isSelected = checkBox.isSelected();

                settings.setProperty(SettingKeys.SHOULD_ENABLE_VERTICAL_LINE, isSelected);
                spinner.setEnabled(isSelected);
            }
        });

        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ev) {
                settings.setProperty(SettingKeys.VERTICAL_LINE_WIDTH, (int)spinner.getValue());
            }
        });

        panel.add(checkBox);
        panel.add(spinner);

        return panel;
    }

    private void initialize() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(createFontSection());
        panel.add(createVerticalLineSection());

        this.add(panel, BorderLayout.CENTER);
        this.pack();
    }
}

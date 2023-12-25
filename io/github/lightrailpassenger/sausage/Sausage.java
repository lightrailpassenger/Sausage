package io.github.lightrailpassenger.sausage;

import java.awt.Font;
import java.io.File;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import static io.github.lightrailpassenger.sausage.constants.SausageConstants.*;

public class Sausage {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();

                    UIManager.setLookAndFeel(lookAndFeel);

                    Map<Object, Object> lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();

                    for (Map.Entry<Object, Object> entry: lookAndFeelDefaults.entrySet()) {
                        String key = entry.getKey().toString();

                        if (key.endsWith(".font")) {
                            Object value = lookAndFeelDefaults.get(key);

                            if (value instanceof Font) {
                                lookAndFeelDefaults.put(key, ((Font)value).deriveFont(Font.PLAIN, DEFAULT_FONT_SIZE));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // pass
                }

                Settings settings = new Settings(new File(System.getProperty("user.home"), ".sausage.pref"));
                SausageFrame frame = new SausageFrame(settings);
                PreferenceStore.getInstance().addFrame(frame);

                frame.constructAndAddUntitledTab();
                frame.setSize(SAUSAGE_FRAME_DEFAULT_DIMENSION);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}

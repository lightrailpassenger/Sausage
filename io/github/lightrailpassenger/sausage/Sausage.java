package io.github.lightrailpassenger.sausage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import static io.github.lightrailpassenger.sausage.constants.SausageConstants.*;
import io.github.lightrailpassenger.sausage.constants.SettingKeys;

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
                final SausageFrame frame = new SausageFrame(settings);
                PreferenceStore.getInstance().addFrame(frame);

                frame.constructAndAddUntitledTab();

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Coercer<Integer> locationXCoercer = new NumericRangeCoercer(0, screenSize.width, 0);
                Coercer<Integer> locationYCoercer = new NumericRangeCoercer(0, screenSize.height, 0);

                int locationX = settings.get(SettingKeys.LOCATION_X, locationXCoercer);
                int locationY = settings.get(SettingKeys.LOCATION_Y, locationYCoercer);

                Coercer<Integer> widthCoercer = new NumericRangeCoercer(
                    Math.min(SAUSAGE_FRAME_DEFAULT_DIMENSION.width, screenSize.width - locationX),
                    Math.max(SAUSAGE_FRAME_DEFAULT_DIMENSION.width, screenSize.width - locationX),
                    SAUSAGE_FRAME_DEFAULT_DIMENSION.width
                );
                Coercer<Integer> heightCoercer = new NumericRangeCoercer(
                    Math.min(SAUSAGE_FRAME_DEFAULT_DIMENSION.height, screenSize.height - locationY),
                    Math.max(SAUSAGE_FRAME_DEFAULT_DIMENSION.height, screenSize.height - locationY),
                    SAUSAGE_FRAME_DEFAULT_DIMENSION.height
                );

                int width = settings.get(SettingKeys.DIMENSION_WIDTH, widthCoercer);
                int height = settings.get(SettingKeys.DIMENSION_HEIGHT, heightCoercer);

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent ev) {
                        Dimension size = frame.getSize();
                        Point location = frame.getLocationOnScreen();

                        settings.setProperty(SettingKeys.LOCATION_X, location.x + "");
                        settings.setProperty(SettingKeys.LOCATION_Y, location.y + "");
                        settings.setProperty(SettingKeys.DIMENSION_WIDTH, size.width + "");
                        settings.setProperty(SettingKeys.DIMENSION_HEIGHT, size.height + "");

                        try {
                            settings.save();
                        } catch (IOException ex) {
                            // pass
                        }
                        System.exit(0);
                    }
                });

                frame.setSize(width, height);
                frame.setLocation(new Point(locationX, locationY));
                frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}

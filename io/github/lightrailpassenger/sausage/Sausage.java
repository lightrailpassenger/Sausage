package io.github.lightrailpassenger.sausage;

import javax.swing.SwingUtilities;

import static io.github.lightrailpassenger.sausage.constants.SausageConstants.*;

public class Sausage {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SausageFrame frame = new SausageFrame();
                PreferenceStore.getInstance().addFrame(frame);

                frame.constructAndAddUntitledTab();
                frame.setSize(SAUSAGE_FRAME_DEFAULT_DIMENSION);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}

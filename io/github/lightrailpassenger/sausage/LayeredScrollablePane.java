package io.github.lightrailpassenger.sausage;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JLayeredPane;
import javax.swing.Scrollable;

public class LayeredScrollablePane extends JLayeredPane implements Scrollable {
    private Scrollable sc;

    public LayeredScrollablePane(Scrollable sc) {
        super();
        this.sc = sc;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.sc.getPreferredScrollableViewportSize();
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle rect, int orientation, int direction) {
        return this.sc.getScrollableBlockIncrement(rect, orientation, direction);
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return this.sc.getScrollableTracksViewportHeight();
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return this.sc.getScrollableTracksViewportWidth();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle rect, int orientation, int direction) {
        return this.sc.getScrollableUnitIncrement(rect, orientation, direction);
    }
}

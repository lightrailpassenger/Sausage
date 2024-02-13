package io.github.lightrailpassenger.sausage;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import javax.swing.JComponent;

public class VerticalLineLayer extends JComponent {
    private Font font;
    private int width;

    public VerticalLineLayer(Font font, int width) {
        this.font = font;
        this.width = width;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        if (this.width < 0) {
            return;
        }

        g2d.setFont(this.font);

        FontMetrics fm = g2d.getFontMetrics(this.font);
        int charWidth = fm.charWidth('_');
        int fullWidth = charWidth * this.width;
        Shape line = new Line2D.Float(fullWidth, 0, fullWidth, this.getHeight());

        g2d.setColor(Color.BLACK);
        g2d.draw(line);
    }
}

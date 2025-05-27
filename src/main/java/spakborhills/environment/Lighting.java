package spakborhills.environment;

import spakborhills.GamePanel;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Lighting {
    GamePanel gp;
    BufferedImage darkFilter;
    int effectRadius;
    public Lighting(GamePanel gp, int radius) {
        this.gp = gp;
        this.effectRadius = radius;
    }

    public void update(float baseAlpha) {
        if (gp == null || gp.player == null) {
            return;
        }

        darkFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) darkFilter.getGraphics();

        Color outerColor = new Color(0, 0, 0, Math.min(1.0f, Math.max(0f, baseAlpha)));

        float centerAlphaFactor = 0.3f;
        float centerAlpha = Math.max(0f, baseAlpha * (1.0f - centerAlphaFactor));
        Color centerColor = new Color(0, 0, 0, Math.min(1.0f, Math.max(0f, centerAlpha)));

        // 3. Buat RadialGradientPaint
        Point2D center = new Point2D.Float(gp.player.screenX + (gp.tileSize / 2.0f),
                gp.player.screenY + (gp.tileSize / 2.0f));
        float radius = this.effectRadius;
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {centerColor, outerColor};

        if (baseAlpha < 0.05f) {
            g2.setColor(new Color(0,0,0,0));
        } else {
            RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
            g2.setPaint(rgp);
        }

        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.dispose();
    }

    public void draw(Graphics2D g2) {
        if (darkFilter != null) {
            g2.drawImage(darkFilter, 0, 0, null);
        }
    }
}

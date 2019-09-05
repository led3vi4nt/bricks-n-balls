import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Ray {

    public static void cast(Graphics2D g, Point2D.Double start, Vector<Double> normal) {

        float[] dash1 = {2f, 0f, 2f};
        BasicStroke bs1 = new BasicStroke(1,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                1.0f,
                dash1,
                2f);
        g.setStroke(bs1);
        double i = 0.0;
        if (normal.get(0) > 0)
            i = (Game.frameWidth - start.getX()) / normal.get(0);
        if (normal.get(0) < 0)
            i = start.getX() / Math.abs(normal.get(0));

        double x = start.getX();
        double y = start.getY();
        if (i != 0.0) {
            int endX = (int) (x + i * normal.get(0));
            int endY = (int) (y + i * normal.get(1));

            g.drawLine((int) x, (int) y, endX, endY);

            if (endY > 0) {
                normal.set(0, -normal.get(0));
                cast(g, new Point2D.Double(endX, endY), normal);
            }
        }
    }
}

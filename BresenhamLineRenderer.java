package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;

public class BresenhamLineRenderer implements LineRenderer {
    private BresenhamLineRenderer() {
    }

    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable panel) {
        int p1x = p1.getIntX();
        int p1y = p1.getIntY();

        int p2x = p2.getIntX();
        int p2y = p2.getIntY();

        int m_num = 2 * (p2y - p1y);
        int dx = p2x - p1x;

        int x = p1x;
        int y = p1y;

        double err = m_num - dx;
        int k = m_num - (2 * dx);

        int argbColor = p1.getColor().asARGB();

        while (x <= p2x)
        {
            panel.setPixel(x, y, 0.0, argbColor);
            x++;

            if(err>=0)
            {
                err += k;
                y++;
            }
            else
            {
                err += m_num;
            }
        }

    }

    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new BresenhamLineRenderer());
    }
}
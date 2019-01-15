package line;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;


public class DDALineRenderer implements LineRenderer {
    private DDALineRenderer(){}
    Color color;

    @Override
    public void drawLine(Vertex3D p1, Vertex3D p2, Drawable panel)
    {
        int p1x = p1.getIntX();
        int p1y = p1.getIntY();
        int p1z = p1.getIntZ();

        int p2x = p2.getIntX();
        int p2y = p2.getIntY();

        double r1 = p1.getColor().getR();
        double g1 = p1.getColor().getG();
        double b1 = p1.getColor().getB();

        double r2 = p2.getColor().getR();
        double g2 = p2.getColor().getG();
        double b2 = p2.getColor().getB();


        double deltaX = p2x - p1x;
        double deltaY = p2y - p1y;

        double deltaR = r2-r1;
        double deltaG = g2-g1;
        double deltaB = b2-b1;

        //System.out.println(deltaG);
        //System.out.println(deltaB);

        double mr = deltaR/deltaX;
        double mg = deltaG/deltaX;
        double mb = deltaB/deltaX;

        //System.out.println(mg);
        //System.out.println(mb);

        double slope = deltaY/deltaX;

        double y = p1y;
        double r = r1;


        for (int x = p1x; x <= p2x ; x++)
        {
            int y_int = (int) Math.round(y);
            Color c = new Color(r,g1,b1);
            int r_int = c.getIntR();
            int g_int = c.getIntG();
            int b_int = c.getIntB();

            panel.setPixel(x, y_int,p1z, Color.makeARGB(r_int,g_int,b_int));
            y = y + slope;
            r = r + mr;
            g1 = g1 + mg;
            b1 = b1 + mb;

            //System.out.println(g1);
            //System.out.println(b1);
        }
    }
    public static LineRenderer make() {
        return new AnyOctantLineRenderer(new DDALineRenderer());
    }
}

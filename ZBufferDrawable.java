package windowing.drawable;
import client.RendererTrio;
import client.interpreter.SimpInterpreter;
import geometry.Vertex3D;
import polygon.Polygon;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class ZBufferDrawable extends DrawableDecorator {
    double[][] zbuffer;

    public ZBufferDrawable(Drawable delegate) {
        super(delegate);

        zbuffer = new double[getWidth()][getHeight()];
        initiZ(zbuffer);

    }

    @Override
    public void setPixel(int x, int y, double z, int argbColor) {
        //System.out.println("true");
        delegate.setPixel(x,  y,  z, argbColor);

        /*

        if(z > zbuffer[x][y])
        {
            zbuffer[x][y] = z;
           // System.out.println(z);
            delegate.setPixel(x,  y,  z, argbColor);
        }
*/

    }

    private void initiZ(double zb[][])
    {
        double min = -200;

        for (int i = 0; i < zbuffer.length; i++) {

            for (int j = 0; j < zbuffer[i].length; j++) {
                zbuffer[i][j] = min;
                //System.out.println(zbuffer[i][j]);
            }
        }

    }


}

package client;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.drawable.DrawableDecorator;
import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator {
    private Color color;


    DepthCueingDrawable(Drawable draw, int x, int y, Color color){
        super(draw);
        this.color = color;

    }

    private Vertex3D depthCueing(Vertex3D p1)
    {

        int y = delegate.getHeight();

        double z = 1-((double) p1.getIntZ())/y;

        Color color = new Color(z,z,z);


        p1 = new Vertex3D(p1.getX(),p1.getY(),p1.getZ(),color);

        return p1;

    }

}

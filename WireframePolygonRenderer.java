package polygon;

import geometry.Vertex3D;
import line.BresenhamLineRenderer;
import line.DDALineRenderer;
import windowing.drawable.Drawable;
import line.LineRenderer;


public class WireframePolygonRenderer implements PolygonRenderer {


    @Override
    public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader)
    {
        Vertex3D[] V = new Vertex3D[polygon.numVertices];

        for(int i=0; i < polygon.numVertices; i++)
        {
            V[i] = polygon.vertices.get(i);

        }

        LineRenderer lineRender1 = DDALineRenderer.make();
        LineRenderer lineRender2 = DDALineRenderer.make();
        LineRenderer lineRender3 = DDALineRenderer.make();

        lineRender1.drawLine(V[0],V[1],drawable);
        lineRender2.drawLine(V[1],V[2],drawable);
        lineRender3.drawLine(V[0],V[2],drawable);



    }

    public static PolygonRenderer make() {
        return new WireframePolygonRenderer();
    }
}

package polygon;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;


public class FilledPolygonRenderer implements  PolygonRenderer {
    private FilledPolygonRenderer(){
    }
    Vertex3D[] vArr;


    @Override
    public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader)
    {

        Vertex3D v[];
        v = new Vertex3D[3];


        for(int i=0;i<=2;i++)
        {
            v[i] = polygon.vertices.get(i);
        }
/*
        System.out.print("\n");
        for (int i = 0; i < v.length; i++) {
            System.out.print(v[i]);
        }
*/

        //insert vertices into array

        sortByY(v);

        Vertex3D p1 = v[0];
        Vertex3D p2 = v[1];
        Vertex3D p3 = v[2];



        if(invertedTriangle(v))
        {
            drawInvertedTriangle(polygon,drawable,vertexShader,v);
        }
        else if (flatBottomTriangle(v))

        {
            //System.out.println("True\n");
            drawFlatBottomTriangle(polygon,drawable,vertexShader,v);


        }

        else
        {
            //System.out.print("True");


            double ratio = ((double)(p2.getIntY()- p1.getIntY()))/(p3.getIntY()-p1.getIntY());
            double px = (p1.getIntX()+( (p3.getIntX()-p1.getIntX())*ratio) );

            double py = (p1.getIntY()+( (p3.getIntY()-p1.getIntY())*ratio ));


            Vertex3D extra_pnt = new Vertex3D(px,py,0,p1.getColor());

            Vertex3D[] vInv;
            vInv = new Vertex3D[3];

            vInv[0] = p1;
            vInv[1] = p2;
            vInv[2] = extra_pnt;
            sortByY(vInv);

            drawFlatBottomTriangle(polygon,drawable,vertexShader,vInv);

            vInv[0] = extra_pnt;
            vInv[1] = p2;
            vInv[2] = p3;
            sortByY(vInv);
            drawInvertedTriangle(polygon,drawable,vertexShader,vInv);

            /*
            for(int i=0;i<vInv.length;i++)
            {
                System.out.print(vInv[i]);
            }
            */

        }


    }

    private void sortByY(Vertex3D[] vArr)
    {

        for(int i=0; i < vArr.length-1;i++) {
            for(int j=0; j< vArr.length-1-i ; j++)
            {
                if (vArr[j].getIntY() <= vArr[j+1].getIntY())
                {
                    Vertex3D cur = vArr[j];
                    vArr[j] = vArr[j+1];
                    vArr[j+1] = cur;
                }
            }


        }

    }


    private boolean invertedTriangle(Vertex3D[] vArr)
    {
        if(vArr[0].getIntY() == vArr[1].getIntY())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean flatBottomTriangle(Vertex3D[] vArr)
    {
        if(vArr[1].getIntY() == vArr[2].getIntY())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void drawFlatBottomTriangle(Polygon polygon, Drawable drawable, Shader vertexShader, Vertex3D[] V)
    {
        LineRenderer renderer1 = DDALineRenderer.make();
        LineRenderer renderer2 = DDALineRenderer.make();
/*
        for (int i = 0; i < V.length; i++) {
            System.out.print(V[i]);
        }
*/

        if(V[1].getIntX()>V[2].getIntX())
        {
            Vertex3D temp = V[1];
            V[1] = V[2];
            V[2] = temp;
        }

        Vertex3D p1 = V[0];
        Vertex3D p2 = V[1];
        Vertex3D p3 = V[2];


        int p1_x = p1.getIntX();
        int p1_y = p1.getIntY();
        double z = p1.getZ();


        int p2_x = p2.getIntX();
        int p2_y = p2.getIntY();

        int p3_x = p3.getIntX();
        int p3_y = p3.getIntY();

        double r1L = p1.getColor().getR();
        //System.out.println(r1L);
        double g1L = p1.getColor().getG();
        double b1L = p1.getColor().getB();

        double r2L = p2.getColor().getR();
        //System.out.println(r2L);
        double g2L = p2.getColor().getG();
        double b2L = p2.getColor().getB();


        //renderer1.drawLine(p1,p2,drawable);
        //renderer2.drawLine(p1,p3,drawable);


        int dxL = p1_x - p2_x;
        int dxR = p1_x - p3_x;


        int dyL = p1_y - p2_y;
        int dyR = p1_y - p3_y;


        double mR = ((double) dyR/dxR);
        double mL = ((double)dyL/dxL);

        double dRedLeft = r1L - r2L;
        double dGREEN = g1L - g2L;
        double dBLUE = b1L - b2L;

        double mRedLeft =  dRedLeft/(double)dxL;

        double mGREENLeft = dGREEN/(double)dxL;
        //System.out.println(mGREENLeft);

        double mBLUELeft = dBLUE/(double)dxL;
        //System.out.println("blue slope: ");
        //System.out.println(mBLUELeft);


        double xL = p1_x;
        double xR = p1_x;


        double rR = r1L;
        double gR = g1L;
        double bR = b1L;

        for(int y=p1_y; y>=p2_y;y--)
        {

            int xL_int = (int) Math.round(xL);
            int xR_int = (int)Math.round(xR);


            Color c = new Color(rR,gR,bR);
            int rL_int = c.getIntR();
            int gR_int = c.getIntG();
            int bL_int = c.getIntB();

            for(int x=xL_int; x<= xR_int-1; x++)
            {

                drawable.setPixel(x,y,z,Color.makeARGB(rL_int,gR_int,bL_int));


            }



            xL = xL - (1/mL);
            xR = xR - (1/mR);
            rR = rR - (mRedLeft);
            gR = gR - (mGREENLeft);
            bR = bR - (mBLUELeft);


            //System.out.println(rR);
            //System.out.println(gR);
            //System.out.println(bR);


        }





}
    private void drawInvertedTriangle(Polygon polygon, Drawable drawable, Shader vertexShader, Vertex3D[] V)
    {
        LineRenderer renderer1 = DDALineRenderer.make();
        LineRenderer renderer2 = DDALineRenderer.make();



        Vertex3D p1 = V[0];
        Vertex3D p2 = V[1];
        Vertex3D p3 = V[2];

/*
        for (int i = 0; i < V.length; i++) {
            System.out.print(V[i]);
        }
*/

        int p1_x = p1.getIntX();
        int p1_y = p1.getIntY();

        int p2_x = p2.getIntX();
        int p2_y = p2.getIntY();

        int p3_x = p3.getIntX();
        int p3_y = p3.getIntY();

        double r1L = p1.getColor().getR();
        //System.out.println(r1L);
        double g1L = p1.getColor().getG();
        double b1L = p1.getColor().getB();

        double r2L = p2.getColor().getR();
        //System.out.println(r2L);
        double g2L = p2.getColor().getG();
        double b2L = p2.getColor().getB();

        double dRedLeft = r1L - r2L;
        double dGREEN = g1L - g2L;
        double dBLUE = b1L - b2L;


        //renderer1.drawLine(p1,p3,drawable);
        //renderer2.drawLine(p2,p3,drawable);

        int argbColor = p1.getColor().asARGB();

        int dxL = p1_x - p3_x;
        int dxR = p2_x - p3_x;

        int dyL = p1_y - p3_y;
        int dyR = p2_y - p3_y;


        double mR = ((double) dyR/dxR);
        double mL = ((double)dyL/dxL);
        //System.out.print(mL);

        double xL;
        double xR;
        double temp = mL;
        if(p1_x<p2_x)
        {
            xL = p1_x;
            xR = p2_x;
        }
        else
        {
            mL = mR;
            mR = temp;
            xL = p2_x;
            xR = p1_x;
        }

        double mRedLeft =  dRedLeft/(double)dxL;

        double mGREENLeft = dGREEN/(double)dxL;
        //System.out.println(mGREENLeft);

        double mBLUELeft = dBLUE/(double)dxL;

        double rR = r1L;
        double gR = g1L;
        double bR = b1L;

        for(int y=p1_y; y>=p3_y;y--)
        {

            int xL_int = (int) Math.round(xL);
            int xR_int = (int)Math.round(xR);

            Color c = new Color(rR,gR,bR);
            int rL_int = c.getIntR();
            int gR_int = c.getIntG();
            int bL_int = c.getIntB();


            for(int x=xL_int; x<= xR_int-1; x++)
            {
                drawable.setPixel(x,y,0,Color.makeARGB(rL_int,gR_int,bL_int));
            }

            xL = xL - (1/mL);
            xR = xR - (1/mR);
            rR = rR - (mRedLeft);
            gR = gR - (mGREENLeft);
            bR = bR - (mBLUELeft);

        }


    }



    public static PolygonRenderer make() {
        return new FilledPolygonRenderer();
    }
}

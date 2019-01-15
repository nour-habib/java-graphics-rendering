package geometry;
import java.lang.String;
import windowing.graphics.Dimensions;

public class Transformation {
    private static int m = 4;
    private static int n = 4;
    private static double [][] Matrix;

    public Transformation(){
        Matrix = new double[m][n];
    }


    public static Transformation identity()
    {
        Transformation transformation = new Transformation();

        double[][] Identity = new double[4][4];
        for(int i=0;i < m; i++)
        {
            for(int j=0; j<n;j++)
            {
                if (i==j)
                {
                    Identity[i][j] = 1;
                }
                else
                {
                    Identity[i][j] = 0;
                }
            }
        }

        transformation.Matrix = Identity;



        return transformation;
    }

    public Transformation scale(double sx, double sy, double sz)
    {

        Transformation transformation = new Transformation();
        double[][] S = zeroMatrix(m,n);

        S[0][0] = sx;
        S[1][1] = sy;
        S[2][2] = sz;
        S[3][3] = 1;


        transformation.Matrix = multiplyMatrices(Matrix,S,m,n);

        return transformation;

    }


    public static Transformation translate(double tx, double ty, double tz)
    {
        Transformation transformation = new Transformation();
        double[][] S = zeroMatrix(m,n);

        for(int i=0; i<m; i++)
        {
            for(int j=0; j<n;j++)
            {
                if(i==j)
                {
                    S[i][j] = 1;

                }
                else
                {
                    S[i][j] = 0;
                }
            }

        }


        S[0][n-1] = tx;
        S[1][n-1] = ty;
        S[2][n-1] = tz;

        transformation.Matrix = addMatrices(Matrix,S,m,n);

        return transformation;
    }

    public static Transformation rotation(double angle, String axis)
    {
        Transformation transformation = new Transformation();
        double[][] rotationMatrix = zeroMatrix(m,n);


        if(axis == "z")
        {
            rotationMatrix[0][0] = Math.cos(Math.toRadians(angle));
            rotationMatrix[1][1] = Math.cos(Math.toRadians(angle));

            rotationMatrix[0][1] = - (Math.sin(Math.toRadians(angle)));
            rotationMatrix[1][0] = Math.cos(Math.toRadians(angle));

            rotationMatrix[2][2] = 1;
            rotationMatrix[3][3] = 1;

            transformation.Matrix = multiplyMatrices(Matrix,rotationMatrix,m,n);

        }

        if(axis == "x")
        {
            rotationMatrix[0][0] = 1;
            rotationMatrix[1][1] = Math.cos(Math.toRadians(angle));
            rotationMatrix[2][2] = Math.cos(Math.toRadians(angle));;
            rotationMatrix[1][2] = - (Math.sin(Math.toRadians(angle)));

            rotationMatrix[2][1] = (Math.sin(Math.toRadians(angle)));
            rotationMatrix[3][3] = 1;

            transformation.Matrix = multiplyMatrices(Matrix,rotationMatrix,m,n);

        }

        if(axis == "y")
        {

        }

        return transformation;
    }

    private static double[][]  multiplyMatrices(double[][] A, double[][] B, int rows, int columns)
    {

        double[][] result = new double[rows][columns];

        for(int i=0; i <rows; i++)
        {
            for(int j=0; j<columns;j++)
            {
                double val = A[i][j] * B[i][j];
                result[i][j] = val;
            }
        }

        return result;

    }

    private static double[][] addMatrices(double[][] A, double[][] B, int m,int n)
    {
        double[][]result = new double[m][n];

        for(int i=0; i < m; i++)
        {
            for(int j=0; j<n; j++)
            {
               double val = A[i][j] + B[i][j];
               result[i][j] = val;
            }
        }

        return result;
    }


    private static double[][] zeroMatrix(int rows, int columns)
    {
        double[][] zeroMatrix = new double[rows][columns];

        for(int i=0;i<rows;i++)
        {
            for(int j=0; j< columns;j++)
            {
                zeroMatrix[i][j] = 0;
            }
        }


        return zeroMatrix;
    }

    private static Vertex3D crossProduct(Vertex3D p1, Vertex3D p2)
    {
        double detx = (p1.getY()*p2.getZ())-(p1.getZ()*p2.getY());
        double dety = -(p1.getX()*p2.getZ())-(p1.getZ()*p2.getX());
        double detz = (p1.getX()*p2.getY())- (p1.getY()*p2.getX());

        Vertex3D result = new Vertex3D(detx,dety,detz,p1.getColor());
        return result;
    }

    public void setMatrix()
    {

    }

    public static void printTrans()
    {
        for(int i=0; i<m; i++)
        {
            for(int j=0; j<n;j++)
            {

                   System.out.println(Matrix[i][j]);


            }

        }
    }

    public static Point3DH parallelProjZ(double x, double y, double z)
    {


        double proj[][] = zeroMatrix(m,n);
        proj[0][0] = 1;
        proj[1][1] = 1;
        proj[3][3] = 1;

        double input[][] = zeroMatrix(m,n);
        input[0][0] = 1;
        input[0][n-1] = x;
        input[1][1] = 1;
        input[1][n-1] = y;
        input[2][n-1] = z;
        input[2][2] = 1;
        input[n-1][n-1] = 1;


        double result[][] = multiplyMatrices(proj,input,m,n);

        Point3DH p = new Point3DH(result[0][n-1],result[1][n-1],result[2][n-1]);
        return p;
    }

}

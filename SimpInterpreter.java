package client.interpreter;

import java.util.Stack;

import client.interpreter.LineBasedReader;
import geometry.Point3DH;
import geometry.Point2D;
import geometry.Rectangle;
import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import client.Clipper;
import client.RendererTrio;
import geometry.Transformation;
import polygon.*;
import windowing.drawable.Drawable;
import windowing.drawable.TranslatingDrawable;
import windowing.drawable.ZBufferDrawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	private RenderStyle renderStyle;
	
	private Transformation CTM;
	private Transformation worldToScreen;
	
	private static int WORLD_LOW_X = -100;
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;
	
	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	private Stack<Transformation> transformationStack = new Stack<>();
	
	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;
	
	private Drawable drawable;
	private Drawable depthCueingDrawable;
	
	private LineRenderer lineRenderer;
	private PolygonRenderer filledRenderer;
	private PolygonRenderer wireframeRenderer;
	private Transformation cameraToScreen;
	private Clipper clipper;
	private Shader vertexShader;
	private boolean wireframeRenderBool = false;
	private boolean filledRenderBool = false;
	private ZBufferDrawable ZbufferDrawable;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	public SimpInterpreter(String filename, 
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.defaultColor = Color.WHITE;
		makeWorldToScreenTransform(drawable.getDimensions());
		
		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
		CTM = Transformation.identity();

	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		// TODO: fill this in

		//transform from -100,100 to 0,650

		double xmin = WORLD_LOW_X*6.5;
		double xmax = WORLD_HIGH_X*6.5;

		double ymin = WORLD_LOW_Y*6.5;
		double ymax = WORLD_HIGH_Y*6.5;

		worldToScreen = new Transformation();


        //CTM = worldToScreen.scale(3.25*x,3.25*y,0);
		//worldToScreen.printTrans();
		CTM = worldToScreen.translate(650,0,0);
		//worldToScreen.printTrans();

	}
	
	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;
		
		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line":		interpretLine(tokens);		break;
		case "polygon":	interpretPolygon(tokens);	break;
//		case "camera" :		interpretCamera(tokens);	break;
//		case "surface" :	interpretSurface(tokens);	break;
//		case "ambient" :	interpretAmbient(tokens);	break;
//		case "depth" :		interpretDepth(tokens);		break;
//		case "obj" :		interpretObj(tokens);		break;
		
		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void push() {
		// TODO: finish this method

		transformationStack.push(CTM);

	}
	private void pop() {
		// TODO: finish this method

		Transformation duplicate = transformationStack.peek();
		transformationStack.pop();
		CTM = duplicate;
	}
	private void wire() {
		// TODO: finish this method

		filledRenderBool = false;
		wireframeRenderBool = true;

	}
	private void filled() {
		// TODO: finish this method


		filledRenderBool = true;
		wireframeRenderBool = false;
	}
	
	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	private void file(String filename) {
		readerStack.push(reader);

		reader = new LineBasedReader(filename);
	}	

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		// TODO: finish this method

		CTM.scale(sx,sy,sz);
		transformationStack.push(CTM);
	}
	private void interpretTranslate(String[] tokens) {
//		interpretLine(tokens[0]);
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		// TODO: finish this method

		CTM.translate(tx,ty,tz);
		transformationStack.push(CTM);
	}
	private void interpretRotate(String[] tokens) {
		//interpretLine(tokens[0]);
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);

		// TODO: finish this method

		CTM.rotation(angleInDegrees,axisString);
		transformationStack.push(CTM);
	}
	private double cleanNumber(String string) {
		return Double.parseDouble(string);
	}
	
	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);
		
		private int numTokensPerVertex;
		
		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	private void interpretLine(String[] tokens) {
		interpretLine(tokens[0]);
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);

		// TODO: finish this method

		line(vertices[1],vertices[2]);
	}	
	private void interpretPolygon(String[] tokens) {

		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);

		// TODO: finish this method
/*
		for(int i=0;i<=vertices.length;i+=3)
		{
			//System.out.println(vertices[i]);
			polygon(vertices[i],vertices[i+1],vertices[i+2]);
		}
*/
		polygon(vertices[0],vertices[1],vertices[2]);

	}
	
	
	
	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);	
		Vertex3D vertices[] = new Vertex3D[numVertices];
		
		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	
	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		
		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}

		// TODO: finish this method


        Vertex3D v = new Vertex3D(point,color);
		return v;

	}

	public Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method

		Point3DH pnt = new Point3DH(x,y,z);
        return pnt;
	}
	public Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method


        Color color = new Color(r,g,b);
        return color;
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		// TODO: finish this method

		ZbufferDrawable = new ZBufferDrawable(drawable);
		lineRenderer = DDALineRenderer.make();
		lineRenderer.drawLine(screenP1,screenP2,ZbufferDrawable);

	}
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {

		//inputs are object space - map to world space - screen
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		Vertex3D screenP3 = transformToCamera(p3);
		// TODO: finish this method

		Polygon polygon = Polygon.make(screenP1,screenP2,screenP3);

		if(filledRenderBool==true)
		{

			filledRenderer = FilledPolygonRenderer.make();
			filledRenderer.drawPolygon(polygon,drawable,vertexShader);
		}



		else if(wireframeRenderBool == true)
		{
			//System.out.println("True");
			polygon = Polygon.make(screenP1,screenP2,screenP3);
			wireframeRenderer= WireframePolygonRenderer.make();
			wireframeRenderer.drawPolygon(polygon,drawable,vertexShader);

		}



	}

	private Vertex3D transformToCamera(Vertex3D vertex) {
		// TODO: finish this method

		Dimensions d = new Dimensions(vertex.getIntX(),vertex.getIntY());
		makeWorldToScreenTransform(d);


        return vertex;
	}



}

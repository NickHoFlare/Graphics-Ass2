package ass2.spec;

public class Triangle {
	private double[] myX;
	private double[] myY;
	private double[] myZ;
	private double[][] myTriangle;
	private static final int numVertices = 3;
	
	// Create a Triangle object by supplying the coordinates of its vertices
	public Triangle(double[] x, double[] y, double[] z) {
		myX = x;
		myY = y;
		myZ = z;
		myTriangle[0] = myX;
		myTriangle[1] = myY;
		myTriangle[2] = myZ;
	}
	
	public Triangle(double x1, double x2, double x3, 
					 double y1, double y2, double y3, 
					 double z1, double z2, double z3) {
		myX[0] = x1;
		myX[1] = x2;
		myX[2] = x3;
		myY[0] = y1;
		myY[1] = y2;
		myY[2] = y3;
		myZ[0] = z1;
		myZ[1] = z2;
		myZ[2] = z3;
		myTriangle[0] = myX;
		myTriangle[1] = myY;
		myTriangle[2] = myZ;
	}
	
	public double[] getXCoords() {
		return myX;
	}
	
	public double[] getYCoords() {
		return myY;
	}
	
	public double[] getZCoords() {
		return myZ;
	}
	
	public double[][] getTriangle() {
		return myTriangle;
	}
	
	public int getNumVertices() {
		return numVertices;
	}
}

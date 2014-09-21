package ass2.spec;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * COMMENT: Comment HeightMap 
 * 
 * fields involved:
 * mySize: 		Contains the size of the world, in x-z coordinates
 * myAltitude: 	A 2D array of doubles that represent the y-coordinates at each x-z location.
 * myTrees: 	A list of trees that will be placed on the terrain.
 * myRoads: 	A list of roads that will be placed on the terrain.
 * mySunlight: 	The 3d position of the sun (as a directional light source?)
 * 
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;

    /**
     * Create a new terrain
     * 
     * mySize is initialised to the values of width and depth provided.
     * myAltitude creates a new 2d array to the dimensions of mySize.
     * myTrees and myRoads are initialised to construct a new empty arraylist each.
     * mySunlight is initialised to create a new empty array.
     * 
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
    }
    
    /**
     * Alternate constructor that accepts a Dimension instead of width/depth values.
     * @param size the Dimension object that contains the width and depth of the terrain
     */
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * -x1,x2 are the closest integer left and right grid bounds for x.
     * -z1,z2 are the closest integer top and bottom grid bounds for z.
     * 
     *  +------>  x
     *  |
     *  |
     *  |
     *  v
     *  z
     *  
     * -A unit square is formed, with the coordinates (x,z) somewhere within it.
     *  bottomLeft/RightAlt represent the y-values of the bottom left and right corners
     *  of the unit square respectively.
     * -topLeft/RightAlt represent the y-values of the top left and right corners
     *  of the unit square respectively.
     * -bottomX/YGrad represent the gradients of the straight lines formed in the (x,y) plane
     *  at the bottom and top sides of the unit square respectively.
     * -interpolatedBottom/Top represent the y-values on the planes formed at the bottom and
     *  top of the unit square at the given x value, respectively.
     * -yGrad represents the gradient of the straight line formed in the (z,y) plane
     *  at the given x value, using the previously calculated interpolatedBottom/Top y values
     * -Finally, altitude is the altitude / y-value at the given arbitrary (x,z) coordinates.
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        double altitude = 0;
        int x1 = (int) Math.floor(x);
        int x2 = (int) Math.ceil(x);
        int z1 = (int) Math.floor(z);
        int z2 = (int) Math.ceil(z);
        double bottomLeftAlt = getGridAltitude(x1, z2);
        double bottomRightAlt = getGridAltitude(x2, z2);
        double topLeftAlt = getGridAltitude(x1, z1);
        double topRightAlt = getGridAltitude(x2, z1);
        double bottomXGrad = calculateGradient(x1,x2,bottomLeftAlt,bottomRightAlt);
        double topXGrad = calculateGradient(x1,x2,topLeftAlt,topRightAlt);
        double interpolatedBottom = bottomXGrad * (x - x1) + bottomLeftAlt;
        double interpolatedTop = topXGrad * (x - x1) + topLeftAlt;
        double yGrad = calculateGradient(z1,z2,interpolatedTop,interpolatedBottom);
        
        altitude = yGrad * (z - z1) + interpolatedTop;
        
        return altitude;
    }

    public double calculateGradient(double x1, double x2, double y1, double y2) {
    	double gradient = (y2 - y1) / (x2 - x1);
    	return gradient;
    }
    
    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }
    
    /**
     * 
     * Draw a mesh iteratively, each mesh looks like this:
     * LeftTriangle starts from top right
     * RightTriangle starts from bottom left.
     * 
     * (x,z)   +-----+ (x+1,z)
   	 * 		   | LT /|  
     * 		   |  /  |
     * 		   |/ RT |
     * (x,z+1) +-----+ (x+1,z+1)
     * 
     * @param gl
     */
    public void draw(GL2 gl) {
    	for (int x = 0 ; x < mySize.getWidth()-1 ; x++) {
    		for (int z = 0 ; z < mySize.getHeight()-1 ; z++) {
    			Triangle triangleLeft = new Triangle
    					(x,x,x+1,
    					getGridAltitude(x,z), getGridAltitude(x,z+1), getGridAltitude(x+1,z),
    					z,z+1,z);
    			double[] leftNormal = calculateSurfaceNormal(triangleLeft);
    			Triangle triangleRight = new Triangle
    					(x,x+1,x+1,
    					getGridAltitude(x,z+1), getGridAltitude(x+1,z+1), getGridAltitude(x+1,z),
    					z+1,z+1,z);
    			double[] rightNormal = calculateSurfaceNormal(triangleRight);
    			
    			gl.glBegin(GL2.GL_TRIANGLES); // Left Triangle
	    			gl.glNormal3d(leftNormal[0],leftNormal[1],leftNormal[2]);
	    			gl.glVertex3d(triangleLeft.getXCoords()[0], triangleLeft.getYCoords()[0], triangleLeft.getZCoords()[0]); // Top
	    			gl.glVertex3d(triangleLeft.getXCoords()[1], triangleLeft.getYCoords()[1], triangleLeft.getZCoords()[1]); // Bottom Left
	    		    gl.glVertex3d(triangleLeft.getXCoords()[2], triangleLeft.getYCoords()[2], triangleLeft.getZCoords()[2]); // Bottom Right
    		    gl.glEnd();
	    		    gl.glBegin(GL2.GL_TRIANGLES); // Right Triangle
	    		    gl.glNormal3d(rightNormal[0],rightNormal[1],rightNormal[2]);
	    		    gl.glVertex3d(triangleRight.getXCoords()[0], triangleRight.getYCoords()[0], triangleRight.getZCoords()[0]); // Top
	    			gl.glVertex3d(triangleRight.getXCoords()[1], triangleRight.getYCoords()[1], triangleRight.getZCoords()[1]); // Bottom Right
	    		    gl.glVertex3d(triangleRight.getXCoords()[2], triangleRight.getYCoords()[2], triangleRight.getZCoords()[2]); // Top Right
    		    gl.glEnd();
    			
    		/*	
    			gl.glBegin(GL2.GL_TRIANGLES); // Left Triangle
    		    gl.glVertex3d(x, getGridAltitude(x,z), z); // Top
    		    gl.glVertex3d(x, getGridAltitude(x,z+1), z+1); // Bottom Left
    		    gl.glVertex3d(x+1, getGridAltitude(x+1,z+1), z+1); // Bottom Right
    		    gl.glEnd();
    		    gl.glBegin(GL2.GL_TRIANGLES); // Right Triangle
    			gl.glVertex3d(x, getGridAltitude(x,z), z); // Top
    			gl.glVertex3d(x+1, getGridAltitude(x+1,z+1), z+1); // Bottom Right
    		    gl.glVertex3d(x+1, getGridAltitude(x+1,z), z); // Top Right
    		    gl.glEnd();
    		*/
    		}
    	}
    }
    
    public double[] calculateSurfaceNormal(Triangle triangle) {
    	double[] normal = {0,0,0};
    	double xNormal = 0;
    	double yNormal = 0;
    	double zNormal = 0;
    	
    	for (int i = 0 ; i < triangle.getNumVertices() ; i++) {
    		xNormal += (triangle.getYCoords()[i] - triangle.getYCoords()[i+1]) *
    				   (triangle.getZCoords()[i] + triangle.getZCoords()[i+1]);
    	}
    	for (int j = 0 ; j < triangle.getNumVertices() ; j++) {
    		yNormal += (triangle.getZCoords()[j] - triangle.getZCoords()[j+1]) *
    				   (triangle.getXCoords()[j] + triangle.getXCoords()[j+1]);
    	}
    	for (int k = 0 ; k < triangle.getNumVertices() ; k++) {
    		zNormal += (triangle.getXCoords()[k] - triangle.getXCoords()[k+1]) *
    				   (triangle.getYCoords()[k] + triangle.getYCoords()[k+1]);
    	}
    	
    	normal[0] = xNormal;
    	normal[1] = yNormal;
    	normal[2] = zNormal;
    	
    	return normal;
    }
}

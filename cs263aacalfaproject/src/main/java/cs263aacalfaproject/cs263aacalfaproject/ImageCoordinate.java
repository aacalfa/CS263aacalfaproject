package cs263aacalfaproject.cs263aacalfaproject;

/**
 * Holds image coordinates
 * @author Andre Abreu Calfa
 *
 */
public class ImageCoordinate {
	private int xcoord;
    private int ycoord;
    
    /**
     * Represents an image coordinate
     * @param x X coordinate
     * @param y Y coordinate
     */
    public ImageCoordinate(int x, int y){
        this.xcoord = x;
        this.ycoord = y;
    }
    
    /**
     * Returns the x coordinate of the image.
     * @return
     */
    public int getXCoord(){ return xcoord; }
    
    /**
     * Returns the y coordinate of the image.
     * @return
     */
    public int getYCoord(){ return ycoord; }
    
    /**
     * Sets the x coordinate of the image.
     * @param x New x coordinate.
     * @return
     */
    public int setXCoord(int x){ this.xcoord = x; return xcoord;}
    
    /**
     * Sets the y coordinate of the image.
     * @param y New y coordinate.
     * @return
     */
    public int setYCoord(int y){ this.ycoord = y; return ycoord; }
}

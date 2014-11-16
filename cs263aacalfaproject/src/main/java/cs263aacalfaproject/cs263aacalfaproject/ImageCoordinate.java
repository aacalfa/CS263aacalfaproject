package cs263aacalfaproject.cs263aacalfaproject;

public class ImageCoordinate {
	private int xcoord;
    private int ycoord;
    public ImageCoordinate(int x, int y){
        this.xcoord = x;
        this.ycoord = y;
    }
    public int getXCoord(){ return xcoord; }
    public int getYCoord(){ return ycoord; }
    public int setXCoord(int w){ this.xcoord = w; return xcoord;}
    public int setYCoord(int h){ this.ycoord = h; return ycoord; }
}

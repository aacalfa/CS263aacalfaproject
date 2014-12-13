package cs263aacalfaproject.cs263aacalfaproject;

/**
 *  Implementation of a Pair object.
 * @author Andre Abreu Calfa
 *
 * @param <L> Left element of pair.
 * @param <R> Right element of pair.
 */
public class Pair<L,R> {
    private L l;
    private R r;
    
    /**
     * Represents a pair.
     * @param l Left element.
     * @param r Right element.
     */
    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }
    
    /**
     * Returns left element of pair.
     * @return
     */
    public L getL(){ return l; }
    
    /**
     * Returns right element of pair.
     * @return
     */
    public R getR(){ return r; }
    
    /**
     * Sets left element of pair.
     * @param l
     */
    public void setL(L l){ this.l = l; }
    
    /**
     * Sets right element of pair.
     * @param r
     */
    public void setR(R r){ this.r = r; }
}
/** A Wumpus World is made up of tiles on which we can place wumpuses,
 *  hunters and other critters. Each tile has an associated terrain, and a
 *  pair of coordinates. 
 *  
 *  User should connect tiles in sensible ways. Specifically, if a tile A
 *  with coordinates (x,y) is connected north of a tile with coordinates 
 *  (u,v), then x should be u and y should be v - 1. So moving east should increase
 *  the x-coordinate by 1, and moving north should decrease the y-coordinate by 1.
 
 *  Once two tiles are connected together, they cannot be
 *  disconnected, nor have their connections overridden. Attempting to overwrite an
 *  existing connection should throw an exception.     
    
    @see Terrain 
    @see Direction    
*/

public abstract class Tile {
    /** Attempt to link this tile to {@code tile} in a given
	direction. The method should also automatically link {@code tile} to
	this tile in the *opposite* direction.    
	
	This means that if {@code m.connect(d,r)} is successful, then we 
	must have {@code m.to(d).equals(r);} */
    public abstract void connect(Direction d, Tile tile);

    /** Get the tile, if any, in the direction {@code d}.*/
    public abstract Tile to(Direction d);

    /** Returns true if there are <i>no</i> tiles in the
     * direction {@code d}. This means there is a <i>boundary</i> of
     * the map in this direction.*/
    public abstract boolean boundary(Direction d);
    
    public abstract Terrain getTerrain();
    public abstract void setTerrain(Terrain t);
    	
    /** Get the x-coordinate of this tile.*/
    public abstract int getX();

    /** Get the y-coordinate of this tile.*/
    public abstract int getY();
    
    protected static void verifyDistance(Tile fromTile, Direction d, Tile toTile) {
    	if (d.dx() + fromTile.getX() - toTile.getX() != 0 ||
    		d.dy() + fromTile.getY() - toTile.getY() != 0) {
    		throw new IllegalArgumentException("Attempt to link " + fromTile + " to " + toTile
    				+ " in direction " + d);
    	}
    }
    
    protected static void ensureConnection(Tile fromTile, Direction d, Tile toTile) {
    	if (fromTile.boundary(d) || !fromTile.to(d).equals(toTile)) {
    		fromTile.connect(d, toTile);
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof Tile) {
    		Tile tile = (Tile) obj;
    		return getX() == tile.getX() && getY() == tile.getY();
    	}
    	return false;
    }
    
    @Override
    public int hashCode() {
    	return getX() ^ getY();
    }
    
    public String toString() {
    	return "Tile (" + getX() + "," + getY() + ")";
    }     
}
/** 
 * This type defines the four compass directions applicable
 * in the wumpus world. Each compass direction is associated
 * with a translation {@code (dx,dy)}.
*/

public enum Direction {
    NORTH (0,-1),
	EAST (1,0),
	SOUTH (0,1),
	WEST (-1,0);

    private final int dx;
    private final int dy;

    private Direction(int dx, int dy) {
	this.dx = dx;
	this.dy = dy;
    }

    public int dx() { return dx; }
    public int dy() { return dy; }

    /** Return the opposite direction to @code{d}. */
    public static Direction inverse(Direction d) {
	switch (d) {
	    case NORTH: return SOUTH;
	    case EAST: return WEST; 
	    case SOUTH: return NORTH;
	    case WEST: return EAST; 
	    }
	throw new IllegalArgumentException("Direction cases not exhaustive.");
    }
}
import java.util.EnumMap;

/** 
 * Grid maps are implemented as two-dimensional arrays, giving a
 * two-dimensional grid of tiles linked internally along the compass
 * directions. Once the grid map is created, the interior links cannot be
 * modified. However, a grid map <i>can</i> be linked to other maps
 * by connecting outwards from its borders. 
 */

public class GridMap {

    // Any part of the edge of a grid map can be connected to more tiles.
    // The connections at the north, east, south and west edges are implemented
    // as the arrays northWays, eastWays, southWays and westWays respectively.
    private EnumMap<Direction, Tile[]> borders;;

    // ox and oy give the coordinates of the top left of the grid-map, while w and
    // h give the height.
    private int ox,oy,w,h;

    // The following inner class defines the type of tiles that sit on a
    // GridMap.
    private class GridTile extends Tile {
    	private int x,y;
    	private Terrain terrain;
    	
		public Tile to(Direction d) {
		    if (boundary(d)) {
		    	throw new IllegalArgumentException("Off the map. Use the boundary(Direction) method to check for boundaries.");
		    }
		    else {
		    	return toAux(d);
		    }
		}
	
		// Common to the to and boundary methods.
		private Tile toAux(Direction d) {
	  	    int tx = x + d.dx();
		    int ty = y + d.dy();
	
		    if (contains(tx,ty)) 
		    	return tiles[tx - ox][ty - oy];
		    else 
		    	return borders.get(d)[borderIndex(tx - ox,ty - oy)];		    		    
		}
	
		public boolean boundary(Direction d) {
		    return (toAux(d) == null);
		}
	
		public Terrain getTerrain() {
		    return terrain;
		}
	    
		public GridTile(int x, int y, Terrain defaultTerrain) {
		    this.x = x;
		    this.y = y;
		    this.terrain = defaultTerrain;
		}
	
		public void setTerrain(Terrain terrain) {
		    this.terrain = terrain;
		}
	
		public void connect(Direction d, Tile tile) {
			verifyDistance(this, d, tile);
		    if (!boundary(d)) 
		    	throw new IllegalArgumentException("Attempt to overwrite a connection.");
		    else {
		    	int tx = d.dx() + x;
			    int ty = d.dy() + y;
		    	borders.get(d)[borderIndex(tx - ox,ty - oy)] = tile;
		    	ensureConnection(tile, Direction.inverse(d), this);
		    }		    		    	  
		}
		
		private int borderIndex(int x, int y) {
			if (x < 0 || x >= w) return y;
			else return x;
		}
	
		public int getX() {
		    return x;
		}
	
		public int getY() {
		    return y;
		}				
    }

    private GridTile[][] tiles;

    /** A grid map is created from a point {@code (ox,oy)} which are the  
     * coordinates of the most {@code NORTH}-{@code WEST} tile, a width {@code w} and a 
     * height {@code h}. Tiles
     * are then created at every coordinate within the bounding 
     * rectangle. Each tile is created with a default terrain. 
     */
    public GridMap(int ox, int oy, int w, int h, Terrain defaultTerrain) {
		this.ox = ox;
		this.oy = oy;
		this.w = w;
		this.h = h;
		tiles = new GridTile[w][h];
		borders = new EnumMap<Direction, Tile[]>(Direction.class);
		for (Direction d : Direction.values()) {
			borders.put(d, new Tile[Math.max(w, h)]);
		}
		
		for (int i=0; i<w; i++) {
		    for (int j=0; j<h; j++) {
			tiles[i][j] = new GridTile(ox + i, oy + j, defaultTerrain);
		    }
		}
    }

    /** 
     * Returns true if the coordinates {@code (x,y)} exist on the grid. 
     */
    public boolean contains(int x, int y) {
    	return (ox <= x && oy <= y && x - ox < w && y - oy < h);
    }
    
    /** 
     * Look up a tile by a coordinate. 
     */    
    public Tile get(int x, int y) {
    	if (contains(x,y))
    		return tiles[x - ox][y - oy];
    	else throw new IllegalArgumentException("Coordinates are not on the map.");
    }
}
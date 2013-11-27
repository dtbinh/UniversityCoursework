import java.util.Map;
import java.util.EnumMap;

/** 
 * Graph tiles are a very simple kind of tile. Each graph tile
 * knows about its neighbours in each of the four compass directions, and neighbours
 * can be connected to any graph tile, so long as this does not override
 * existing connections, in which case an exception is thrown.
 *     
*/
    
public class GraphTile extends Tile {
    private Terrain terrain;
    private int x,y;

    private Map<Direction,Tile> adjacents;

    public void connect(Direction d, Tile tile) {
    	verifyDistance(this, d, tile);
    	if (to(d) != null)
    		throw new IllegalArgumentException("Attempt to overwrite a connection.");
	
    	adjacents.put(d, tile);
    	ensureConnection(tile, Direction.inverse(d), this);    	
    }

    public Tile to(Direction d) {
    	return adjacents.get(d);
    }

    public boolean boundary(Direction d) {
    	return adjacents.get(d) == null;
    }

    public Terrain getTerrain() {
    	return terrain;
    }
    
    public void setTerrain(Terrain t) {
    	this.terrain = t;
    }
    
    public GraphTile(int x, int y, Terrain terrain) {
    	this.terrain = terrain;
    	this.adjacents = new EnumMap<Direction, Tile>(Direction.class);
    	this.x = x;
    	this.y = y;    	
    }

    public int getX() {
    	return x;
    }

    public int getY() {
    	return y;
    }
 }
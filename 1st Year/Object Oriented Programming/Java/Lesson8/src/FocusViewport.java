import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * A FocusViewport represents a window into the Wumpus World centred on and reachable from a particular 
 * {@code Tile} with a given width and height. 
 */
public class FocusViewport {
	private boolean limitedDepth;
	private int maxDepth;
	private Tile focus;
	private ArrayList<Tile> tiles;
	private int w, h;
	
	private void initialise(Tile focus, int w, int h) {
		this.focus = focus;
		this.w = w;
		this.h = h;
		tiles = new ArrayList<Tile>();			
	}
	
	
	/**
	 * @param focus The tile which is at the centre of the viewport. All tiles visible
	 * through the viewport must ultimately be connected to centre.
	 * @param depth The visible tiles are found by traversing the graph of tiles starting
	 * at focus. In the case of infinitely large or very large maps, the user can supply this field 
	 * to limit the depth of the traversal.
	 * @param w The width of the viewport
	 * @param h The height of the viewport
	 */
	public FocusViewport(Tile focus, int depth, int w, int h) {
		initialise(focus, w, h);
		this.limitedDepth = true;
		this.maxDepth = depth;
		viewport();	
	}
	
	
	/**
	 * Constructs a focused viewport with no depth limit on the graph traversal.
	 * 
	 * @see FocusViewport
	 */
	public FocusViewport(Tile focus, int w, int h) {
		initialise(focus, w, h);
		this.limitedDepth = false;
		this.maxDepth = 0;
		viewport();
	}
	
	/**
	 * @return The x-coordinate of the (0,0) tile relative to the top-left corner of the viewport.  
	 */
	public int dx() {
		return w / 2 - focus.getX();
	}
	
	/**
	 * @return The y-coordinate of the (0,0) tile relative to the top-left corner of the viewport. 
	 */
	public int dy() {
		return h / 2 - focus.getY();
	}

	/**
	 * Reconstructs the viewport
	 * 
	 * @param tile The tile that will become the focus.
	 */
	public void refocus(Tile tile) {
		focus = tile;
		viewport();		
	}
	
	private void viewport() {
		viewport(focus);
	}
			
	/**
	 * Returns true if the supplied coordinates are in the bounds of the viewport.
	 * 
	 */
	public boolean inRect(int x, int y) {
		return (x + dx() >= 0 && x + dx() <= w && y + dy() >= 0 && y + dy() <= h);
	}		
	
	private void viewport(Tile tile) {
		tiles.clear();
		ArrayList<Tile> successors = new ArrayList<Tile>();
		HashSet<Tile> visited = new HashSet<Tile>();
		
		successors.add(tile);
		for (int i = 0; i < maxDepth || !limitedDepth; i++) {
			ArrayList<Tile> newSuccessors = new ArrayList<Tile>();
			for (Tile current : successors) {
				tiles.add(current);
				for (Direction d : Direction.values()) {				
					if (!current.boundary(d)) {						
						Tile successor = current.to(d);
						if (!visited.contains(successor) && 
								inRect(successor.getX(),successor.getY())) {
							newSuccessors.add(successor);
							visited.add(successor);
						}
					}
				}
			}
			successors = newSuccessors;
		}		
	}
	
	
	/**
	 * @return A collection of all tiles visible in this viewport.
	 */
	public Collection<Tile> getVisibleTiles() {
		return tiles;
	}
}	
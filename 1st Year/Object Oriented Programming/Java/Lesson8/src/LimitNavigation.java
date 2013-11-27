import java.util.ArrayList;
import java.util.Collection;


/**
 * A {@code LimitNavigation} is constructed from a navigation N and a list of terrain types T, by 
 * effectively making those parts of the map which are not in T invisible to N.
 */
public class LimitNavigation implements Navigation {

	private Navigation subNavigation;
	private Collection<Terrain> traversableTerrain;
	
	/**
	 * @param subNavigation The navigation to limit.
	 * @param traversableTerrain The terrain this navigation is allowed to traverse.
	 */
	public LimitNavigation(Navigation subNavigation, Collection<Terrain> traversableTerrain) {
		this.subNavigation = subNavigation;
		this.traversableTerrain = traversableTerrain;
	}
	
	private ArrayList<Direction> traversableDirections(Tile tile, ArrayList<Direction> ds) {
		ArrayList<Direction> traversable = new ArrayList<Direction>();
		for (Direction d : ds) {
			if (traversableTerrain.contains(tile.to(d).getTerrain()))
				traversable.add(d);
		}
		return traversable;
	}
	
	@Override
	public Direction nextDirection(Tile tile,
			ArrayList<Direction> directions) {
		return subNavigation.nextDirection(tile, traversableDirections(tile, directions));
	}

	@Override
	public Navigation nextNavigation(Tile tile,
			ArrayList<Direction> directions) {
		subNavigation = subNavigation.nextNavigation(tile, traversableDirections(tile, directions));
		return this;
	} 

}

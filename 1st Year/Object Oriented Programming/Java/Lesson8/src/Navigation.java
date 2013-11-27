import java.util.ArrayList;

/**
 * A navigation represents a strategy for navigating the Wumpus World. A navigation is made up from
 * two components:
 *  <ul>
 *   	<li>
 *  	the next direction to move in
 *   	</li>
 *   	<li>
 *   	the strategy to use from then on.
 *   	</li>
 *	</ul>
 */
public interface Navigation {
	
	/**
	 * @param directions A list of directions to choose from.
	 * @return The direction to move in from {@code tile}. For consistent behaviour, this returned value
	 * should be a member of {@code directions}. Return {@code null} to indicate a decision to remain
	 * in the same place.
	 * 
	 */
	Direction nextDirection(Tile tile, ArrayList<Direction> directions);
	
	/**
	 * @param directions A list of directions to choose from.
	 * @return The strategy to use after we have moved in {@code nextDirection(tile, directions)}. 
	 */
	Navigation nextNavigation(Tile tile, ArrayList<Direction> directions);
}

import java.util.ArrayList;

/**
 * A {@code Stationary} navigation strategy is a very primitive strategy, where we simply stay in
 * the same place indefinitely. 
 *
 */
public class Stationary implements Navigation {

	@Override
	public Direction nextDirection(Tile tile,
			ArrayList<Direction> directions) {
		return null;
	}

	@Override
	public Navigation nextNavigation(Tile tile,
			ArrayList<Direction> directions) {
		return this;
	}

}

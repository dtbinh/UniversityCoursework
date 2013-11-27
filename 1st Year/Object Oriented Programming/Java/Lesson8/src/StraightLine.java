import java.util.ArrayList;

/**
 * StraightLine is a primitive strategy in which we move in a fixed-direction indefinitely, so long
 * as the direction is available.
 */
public class StraightLine implements Navigation {

	private Direction d;
	
	public StraightLine(Direction d) {
		this.d = d;
	}
	
	@Override
	public Direction nextDirection(Tile tile,
			ArrayList<Direction> directions) {
		if (directions.contains(d)) {
			return d;
		}
		else return null;
	}

	@Override
	public Navigation nextNavigation(Tile tile,
			ArrayList<Direction> directions) {
		return this;
	}

}

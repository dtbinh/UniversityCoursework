import java.util.ArrayList;

/**
 * A ChaseNavigation seeks out a moving target agent by always moving in the first direction which
 * brings it closer to the target. If there is no such direction, the agent remains stationary. 
 */
public class ChaseNavigation implements Navigation {
	private Agent target;
	
	public ChaseNavigation(Agent target) {
		this.target = target;
	}
	
	private int distanceToAgent(int x, int y) {
		return Math.abs(target.getLocation().getX() - x) + 
			Math.abs(target.getLocation().getY() - y);
	}
	
	@Override
	public Direction nextDirection(Tile tile,
			ArrayList<Direction> directions) {
		int x = tile.getX();
		int y = tile.getY();
		Direction bestDirection = null;
		for (Direction d : directions) {
			if (distanceToAgent(d.dx() + x, d.dy() + y) < distanceToAgent(x,y)) {
				bestDirection = d;				
			}
		}
		return bestDirection;
	}

	@Override
	public Navigation nextNavigation(Tile tile,
			ArrayList<Direction> directions) {
		return this;
	}
}

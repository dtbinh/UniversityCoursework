import java.util.ArrayList;

/**
 * SwitchAfter composes two navigation strategies into one. The resulting strategy uses the first
 * strategy for a set number of turns, after which the alternative strategy is used. 
 *
 */
public class SwitchAfter implements Navigation {
	private Navigation subNav;
	private Navigation nextNav;
	private int turnsTaken, maxTurns;
	

	/**
	 * @param The strategy to use initially.
	 * @param The strategy to use after @{code turns}
	 */
	public SwitchAfter(Navigation subNav, Navigation nextNav, int turns) {
		this.subNav = subNav;
		this.nextNav = nextNav;
		this.turnsTaken = 0;
		this.maxTurns = turns;		
	}
	
	public void setNextNav(Navigation nextNav) {
		this.nextNav = nextNav;
	}
	
	@Override
	public Direction nextDirection(Tile tile,
			ArrayList<Direction> directions) {
		return subNav.nextDirection(tile, directions);		
	}

	@Override
	public Navigation nextNavigation(Tile tile,
			ArrayList<Direction> directions) {
		turnsTaken++;
		if (turnsTaken >= maxTurns) {
			turnsTaken = 0;
			return nextNav;
		}
		else {
			subNav = subNav.nextNavigation(tile, directions);
			return this;
		}
	}
}

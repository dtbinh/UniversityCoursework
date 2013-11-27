import java.util.ArrayList;

/**
 * This navigation combines two existing navigations, a <i>default</i> and an <i>alternative</i> into
 * one. The composite navigation uses the <i>default</i> until the <i>default</i> strategy first becomes
 * stationary, after which it is replaced by the <i>alternative</i>.
 * 
 */
public class SwitchOnStuck implements Navigation {
	private Navigation defaultNav;
	private Navigation altNav;
	
	public SwitchOnStuck(Navigation defaultNav, Navigation altNav) {
		this.defaultNav = defaultNav;
		this.altNav = altNav;
	}
	
	public void setAlt(Navigation alt) {
		altNav = alt;
	}
	
	@Override
	public Direction nextDirection(Tile tile,
			ArrayList<Direction> directions) {
		Direction d = defaultNav.nextDirection(tile, directions);
		if (d == null) {
			d = altNav.nextDirection(tile, directions);
		}
		return d;
	}

	@Override
	public Navigation nextNavigation(Tile tile,
			ArrayList<Direction> directions) {
		Direction d = defaultNav.nextDirection(tile, directions);
		if (d == null) return altNav;
		else {
			defaultNav = defaultNav.nextNavigation(tile, directions);
			return this;
		}
	}
}

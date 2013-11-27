import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * An {@code Agent} represents a denizen of the Wumpus World. An agent has a location in the world,
 * a graphical avatar, and a navigation strategy which describes how it moves in the world. 
 *
 */
public class Agent {
	private Tile location;
	private Image avatar;
	private Navigation moveStrategy;
		
	/**
	 * An example Wumpus avatar, downloaded when the program is first run. 
	 */
	public static Image WUMPUS;
	
	/**
	 * An example Hunter avatar, downloaded when the program is first run. 
	 */
	public static Image HUNTER;
	
	static {
		try {			
			String urlprefix = "http://homepages.inf.ed.ac.uk/s0785695/images/";
			WUMPUS = ImageIO.read(new URL(urlprefix + "wumpus.gif"));
			HUNTER = ImageIO.read(new URL(urlprefix + "hunter.png"));
		}
		catch (Exception e)  {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param startLocation The agent's starting location
	 * @param moveStrategy The stategy the agent uses to navigate the world
	 * @param avatar An @{code Image} avatar to represent the agent on the screen.
	 */
	public Agent(Tile startLocation, Navigation moveStrategy, Image avatar) {
		this.location = startLocation;
		this.avatar = avatar;
		this.moveStrategy = moveStrategy;
	}
	
	public Tile getLocation() {
		return location;
	}
	
	public Image getImage() {
		return avatar;
	}
	
	/**
	 * Moves the agent in the direction specified by its {@code moveStrategy}, and updates the strategy.
	 */
	public void update() {
		ArrayList<Direction> possibleMoves = new ArrayList<Direction>();
		for (Direction d : Direction.values()) {
			if (!location.boundary(d)) {
				possibleMoves.add(d);
			}		
		}
		Direction nextDirection = moveStrategy.nextDirection(location, possibleMoves);
		moveStrategy = moveStrategy.nextNavigation(location, possibleMoves);
		if (nextDirection != null) {
			location = location.to(nextDirection);
		}
	}
}

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Instances of this class represent a visualisation of a small region of the Wumpus World.  
 * 
 */
public class WumpusRunner {
	private static final int DEPTH = 20;
	private WumpusCanvas c;

	/**
	 * Create a {@code WumpusRunner} in a new window.
	 * 
	 * @param title The title of the window.
	 * @param tile The centre location of the map.
	 * @param width The width of the visible part of the map.
	 * @param height The height of the visible part of the map.
	 */
	public WumpusRunner(String title, Tile tile, int width, int height) {
		JFrame f = new JFrame(title);
        c = new WumpusCanvas(tile,DEPTH,width,height);
        c.setBounds(0,0,width * WumpusCanvas.TILE_WIDTH, height * WumpusCanvas.TILE_HEIGHT);
        f.add(c);                
        f.pack();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
        c.createBufferStrategy(2);
        c.init();
	}
	
	/**
	 * Runs a Wumpus game, where the visible region of the Wumpus World is continually adjusted to track
	 * the hunter {@code Agent}. The game will end when the agents occupy the same square. 
	 * 
	 */
	public void simulate(Agent hunter, Agent hunted) {
		c.addAgent(hunter);
        c.addAgent(hunted);
        
        while (true) {
        	if (hunter.getLocation().equals(hunted.getLocation())) {
        		break;
        	}
        		
        	try {
				Thread.sleep(200);
			} 
        	catch (InterruptedException e) {
				e.printStackTrace();
			}        	
        	hunter.update();
        	hunted.update();
        	c.refocus(hunter.getLocation());        	
        	c.repaint();
        }
    }		
}

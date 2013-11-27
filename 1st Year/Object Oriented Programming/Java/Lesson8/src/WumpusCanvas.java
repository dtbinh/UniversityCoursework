import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;

import javax.imageio.ImageIO;

public class WumpusCanvas extends Canvas {
	public static final int TILE_WIDTH = 50;
	public static final int TILE_HEIGHT = 50;
	
	private FocusViewport viewp;
	private static final long serialVersionUID = 1L;
	private static EnumMap<Terrain, Image> images;
	private ArrayList<Agent> agents;
	private Graphics buffer;
	private Image hidden;
	private Dimension dim;
	
	static {
		try {			
			String urlprefix = "http://homepages.inf.ed.ac.uk/s0785695/images/";
			images = new EnumMap<Terrain, Image>(Terrain.class);
			images.put(Terrain.GRASS, ImageIO.read(new URL(urlprefix + "grass.jpg")));
			images.put(Terrain.WATER, ImageIO.read(new URL(urlprefix + "river.jpg")));
			images.put(Terrain.ROAD, ImageIO.read(new URL(urlprefix + "road.jpg")));			
		}
		catch (Exception e)  {
			e.printStackTrace();
		}
	}
	
	private WumpusCanvas(int w, int h) {
		this.setBounds(0,0,w * TILE_WIDTH, h * TILE_HEIGHT);
	}
	
	public WumpusCanvas(Tile focus, int depth, int w, int h) {
		this(w, h);
		agents = new ArrayList<Agent>();
		viewp = new FocusViewport(focus, depth, w, h);
	}
	
	public WumpusCanvas(Tile focus, int w, int h) {
		this(w, h);
		agents = new ArrayList<Agent>();
		viewp = new FocusViewport(focus, w, h);
	}
	
	public void init() {
		dim = getSize();
		hidden = createImage(dim.width, dim.height);
		buffer = hidden.getGraphics();
	}
	
	public void addAgent(Agent a) {
		agents.add(a);
	}
	
	public void refocus(Tile tile) {
		viewp.refocus(tile);
	}
	
	public void paint(Graphics g) {
		buffer.clearRect(0, 0, dim.width, dim.height);
		for (Tile r : viewp.getVisibleTiles()) {			
			buffer.drawImage(images.get(r.getTerrain()), TILE_WIDTH * (r.getX() + viewp.dx()), 
					TILE_HEIGHT * (r.getY() + viewp.dy()), 
					TILE_WIDTH, TILE_HEIGHT, null);
		}
		for (Agent a : agents) {
			int x = a.getLocation().getX();
			int y = a.getLocation().getY();
			if (viewp.inRect(x,y)) {
				buffer.drawImage(a.getImage(), TILE_WIDTH * (x + viewp.dx()), 
						TILE_HEIGHT * (y + viewp.dy()), 
						TILE_WIDTH, TILE_HEIGHT, null);				
			}
		}
		g.drawImage(hidden,0,0,this);
	}
	
	public void update(Graphics g) {
		buffer.clearRect(0, 0, dim.width, dim.height);
		for (Tile r : viewp.getVisibleTiles()) {			
			buffer.drawImage(images.get(r.getTerrain()), TILE_WIDTH * (r.getX() + viewp.dx()), 
					TILE_HEIGHT * (r.getY() + viewp.dy()), 
					TILE_WIDTH, TILE_HEIGHT, null);
		}
		for (Agent a : agents) {
			int x = a.getLocation().getX();
			int y = a.getLocation().getY();
			if (viewp.inRect(x,y)) {
				buffer.drawImage(a.getImage(), TILE_WIDTH * (x + viewp.dx()), 
						TILE_HEIGHT * (y + viewp.dy()), 
						TILE_WIDTH, TILE_HEIGHT, null);				
			}
		}
		g.drawImage(hidden,0,0,this);		
	}
}


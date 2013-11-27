public class ExampleMap {
	
	public static void main(String[] args) {
		Terrain road = Terrain.ROAD;
    	Terrain grass = Terrain.GRASS;
    	Terrain river = Terrain.WATER;
    	
    	GraphTile grass1 = new GraphTile(2,3,grass);
    	GraphTile river1 = new GraphTile(3,3,river);
    	GraphTile grass2 = new GraphTile(4,3,grass);
    	GraphTile river2 = new GraphTile(3,4,river);
    	GraphTile grass3 = new GraphTile(5,4,grass);
    	GraphTile road1 = new GraphTile(3,5,road);
    	GraphTile road2 = new GraphTile(4,5,road);
    	GraphTile road3 = new GraphTile(5,5,road);
    	
    	grass1.connect(Direction.EAST, river1);
    	river1.connect(Direction.EAST, grass2);
    	river1.connect(Direction.SOUTH, river2);
    	river2.connect(Direction.SOUTH, road1);
    	road1.connect(Direction.EAST, road2);
    	road2.connect(Direction.EAST, road3);
    	road3.connect(Direction.NORTH, grass3);
    	
    	new WumpusRunner("Wumpus World", river2, 5, 3);
	}
} 

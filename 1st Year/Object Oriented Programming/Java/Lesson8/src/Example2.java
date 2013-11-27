
public class Example2 {
	
	static Terrain road;
	static Terrain grass;
	static Terrain water;
	
	public static void main(String[] args) {
		
		road = Terrain.ROAD;
		grass = Terrain.GRASS;
		water = Terrain.WATER;
		
		GridMap water1 = new GridMap(0,4,3,3,water);
		GridMap water2 = new GridMap(4,0,3,3,water);
		GridMap water3 = new GridMap(8,4,3,3,water);
		GridMap water4 = new GridMap(4,8,3,3,water);
		GridMap grass1 = new GridMap(4,4,3,3,grass);
		
		GraphTile road1 = new GraphTile(3,5,road);
		GraphTile road2 = new GraphTile(7,5,road);
		GraphTile road3 = new GraphTile(5,3,road);
		GraphTile road4 = new GraphTile(5,7,road);
		
		road1.connect(Direction.WEST, water1.get(2, 5));
		road1.connect(Direction.EAST, grass1.get(4, 5));
		road2.connect(Direction.WEST, grass1.get(6, 5));
		road2.connect(Direction.EAST, water3.get(8, 5));
		road3.connect(Direction.NORTH, water2.get(5, 2));
		road3.connect(Direction.SOUTH, grass1.get(5, 4));
		road4.connect(Direction.NORTH, grass1.get(5, 6));
		road4.connect(Direction.SOUTH, water4.get(5, 8));
		
		new WumpusRunner("Wumpus World", grass1.get(5, 5), 11,11);
		
		
	}

}

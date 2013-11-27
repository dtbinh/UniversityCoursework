
public class Example3 {
	
	static Terrain road;
	static Terrain grass;
	static Terrain water;
	
	public static void main(String[] args) {
		
		road = Terrain.ROAD;
		grass = Terrain.GRASS;
		water = Terrain.WATER;
	
		GridMap mixed = new GridMap(0,0,4,4,grass);
		GridMap square = new GridMap(4,3,4,4,road);
		
		GraphTile road1 = new GraphTile(4,1,road);
		GraphTile road2 = new GraphTile(5,1,road);
		GraphTile road3 = new GraphTile(6,1,road);
		GraphTile road4 = new GraphTile(7,1,road);
		GraphTile road5 = new GraphTile(7,2,road);
		
		(mixed.get(1,1)).setTerrain(water);
		(mixed.get(1,2)).setTerrain(water);
		(mixed.get(2,1)).setTerrain(water);
		(mixed.get(2,2)).setTerrain(water);
		
		mixed.get(3,1).connect(Direction.EAST, road1);
		road1.connect(Direction.EAST, road2);
		road2.connect(Direction.EAST, road3);
		road3.connect(Direction.EAST, road4);
		road4.connect(Direction.SOUTH, road5);
		road5.connect(Direction.SOUTH, square.get(7,3));
		
		new WumpusRunner("Wumpus World", square.get(5, 5), 11,11);
	}

}

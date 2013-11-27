
public class Example1 {
	
	public static void main(String[] args) {
		Terrain road = Terrain.ROAD;
		Terrain grass = Terrain.GRASS;
		Terrain river = Terrain.WATER;
		
		GraphTile road1 = new GraphTile(0,0,road);
    	GraphTile road2 = new GraphTile(0,1,road);
    	GraphTile road3 = new GraphTile(0,2,road);
    	GraphTile road4 = new GraphTile(1,0,road);
    	GraphTile road5 = new GraphTile(1,2,road);
    	GraphTile road6 = new GraphTile(2,0,road);
    	GraphTile road7 = new GraphTile(2,1,road);
    	GraphTile road8 = new GraphTile(2,2,road);
    	
    	road1.connect(Direction.EAST, road4);
    	road1.connect(Direction.SOUTH, road2);
    	road2.connect(Direction.SOUTH, road3);
    	road3.connect(Direction.EAST, road5);
    	road5.connect(Direction.EAST, road8);
    	road4.connect(Direction.EAST, road6);
    	road6.connect(Direction.SOUTH, road7);
    	road7.connect(Direction.SOUTH, road8);
    	
    	new WumpusRunner("Wumpus World", road8, 4,4);
    	

	}

}

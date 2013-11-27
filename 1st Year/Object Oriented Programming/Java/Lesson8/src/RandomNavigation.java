import java.util.ArrayList;
import java.util.Random;

public class RandomNavigation implements Navigation {
	Random r; 
	
	public RandomNavigation() {
		r = new Random();
	}
	
	@Override
	public Direction nextDirection(Tile tile,
			ArrayList<Direction> directions) {
		if (directions.size() == 0) return null;
		else return directions.get(r.nextInt(directions.size()));		
	}

	@Override
	public Navigation nextNavigation(Tile tile,
			ArrayList<Direction> directions) {
		return this;
	}

}

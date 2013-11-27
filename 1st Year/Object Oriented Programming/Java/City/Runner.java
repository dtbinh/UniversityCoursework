
public class Runner {
	public static void main(String[] args) {
		World myWorld = new World();
		City city1 = new City();
		City city2 = new City();
		City city3 = new City();
		City city4 = new City();
		myWorld.setWorldSize(30,30);
		city1.setLocation(10,10);
		city2.setLocation(10,20);
		city3.setLocation(20,10);
		city4.setLocation(20,20);
		myWorld.addCity(city1);
		myWorld.addCity(city2);
		myWorld.addCity(city3);
		myWorld.addCity(city4);
		myWorld.dropBomb(17,17,8);
	}
}

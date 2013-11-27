public class Runner {
    public static void main(String[] args) {
	World myWorld = new World();
        Wumpus wump = new Wumpus();
	myWorld.setWorldSize(10,10);
	myWorld.addWumpus(wump);
	wump.setLocation(0,0);
	//Doing stuff
	while(true) {//big loop
	    while() {
		wump.moveRight();}
	    while(wump.moveDown()) {
		wump.moveDown();}
	    while(wump.moveLeft()) {
		wump.moveLeft();}
	    while(wump.moveUp()) {
		wump.moveUp();}

	    myWorld.updateDisplay();


	}//big loop


    }
}
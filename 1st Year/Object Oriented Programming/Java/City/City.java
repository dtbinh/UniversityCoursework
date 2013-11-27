import java.util.Scanner;
import java.lang.Math;


public class City {
	
	public int x = 0;
	public int y = 0;
	public boolean destroyed = false;
	
	void setLocation(int newx, int newy) {
		x=newx;
		y=newy;
	}
	double distance(int newx, int newy) {
		
		double holder = (newx*newx) + (newy*newy);
		double z = Math.sqrt(holder);
		return z;
		
	}
	void explosion(int newx, int newy, int blastRadius) {
		City dist = new City();
		if(dist.distance(newx, newy)<=blastRadius)
		{dist.isDestroyed();}
	}
	
	boolean isDestroyed() {
		return true;
	}
	int getX() {
		return x;		
	}
	int getY() {
		return y;
	}

}

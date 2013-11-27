package ui.utilities;

import java.io.Serializable;

public class Point implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9207777051398190205L;
	//Possibly need here some sort of global/local attributes - for the ability to scale?
	private int xcoord;
	private int ycoord;
	
	public Point(int x, int y) {
		this.xcoord = x;
		this.ycoord = y;
	}
	
	public void setPoint(int x, int y) {
		xcoord = x;
		ycoord = y;
	}
	
	//These two functions can be used for being able to slide a point up and down a line?
	public void setX(int x) {
		xcoord = x;
	}
	
	public void setY(int y) {
		ycoord = y;
	}
	
	public int getX(){
		return xcoord;
	}
	
	public int getY(){
		return ycoord;
	}
	

}

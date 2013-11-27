package ui.utilities;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Region implements Serializable {

	private static final long serialVersionUID = 8155722358749875557L;
	private ArrayList<Point> points;
	private String region_name;
	private Color region_color = new Color(0,0,1,0.3f);
	
	public Region(String name, ArrayList<Point> points) {
		this.region_name = name;
		this.points = points;
	}
	
	public Region(String name){
		this.region_name = name;
		this.points = new ArrayList<Point>();
	}
	
	public void replacePoint(int index, Point replacePoint) {
		points.set(index, replacePoint);
	}
	
	public Point findPoint(int index) {
		return points.get(index);
	}
	
	public String getName() {
		return region_name;
	}
	
	public void setName(String name){
		region_name = name;
	}
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	public Boolean deletePoint(Point p){
		for(Point pi : points){
			if (nearPoint(pi, p)){
				points.remove(pi);
				return true;
			}
		}
		return false;
	}
	
	public Boolean nearPoint(Point a, Point b){
		if (Math.abs(a.getX()- b.getX()) < 6 && Math.abs(a.getY()- b.getY()) < 6){
			System.out.println("Near Point");
			return true;
		} else {
			return false;
		}
	}

	public Color getRegionColor() {
		return region_color;
	}

	public void setRegionColor(Color region_color) {
		this.region_color = region_color;
	}
	
	
}

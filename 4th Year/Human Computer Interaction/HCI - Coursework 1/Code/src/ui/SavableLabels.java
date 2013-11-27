package ui;

import java.util.ArrayList;
import java.io.Serializable;

public class SavableLabels implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Region> savedRegions = new ArrayList<Region>();
	private String location = "";
	
	public SavableLabels(ArrayList<Region> regionArray, String fileLocation) {
		//TODO: Change this to be a checksum
		location = fileLocation;
		savedRegions = regionArray;
	}
	
	public String getlocation() {
		return location;
	}
	
	public ArrayList<Region> getRegions() {
		return savedRegions;
	}
}

package ui;

import java.awt.Image;
import java.util.ArrayList;

public class LabelledImage {
	private Image image;
	private ArrayList<Region> regions;
	private String filelocation;
	private int SelectedRegionIndex = -1;

		public LabelledImage(Image newimage, ArrayList<Region> regions, String location) {
			image = newimage;
			filelocation = location;
			this.regions = regions;
		}
		
		public LabelledImage(Image newimage, String location) {
			image = newimage;
			this.regions = new ArrayList<Region>();
			filelocation = location;
		}
		
		public Image getImage() {
			return image;
		}
		
		public ArrayList<Region> getRegions() {
			return regions;
		}
		
		public void addRegion(Region reg){
			regions.add(reg);
		}	
		
		
		public Region getRegion(int index) {
			return regions.get(index);
		}
		
		public Region getRegion(String regionName){
			for (Region reg: regions){
				if (reg.getName().equals(regionName)){
					return reg;
				}
			}
			return null;
		}
		
		public void deleteRegion(String regName){
			for (Region reg: regions){
				if (reg.getName().equals(regName)){
					regions.remove(reg);
					break;
				}
			}
		}		
		
		public void deleteRegion(int i){
			regions.remove(i);
		}

		public String getLocation() {
			// TODO Auto-generated method stub
			return filelocation;
		}

		public int getSRegionIndex() {
			return SelectedRegionIndex;
		}

		public void setSRegionIndex(int selectedRegionIndex) {
			SelectedRegionIndex = selectedRegionIndex;
		}
		
		public void setSRegionIndex(String RegionName){
			int temp = -1;
			for (int i = 0; i < regions.size(); i++){
				if(regions.get(i).getName().equals(RegionName)){
					temp = i;
					break;
				}
			}
			
			SelectedRegionIndex = temp;
		}

		
		
}

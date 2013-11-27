package ui;

import java.util.ArrayList;


public class GalleryModel {
	private ArrayList<LabelledImage> workspaceimages;
	
	GalleryModel(){
		
	}
	
	public void setGalleryImages(ArrayList<LabelledImage> imageList) {
		workspaceimages = imageList;
	}
	
	public ArrayList<LabelledImage> getGalleryImages() {
		return workspaceimages;
	}
}

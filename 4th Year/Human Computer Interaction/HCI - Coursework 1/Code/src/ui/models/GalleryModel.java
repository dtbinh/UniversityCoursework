package ui.models;

import java.util.ArrayList;

import ui.utilities.LabelledImage;


public class GalleryModel {
	private ArrayList<LabelledImage> workspaceimages;
	
	public GalleryModel(){
		
	}
	
	public void setGalleryImages(ArrayList<LabelledImage> imageList) {
		workspaceimages = imageList;
	}
	
	public ArrayList<LabelledImage> getGalleryImages() {
		return workspaceimages;
	}
}

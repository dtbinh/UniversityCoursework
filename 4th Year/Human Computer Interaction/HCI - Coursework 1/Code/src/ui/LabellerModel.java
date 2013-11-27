package ui;

import java.awt.Image;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class LabellerModel {
	
	private String set_dir;
	private final String default_image = "images/testTutImage.jpg";	
	private final String png_test_image = "images/pngtest.png";


	LabellerModel() {
		reset();
	}
	
	public void reset() {
		
	}
	
	public void setDirectory(String directory){
		set_dir = directory;
	}

	public String getDirectory() {
		return set_dir;
	}
	
	public String DefaultImageString(){
		return default_image;
	}
	
	
}

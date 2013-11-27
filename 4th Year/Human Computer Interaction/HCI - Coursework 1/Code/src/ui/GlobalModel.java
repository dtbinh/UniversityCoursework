package ui;

public class GlobalModel {
	
	private String working_dir = null;
	private String image_dir = null;

	private String data_dir = null;
	private final String default_image = "images/testTutImage.jpg";	

	public void setWorkingDirectory(String directory){
		working_dir = directory;
	}

	public String getWorkingDirectory() {
		return working_dir;
	}
	
	public String DefaultImageString(){
		return default_image;
	}
	
	public String getImageDirectory() {
		return image_dir;
	}

	public void setImageDirectory(String image_dir) {
		this.image_dir = image_dir;
	}

	public String getDataDirectory() {
		return data_dir;
	}

	public void setDataDirectory(String data_dir) {
		this.data_dir = data_dir;
	}
	
	
}

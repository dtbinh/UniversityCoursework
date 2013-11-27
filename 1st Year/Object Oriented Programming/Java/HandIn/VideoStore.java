import java.util.ArrayList;

public class VideoStore {
	// Instance Variables
	ArrayList<Video> videostore = new ArrayList<Video>(10);

	// Methods
	public void addVideo(String title) {
		if(videostore.size()<10) {
			Video newvideo = new Video();
			newvideo.setTitle(title);
			videostore.add(newvideo);
		}
		else {
			System.out.println("I'm sorry. The shelves are currently full.");
		}
	}

	public int getVideoByTitle(String title) {
		int hold = -1;
		for(int i = 0; i<videostore.size(); i++) {
		if(title.equals((videostore.get(i)).getTitle())) {
			hold=i;
		}
		
		}
		return hold;	
	}

	public void checkoutVideo(int video) {
		if(video>=0) {
			(videostore.get(video)).checkout();
		}

	}

	public void returnVideo(int video) {
		if(video>=0) {
		(videostore.get(video)).returnToStore();
		}
	}

	public void rateVideo(int video, int rating) {
		(videostore.get(video)).addRating(rating);
	}

	public int getRatingForVideo(int video) {
		int hold;
		hold = (videostore.get(video)).getRating();
		return hold;

	}

	public void listInventory() {
		boolean avail;
		String show;
		for(int i=0; i<(videostore.size()); i++) {
			avail = videostore.get(i).isCheckedOut();
			if(avail) {
				show = "Available";}
			else {
				show = "Checked Out";
			}
		System.out.println("Name: "+(videostore.get(i).getTitle())+"\tRating: "+(videostore.get(i).getRating())+"\tStatus: "+show);
		}

	}
}

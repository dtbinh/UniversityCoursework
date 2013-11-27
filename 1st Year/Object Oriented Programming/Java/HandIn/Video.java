import java.util.ArrayList;

public class Video {
	// Instance Variables
	String moviename;
	ArrayList<String> array = new ArrayList<String>();
	int averageratings = 0;
	boolean checkedout;

	// Methods
	public void setTitle(String name) {
		moviename = name;
	}

	// ==========

	public String getTitle() {
		return moviename;
	}

	// ==========

	public void addRating(int rating) {
		
		String holder = String.valueOf(rating);
		array.add(holder);
		int total = 0;
		
		for (int i = 0; i < array.size(); i++) {
			total += Integer.parseInt(array.get(i));
		}
		
		averageratings = total / (array.size());

	}
	
	public int getRating() {
		return averageratings;
	}

	// ==========

	public void checkout() {
		checkedout = true;
	}

	// ==========

	public void returnToStore() {
		checkedout = false;
	}

	// ==========

	public boolean isCheckedOut() {
		return checkedout;
	}

}

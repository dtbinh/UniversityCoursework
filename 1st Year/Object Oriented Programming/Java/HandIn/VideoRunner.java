
public class VideoRunner {
	
	public static void main(String[] args) {
		VideoStore store = new VideoStore();
		store.addVideo("Hot Fuzz");
		store.checkoutVideo(store.getVideoByTitle("Hot Fuzz"));
		store.listInventory();
		System.out.println();
		store.addVideo("Anchorman");
		store.rateVideo(store.getVideoByTitle("Anchorman"), 10);
		store.rateVideo(store.getVideoByTitle("Anchorman"), 2);
		store.rateVideo(store.getVideoByTitle("Anchorman"), 5);
		store.rateVideo(store.getVideoByTitle("Anchorman"), 8);
		store.addVideo("Slumdog Millionaire");
		store.listInventory();
		System.out.println();
		store.returnVideo(store.getVideoByTitle("Hot Fuzz"));
		store.checkoutVideo(store.getVideoByTitle("SLumDog mIllionaire"));
		store.listInventory();
		System.out.println();
		System.out.println();
		store.checkoutVideo(store.getVideoByTitle("Slumdog Millionaire"));
		store.addVideo("Harry Potter");
		store.rateVideo(store.getVideoByTitle("Harry Potter"), 2);
		store.rateVideo(store.getVideoByTitle("Harry Potter"), 5);
		store.rateVideo(store.getVideoByTitle("Harry Potter"), 3);
		store.rateVideo(store.getVideoByTitle("Harry Potter"), 8);
		store.rateVideo(store.getVideoByTitle("Hot Fuzz"), 10);
		store.rateVideo(store.getVideoByTitle("Hot Fuzz"), 8);
		store.rateVideo(store.getVideoByTitle("Hot Fuzz"), 9);
		store.rateVideo(store.getVideoByTitle("Hot Fuzz"), 10);
		store.rateVideo(store.getVideoByTitle("Hot Fuzz"), 7);
		store.rateVideo(store.getVideoByTitle("Hot Fuzz"), 6);
		store.rateVideo(store.getVideoByTitle("Slumdog Millionaire"), 7);
		store.rateVideo(store.getVideoByTitle("Slumdog Millionaire"), 8);
		store.listInventory();
		System.out.println();
	}

}

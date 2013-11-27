package ui.controllers;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.border.BevelBorder;

import javax.swing.event.MouseInputAdapter;

import com.sun.image.codec.jpeg.*; 

import org.apache.commons.io.FileUtils;

import ui.models.ActiveImageModel;
import ui.models.GalleryModel;
import ui.models.GlobalModel;
import ui.utilities.DeleteMode;
import ui.utilities.LabelledImage;
import ui.utilities.Point;
import ui.utilities.PointEditState;
import ui.utilities.Region;
import ui.utilities.SavableLabels;
import ui.views.EditorView;
import ui.views.GalleryItemView;
import ui.views.GalleryView;
import ui.views.ImageView;
import ui.views.LabellerView;

public class LabellerController {
	//private String default_dir;
	
	private GlobalModel global_model;
	private ActiveImageModel a_model;
	private GalleryModel g_model;
	
	private LabellerView l_view;
	private GalleryView g_view;
	private EditorView e_view;
	
	private JFileChooser fc;
	
	private boolean clicked = false;
	
	public void startLabeller(){
		global_model = new GlobalModel();
		a_model = new ActiveImageModel();
		g_model = new GalleryModel();
		
		//h_view = new HomeView();
		g_view = new GalleryView();
		e_view = new EditorView();
		l_view = new LabellerView(g_view,e_view);
		l_view.setVisible(true);
		fc = new JFileChooser();

		e_view.addChooseListener(new FileListener());
		e_view.addSaveListener(new SaveListener());
		l_view.addWindowListener(new CloseListener());
		e_view.addMouseInputAdapter(new MTracker());
		e_view.addCStateListener(new CStateListener());
		e_view.addEStateListener(new EStateListener());
		e_view.addDStateListener(new DStateListener());
		e_view.addDNOWListener(new DeleteNowListener());
		e_view.addDLATERListener(new AskDeleteListener());
		e_view.addColorListener(new ColorListener());
		
		e_view.setFocusable(true);
		e_view.requestFocusInWindow();
		e_view.addKeyListener(new EditorKeyListener());
		
		
		String workingDir = "/";
		global_model.setWorkingDirectory(workingDir);
		
		String imageDir = "images/";
		global_model.setImageDirectory(imageDir);
		
		String dataDir = "hci/";
		global_model.setDataDirectory(dataDir);
		e_view.setNotification("Labeller Loaded", Color.green);
		
		//File folder = new File("hci");
		//if(!(folder.exists())) folder.mkdir();
		
		/*
		 * For future, user args
		 */
		createDirectory(global_model.getImageDirectory());
		createDirectory(global_model.getDataDirectory());
		addtoGallery();
		modelAdd();
		loadImage(g_model.getGalleryImages().get(0).getLocation());
		
		e_view.revalidate();
	}
	/*
	public void importTestImage(String imageFileName){
		BufferedImage image = null;
		Image scaledImage =null;
		try {
			image = ImageIO.read(new File(imageFileName));
			System.out.println(new File(imageFileName).getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (image.getWidth() > 800 || image.getHeight() > 600) {
			int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
			//System.out.println("SCALING TO " + newWidth + "x" + newHeight );
			scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			image.createGraphics();
			//image.getGraphics().drawImage(scaledImage,0,0, this);
		}

		LabelledImage l_image = new LabelledImage(scaledImage, imageFileName);
		a_model.setImage(l_image);
		e_view.setModel(a_model);

		if(l_image != null) {
			a_model.setImage(l_image);
			e_view.setModel(a_model);
		}

		
	}	
	
	public void importTestImage(File imageFile){
		BufferedImage image = null;
		Image scaledImage =null;
		try {
			image = ImageIO.read(imageFile);
			System.out.println(imageFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (image.getWidth() > 800 || image.getHeight() > 600) {
			int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
			//System.out.println("SCALING TO " + newWidth + "x" + newHeight );
			scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			

		}

		LabelledImage l_image = null;
		try {
			l_image = new LabelledImage(scaledImage, imageFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(l_image != null) {
			a_model.setImage(l_image);
			e_view.setModel(a_model);
		}
		
	}
	*/
	
	public void loadImage(File imageFile) {
		try {
			loadImage(imageFile.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Loads an image based of its location
	public void loadImage(String imageLocation){
		//System.out.println(imageLocation);
		clicked = false;
		BufferedImage image = null;
		Image scaledImage =null;
		try {
			image = ImageIO.read(new File(imageLocation));
			double multiplier = 1.0;;
			double wmultiplier = 1.0;
			double hmultiplier = 1.0;
			if (image.getWidth() > 800){
				wmultiplier = 800 / (double) image.getWidth();
			} 
			if (image.getHeight() > 600) {
				hmultiplier = 600 / (double) image.getHeight();
			}
			multiplier = Math.min(wmultiplier, hmultiplier);
			System.out.println(multiplier);
			if (image.getWidth() > 800 || image.getHeight() > 600) {
				int newWidth = (int) (image.getWidth() * multiplier);
				int newHeight =(int) (image.getHeight() * multiplier); 
				scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			} else {
				scaledImage = image;
			}
			/*
			if (image.getWidth() > 800 || image.getHeight() > 600) {
				int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
				int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
				scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			} else {
				scaledImage = image;
			}*/
	
			 		
			String[] extensions = new String[] { "hci" };
			List<File> hciFiles = (List<File>) FileUtils.listFiles(new File("hci"), extensions, true);
			String checksum = createChecksumName(getChecksum(imageLocation));
			boolean match = checkSaveFiles(hciFiles, checksum);
			
			
			LabelledImage l_image = null;
			if(match) {
				SavableLabels loaded = loadLabels("hci/"+checksum+".hci");
				if(loaded != null && loaded.getRegions() != null) 
					l_image = new LabelledImage(scaledImage, loaded.getRegions(), imageLocation);
				else 
					l_image = new LabelledImage(scaledImage, imageLocation);
				
			} else {
				l_image = new LabelledImage(scaledImage, imageLocation);
				
			}
			
			if(l_image != null) {
				//System.out.println(imageLocation);
				if(!checkUsedImage(imageLocation)){
					copyImage(l_image);
					e_view.clearImagePanel();
					addtoGallery();
					modelAdd();
				}
				a_model.setImage(l_image);
				a_model.clearActivePoints();
				
				Dimension size = new Dimension(l_image.getImage().getWidth(null),l_image.getImage().getHeight(null));
				e_view.setImageDim(size);
				e_view.setModel(a_model);
				e_view.setNotification("Image Loaded. Click on image to start labelling", Color.green);
				displayLabels();
				e_view.revalidate();
				e_view.cleanLabelPanel();
				if(match) {
					
					e_view.repaintImage();
				}
			}
		
		} catch (IOException e) {
			l_view.showError("Cannot open file. Aborting\n");
			e_view.setNotification("Cannot open file. Aborting\n", Color.red);
			e_view.clearImagePanel();
			displayLabels();
			e_view.revalidate();
			addtoGallery();
			modelAdd();
			e.printStackTrace();
		}
		
	}
	


	private boolean checkUsedImage(String imageLocation){
		int index = imageLocation.lastIndexOf(File.separator);
		String name = imageLocation.substring(0, index);
		
		File imagefolder = new File(global_model.getImageDirectory());
		File testfolder = new File(name);
		
		String imagefolderS = null, testfolderS = null;
		try {
			testfolderS = testfolder.getCanonicalPath();
			imagefolderS = imagefolder.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(imagefolderS != null&& testfolderS != null){
			return testfolderS.equals(imagefolderS);
		}
		return false;
	}
	
	//Checks if there are any save files (currently only used 
	private boolean checkSaveFiles(List<File> hciFiles, String checksum) {
		for (File f : hciFiles) {
			int index = f.getName().lastIndexOf('.');
			String name = f.getName().substring(0, index);

			if(checksum.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	//Function checks if there are changes to the image and asks for a save before quitting
	private void detectChangesAndQuit() {
		LabelledImage current = a_model.getCurrentImage();
		e_view.setNotification("Closing Application", Color.yellow);
		if(clicked) {
			
			switch(l_view.saveQuery()) {
			case 0: 
				e_view.setNotification("Saving Labels", Color.yellow);
				saveLabels(current);
				l_view.dispose();
				System.exit(0);
				break;
			case 1:
				l_view.dispose();
				System.exit(0);
				break;
			case 2:
				break;
			default:
				break;
			}
		} else {
			l_view.dispose();
			System.exit(0);
		}
	}
	
	//Function checks if there are changes when loading another image, and allows user to save
	private void detectChangesAndSave(File load) {
		try {
			detectChangesAndSave(load.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Function checks if there are changes when loading another image, and allows user to save
	//TODO: Depending on implementation, may need commented out lines
	private void detectChangesAndSave(String load) {
		LabelledImage current = a_model.getCurrentImage();
		if(clicked) {
			switch(l_view.saveQuery()) {
			case 0: 
				e_view.setNotification("Saving Labels", Color.yellow);
				saveLabels(current);
				loadImage(load);
				break;
			case 1:
				loadImage(load);
				break;
			case 2:
				break;
			default:
				break;
			}
		} else {
			loadImage(load);
		}
		
		
	}
	
	//Creates gallery dicectory to store images
	private void createDirectory(String workspacepath) {
		try{
			File workspace = new File(workspacepath);
			if(!workspace.exists())  workspace.mkdir();
		} catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	//Adds images to the gallery scroll
	private void modelAdd() {
		for(LabelledImage lImage : g_model.getGalleryImages()) {
				GalleryItemView temp = new GalleryItemView(lImage);
				temp.addClickListener(new GalleryPanelListener());
				e_view.addImagePanel(temp);
		}
	}
	
	//Recursively finds all images within directory and adds them to gallery model
	private void addtoGallery() {
		ArrayList<LabelledImage> allImages = new ArrayList<LabelledImage>();
		//Gets list of file images
		File dir = new File(global_model.getImageDirectory());
				
		String[] extensions = new String[] { "jpg", "png", "gif" };
		List<File> imageFiles = (List<File>) FileUtils.listFiles(dir, extensions, true);
			
		for (File file : imageFiles) {
			System.out.println(file.getName());
			Image image;
			try {
				if(file.getCanonicalPath().endsWith(".jpg") || file.getCanonicalPath().endsWith(".jpeg")) {
					InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
				    image = JPEGCodec.createJPEGDecoder(inputStream).decodeAsBufferedImage();
				} else {
					image = ImageIO.read(file);					
				}				
				
				LabelledImage l_image = new LabelledImage(image.getScaledInstance(100, 100, Image.SCALE_DEFAULT), file.getCanonicalPath());
				allImages.add(l_image);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		e_view.setNotification("Images Loaded to Gallery", Color.yellow);
		g_model.setGalleryImages(allImages);
		
		
	}
	
	private void copyImage(LabelledImage l_image) {
		String imageLocation = l_image.getLocation();
			int index = imageLocation.lastIndexOf('.');
			String ext = imageLocation.substring(index+1, imageLocation.length());
			//System.out.println(ext);
			
		String outputLoc = global_model.getImageDirectory();
		index = imageLocation.lastIndexOf('\\');
		String name = imageLocation.substring(index+1, imageLocation.length());
		//System.out.println(name);
			
		BufferedImage bi = new BufferedImage(l_image.getImage().getWidth(null),l_image.getImage().getHeight(null),BufferedImage.TYPE_INT_ARGB);
		bi.createGraphics();
		Graphics g = bi.getGraphics();
		g.drawImage(l_image.getImage(),0,0,null);
		try {
			ImageIO.write(bi, ext, new File(outputLoc+name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	//Creates a 'savable' object from a .hci file, which is used to create LabelledImage
	//TODO: If savable file is created from an object that doesn't exist, an error message needs to arise
	private SavableLabels loadLabels(String filename) {
		SavableLabels regions = null;
		try {
		    // Deserialize from a file
		    File file = new File(filename);
		    ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		    // Deserialize the object
		    regions = (SavableLabels) in.readObject();
		    in.close();
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}
		
		return regions;
	}
	
	//Writes a new .hci file to memory for saving
	private void saveLabels(LabelledImage limage) {
		SavableLabels save = new SavableLabels(limage.getRegions(), limage.getLocation());
		try {
			String filename = createChecksumName(getChecksum(limage.getLocation()));
			File folder = new File("hci");
			if(!(folder.exists())) folder.mkdir();
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(folder+"/"+filename+".hci"));
			out.writeObject(save);
		    out.close();
		    e_view.setNotification("Labels Saved", Color.green);
		} catch (IOException e) {
			e.printStackTrace();
		}    
	}
	
	//Creates a byte checksum based off the image 
	private byte[] getChecksum(String filelocation) {
		File file = new File(filelocation);
		byte[] bytes = null;
		try {
			FileInputStream fis = new FileInputStream(file);
        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	byte[] buf = new byte[1024];
        	for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); 
            }
        	fis.close();
        	bytes = bos.toByteArray();
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
		
		return bytes;
        
	}
	
	//Creates a name based off the byte checksum
	private String createChecksumName(byte[] bytes) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			MessageDigest tc1 = (MessageDigest) md.clone();
			byte[] mdbytes = tc1.digest();
			StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < mdbytes.length; i++) {
	          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        return sb.toString();
		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		 return "";
	}
	
	private void displayLabels() {
		e_view.clearLabelPanel();
		for(int i = 0; i < a_model.getCurrentImage().getRegions().size(); i++){
			Region reg = a_model.getCurrentImage().getRegion(i);
			JButton button = new JButton(reg.getName());
			button.addMouseListener(new LabelListener());
			if (i != a_model.getCurrentImage().getSRegionIndex()){
				button.setBackground(Color.GRAY);
			}else {
				button.setBackground(Color.LIGHT_GRAY);
			}
			
			
			button.setForeground(Color.WHITE);
			button.setAlignmentX(Component.CENTER_ALIGNMENT);
			//button.setBorder(new LineBorder(new Color(1, 1, 1)));
			button.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
			e_view.displayRegionBtn(button);
		}
	}
	
	class EditorKeyListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {

			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {

			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			

			
			if ((int)arg0.getKeyChar() == KeyEvent.VK_DELETE) {
				if (a_model.getCurrentImage().getSRegionIndex() == -1){
					return;
				}
				Region reg = a_model.getCurrentImage().getRegion(a_model.getCurrentImage().getSRegionIndex());
				String regionName = reg.getName();
				
				switch (a_model.getD_state()) {
				case DELETE_NOW:
					a_model.getCurrentImage().deleteRegion(
							a_model.getCurrentImage().getSRegionIndex());
					e_view.setNotification("Deleted Label: " + regionName, Color.yellow);
					displayLabels();
					e_view.revalidate();
					clicked= true;
					break;
				case ASK_DELETE:
					
					if (l_view.deleteQuery(regionName) == 0) {
						a_model.getCurrentImage().deleteRegion(
								a_model.getCurrentImage().getSRegionIndex());
						e_view.setNotification("Deleted Label: " + regionName, Color.yellow);
						displayLabels();
						e_view.revalidate();
						clicked= true;
						
					}else {
						e_view.setNotification("Cancelled Deletion", Color.yellow);
					}
					break;
				}
				e_view.cleanLabelPanel();
			}
			
			if ((int)arg0.getKeyChar() == KeyEvent.VK_ENTER) {
				if ( a_model.getActivePoints().size() >2){
					int count = a_model.getCurrentImage().getRegions().size();
					//a_model.editActivePoint(a_model.getActivePoints().size()-1, a_model.getActivePoints().get(0));
					//System.out.println("Count:"+ a_model.getActivePoints().size());
					String name = l_view.nameQuery("");
					if (name != null && name.length() != 0){
						a_model.closeActivePoints(name);
						e_view.setNotification("Created Label: " + name, Color.green);
					} else {
						a_model.closeActivePoints("Label " + count);
						e_view.setNotification("Created Label: " + count, Color.green);
					}
					
				}
				e_view.setModel(a_model);
				displayLabels();
			}
			if((int)arg0.getKeyChar() == KeyEvent.VK_ESCAPE){
				int index =a_model.getCurrentImage().getSRegionIndex();
				if (index != -1){
					e_view.setNotification("Deselected active Label", Color.green);
				}
				a_model.getCurrentImage().setSRegionIndex(-1);
				int hadPoints;
				if (a_model.getActivePoints() != null)
					hadPoints = a_model.getActivePoints().size();
				else 
					hadPoints = 0;
				
				a_model.clearActivePoints();
				if (hadPoints > 0){
					e_view.setNotification("Active Label Canceled" , Color.yellow);
				} else {
					
				}
				
				e_view.setModel(a_model);
				e_view.repaintImage();
				displayLabels();
				e_view.cleanLabelPanel();
			}
			
		}
		
	}
	
	class ColorListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Color c = l_view.colorQuery(new Color(0,0,1,0.4f));
			float[] temp = c.getComponents(new float[4]);
			Color newColor = new Color(temp[0], temp[1], temp[2], 0.4f);
			a_model.setEditorColor(newColor);
			e_view.setNotification("New Color Set ", Color.green);
			e_view.setModel(a_model);
			e_view.revalidate();
			e_view.setFocusable(true);
			e_view.requestFocusInWindow();
		}
		
	}
	
	class LabelListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			JButton btn = (JButton) arg0.getSource();
			String regionName = btn.getText();
			
			if(arg0.getClickCount() >= 1){
				
				a_model.getCurrentImage().setSRegionIndex(regionName);
				e_view.setNotification("Label: " + regionName + " Selected. Press Delete to remove. Double Click to Rename", Color.yellow);

			}
			if(arg0.getClickCount() >= 2) {
				
				//Region reg = a_model.getCurrentImage().getRegion(regionName);
				String name = l_view.nameQuery(regionName);
				a_model.getCurrentImage().getRegion(regionName).setName(name);
				e_view.setNotification("Label: " + regionName + " Renamed to: " + name, Color.green);

			}
			displayLabels();
			e_view.setModel(a_model);
			e_view.repaintImage();
			e_view.revalidate();

			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class DeleteNowListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			
			a_model.setD_state(DeleteMode.DELETE_NOW);
			e_view.setNotification("Warning: Labels will be deleted right away if delete is pressed", Color.yellow);
			
			
		}
	}
	
	class AskDeleteListener implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			
			a_model.setD_state(DeleteMode.ASK_DELETE);
			e_view.setNotification("You will be asked before Labels are deleted", Color.yellow);
			
		}
	}
	
	
	class CStateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
	
			a_model.setEditState(PointEditState.CREATE);
			e_view.setNotification("Point Creation mode set. Click on image to create new points. Press Enter to close or click first point again.", Color.green);
		}
		
	}
	
	class EStateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			a_model.setEditState(PointEditState.EDIT);
			if (a_model.getActivePoints() != null && a_model.getActivePoints().size() != 0 ){
				String name = l_view.nameQuery(null, "Unsaved points must be saved before editing. \n Points will be deleted if canceled");

				if (name != null && name.length() != 0){
					a_model.closeActivePoints(name);
				} else {
					a_model.clearActivePoints();
				}
				
			}
			e_view.setNotification("Point Editing mode set. Click and drag points to move them", Color.green);
			e_view.setModel(a_model);
		}
		
	}
	
	class DStateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			a_model.setEditState(PointEditState.DELETE);
			e_view.setNotification("Point Deletion mode set. Click points to delete them", Color.green);
		}
		
	}
	
	class MTracker extends MouseInputAdapter {
		public void mousePressed(MouseEvent arg0){
			clicked = true;
			int x = arg0.getX();
			int y = arg0.getY();
			
			ImageView iv = (ImageView)arg0.getSource();
		switch(a_model.getEditState()){
		case CREATE:
		
			

			/*
			if (x > iv.getWidth() || y > iv.getHeight()){
				System.out.println("Out of bounds");
			}else {
				System.out.println("Press: X: "+ x + " Y: " + y);
			}*/
			
			if (a_model.getActivePoints() == null){
				Point p = new Point(x,y);
				a_model.openActivePoints();
				a_model.addActivePoint(p);
				e_view.setNotification("Creating new Label. Labels must have 3 or more Points. Press Enter to Save. Press Escape to Cancel" , Color.green);
				
			} else {
				Point p = new Point(x,y);
				a_model.addActivePoint(p);
			}
			
			e_view.setModel(a_model);
			displayLabels();
			break;
		case EDIT:
			Point p = boundedPoint(iv,x,y);
			a_model.setEditPoint(p);
			
			break;
		case DELETE:
			break;
		}
		}
		
		public void mouseDragged(MouseEvent arg0){
		switch(a_model.getEditState()){
			case CREATE:
			updatePoint(arg0, false);
			break;
		case EDIT:
			globalUpdatePoint(arg0, false);
			break;
		case DELETE:
			break;
		}
		}
		
		public void mouseReleased(MouseEvent arg0){
		switch(a_model.getEditState()){
			case CREATE:
			updatePoint(arg0, true);
			break;
		case EDIT:
			globalUpdatePoint(arg0, true);
			break;

		case DELETE:
			
			int x = arg0.getX();
			int y = arg0.getY();
			ImageView iv = (ImageView)arg0.getSource();
			//System.out.println(" X: "+ x + " Y: " + y);
			Point p = boundedPoint(iv,x,y);
			a_model.deleteClickedPoint(p);
			clicked = true;
			a_model.cleanRegions();
			
			e_view.setModel(a_model);
			displayLabels();
			e_view.revalidate();
			e_view.cleanLabelPanel();
			break;
		}
			
		}
		
		public void globalUpdatePoint(MouseEvent arg0, Boolean release){
			int x = arg0.getX();
			int y = arg0.getY();
			
			ImageView iv = (ImageView)arg0.getSource();
			/*
			if (x > iv.getWidth() || y > iv.getHeight()){
				System.out.println("Out of bounds");
			}else {
				System.out.println(" X: "+ x + " Y: " + y);
			}*/
			
			
			Point p = boundedPoint(iv,x,y);
			a_model.editSavedPoint(p);
			//a_model.editActivePoint( p);
			if (release){
				//e_view.setNotification("Edited Point" , Color.green);
				a_model.finishSavedPointEdit(p);
			}
			e_view.setModel(a_model);
			displayLabels();
		}
		
		public void updatePoint(MouseEvent arg0, Boolean release){
			int x = arg0.getX();
			int y = arg0.getY();
			
			ImageView iv = (ImageView)arg0.getSource();
			/*
			if (x > iv.getWidth() || y > iv.getHeight()){
				System.out.println("Out of bounds");
			}else {
				System.out.println(" X: "+ x + " Y: " + y);
			}*/
			
			Point p = boundedPoint(iv,x,y);
			a_model.editActivePoint( p);
			
			if (release){
				if (a_model.nearPoint(a_model.getActivePoints().get(0), p) && a_model.getActivePoints().size() >2){
					int count = a_model.getCurrentImage().getRegions().size();
					//activePoints.set(activePoints.size()-1, activePoints.get(0));
					a_model.editActivePoint(a_model.getActivePoints().size()-1, a_model.getActivePoints().get(0));
					String name = l_view.nameQuery("");
					if (name != null && name.length() != 0){
						a_model.closeActivePoints(name);
						e_view.setNotification("Label: " + name + " saved", Color.green);
					} else {
						a_model.closeActivePoints("Label " + count);
						e_view.setNotification("Label: " + count + " saved", Color.green);
					}
				}
				
			}
			e_view.setModel(a_model);
			displayLabels();
		}
		
		Point boundedPoint(ImageView iv, int x, int y){
			if (x > iv.getWidth()){
				x = iv.getWidth();
			}
			if (x < 0){
				x = 0;
			}
			
			if (y > iv.getHeight()){
				y= iv.getHeight();
			}
			
			if (y < 0){
				y = 0;
			}
			
			return new Point(x,y);
		}
	}	
	
	class SaveListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			
			e_view.setNotification("Saving Image Data", Color.yellow);
			if (a_model.getActivePoints() != null && a_model.getActivePoints().size() != 0 ){
				String name = l_view.nameQuery(null, "Unsaved points must be labelled before saving. \n Points will be deleted if canceled");

				if (name != null && name.length() != 0){
					a_model.closeActivePoints(name);
				} else {
					a_model.clearActivePoints();
				}
				displayLabels();
				e_view.setModel(a_model);
				e_view.revalidate();
			}
			
			saveLabels(a_model.getCurrentImage());
			
			clicked = false;
			e_view.setNotification("Image Saved", Color.green);
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//Add way to reuse in gallery, deal with file types. 
	class FileListener implements MouseListener {


		@Override
		public void mouseClicked(MouseEvent arg0) {
			e_view.setNotification("Choose a new image (jpg, gif or png)", Color.yellow);
			int returnVal = fc.showOpenDialog(e_view);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				detectChangesAndSave(file);
				//e_view.setNotification("Image Opened", Color.green);
			}else {
				e_view.setNotification("Opening Canceled, last image restored", Color.green);
			}
			
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {

			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {

			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
		}
		
	}
	
	class GalleryPanelListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			GalleryItemView image = (GalleryItemView)e.getSource();	
			if(e.getClickCount() >= 2) {
				e_view.setNotification("Changing images...", Color.yellow);
				detectChangesAndSave(image.location);
				
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {

			
		}

		@Override
		public void mouseExited(MouseEvent e) {

			
		}

		@Override
		public void mousePressed(MouseEvent e) {

			
		}

		@Override
		public void mouseReleased(MouseEvent e) {			
		}
		
	}
	
	class CloseListener implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {

			detectChangesAndQuit();
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {		
		}

		@Override
		public void windowIconified(WindowEvent arg0) {			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {			
		}
	}
}

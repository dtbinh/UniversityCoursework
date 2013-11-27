package ui.views;

import java.awt.*;
import javax.swing.*;


public class LabellerView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LabellerView(GalleryView gallery_view, EditorView editor_view){
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JPanel content = new JPanel();

		this.setContentPane(content);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.setMaximizedBounds(ge.getMaximumWindowBounds());
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		//System.out.println("Width: " + ge.getMaximumWindowBounds().width+ " Height: " +ge.getMaximumWindowBounds().height);
		content.setLayout(new GridLayout(1, 1, 0, 0));
		
		//JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		//HomeView home_panel = home_view;
		//tabbedPane.addTab("Home", null, home_panel, null);
		
		//GalleryView gallery_panel = gallery_view;
		//tabbedPane.addTab("Gallery", null, gallery_panel, null);

		EditorView editor_panel = editor_view;
		//tabbedPane.addTab("Editor ", null, editor_panel, null);

		//content.add(tabbedPane);
		content.add(editor_panel);
		this.pack();
		
		this.setTitle("Image Labeller");

	}
	
	void init() {
		
	}
	
	
	void reset() {
		
	}
	
	public void showError(String errMessage) {
		JOptionPane.showMessageDialog(this, errMessage);
	}
	
	public int saveQuery() {
		//Custom button text
		Object[] options = {"Save",
		                    "Don't Save",
		                    "Cancel"};
		int n = JOptionPane.showOptionDialog(this,
		    "All unsaved changes will be lost.\n"
		    + "Do you wish to save?\n", 
		    "Unsaved Changes",
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[2]);
		return n;
	}
	
	public String nameQuery(String inputname) {
		String name = JOptionPane.showInputDialog(this,"Name this label? Name: ",inputname);
		if (name != null && name != "")
			return name;
		else 
			return inputname;
	}
	
	public String nameQuery(String inputname, String warning) {
		String name = JOptionPane.showInputDialog(this,warning +"\n" +"Name this label? Name: ",inputname);
		if (name != null && name.length() != 0)
			return name;
		else 
			return inputname;
	}
	
	public int deleteQuery(String regionName){
		
		Object[] options = {"Delete",
                "Don't Delete"};
		int n = JOptionPane.showOptionDialog(this,
		"Do you wish to delete label: \n"
		+ regionName, 
		"Unsaved Changes",
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[1]);
		return n;
	}
	
	public Color colorQuery(Color oldColor){
		Color newColor = JColorChooser.showDialog(this, "Choose Labelling Color", oldColor);
		
		if(newColor != null)
			return newColor;
		else 
			return oldColor;
		
	}
	
	
	

}

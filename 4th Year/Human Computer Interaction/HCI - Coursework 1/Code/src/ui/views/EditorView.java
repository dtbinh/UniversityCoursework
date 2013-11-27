package ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.event.MouseInputAdapter;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

import ui.models.ActiveImageModel;

public class EditorView extends JPanel {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private ImageView image_view;
	
	//private JPanel choose_image;
	//private JPanel save_image;
	private JScrollPane scrollPane;

	private JPanel east_panel;
	private JPanel imagePanel;
	private JPanel labels_panel;


	private JPanel note_panel;


	private JLabel note_label;


	private JLabel north_label;


	private JPanel editing_panel;
	private JButton btnChooseNewImage;
	private JButton btnSaveImage;
	//private JButton btnEditLabel;


	private JScrollPane ls_pane;
	private JRadioButton rdbtnCreateMode;
	private final ButtonGroup point_edit_modes = new ButtonGroup();


	private JRadioButton rdbtnEditMode;


	private JRadioButton rdbtnDeleteMode;
	private JRadioButton delete_mode1;
	private JRadioButton delete_mode2;
	private final ButtonGroup label_delete_modes = new ButtonGroup();
	private JButton color_button;
	private JMenuBar menuBar;
	//private JMenu mnImageFileOptions;
	private JMenu mnPointsEditingModes;
	private JMenu mnLabelsDeleteMode;
	private JPanel color_display;

	public EditorView(){
		setBackground(Color.DARK_GRAY);
		this.setLayout(new BorderLayout(5, 5));
		
		JPanel north_panel = new JPanel();
		north_panel.setBackground(Color.BLACK);
		this.add(north_panel, BorderLayout.NORTH);
		north_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		north_label = new JLabel("Image Path");
		north_label.setAlignmentX(Component.CENTER_ALIGNMENT);
		north_label.setForeground(Color.WHITE);
		north_panel.add(north_label);
		
		JPanel west_panel = new JPanel();
		west_panel.setBackground(Color.GRAY);
		add(west_panel, BorderLayout.WEST);
		
		JLabel west_area = new JLabel("Image Tool Box");
		west_area.setAlignmentX(Component.CENTER_ALIGNMENT);
		west_area.setForeground(Color.WHITE);
		west_panel.setLayout(new BoxLayout(west_panel, BoxLayout.PAGE_AXIS));
		//west_panel.add(west_area);
		
		//testlbbutton = new JButton("TestLabelButton");
		//testlbbutton.addActionListener(new LabellerController.LabelListener());
		/*west_panel.add(testlbbutton);
		testlbbutton.setForeground(Color.WHITE);
		testlbbutton.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		testlbbutton.setBackground(Color.GRAY);
		testlbbutton.setAlignmentX(0.5f);*/
		


		
		Dimension panelSize = new Dimension(800, 600);
		
		JPanel centre_panel = new JPanel();
		add(centre_panel, BorderLayout.CENTER);
		centre_panel.setLayout(new BorderLayout(0, 0));
		
		editing_panel = new JPanel();
		editing_panel.setBackground(Color.WHITE);
		centre_panel.add(editing_panel, BorderLayout.NORTH);
		editing_panel.setLayout(new BoxLayout(editing_panel, BoxLayout.LINE_AXIS));
		
		menuBar = new JMenuBar();
		menuBar.setAlignmentY(Component.CENTER_ALIGNMENT);
		menuBar.setBorderPainted(false);
		menuBar.setBackground(Color.WHITE);
		editing_panel.add(menuBar);
		
		btnChooseNewImage = new JButton(" Choose New Image ");
		menuBar.add(btnChooseNewImage);
		btnChooseNewImage.setBackground(Color.WHITE);
		btnChooseNewImage.setForeground(Color.BLACK);
		btnChooseNewImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnChooseNewImage.setBorder(null);
		
		btnSaveImage = new JButton(" Save Image ");
		menuBar.add(btnSaveImage);
		btnSaveImage.setBackground(Color.WHITE);
		btnSaveImage.setForeground(Color.BLACK);
		btnSaveImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSaveImage.setBorder(null);
		
		//mnImageFileOptions = new JMenu("Image File Options");
		//menuBar.add(mnImageFileOptions);
		
		mnPointsEditingModes = new JMenu("Points Editing Modes");
		menuBar.add(mnPointsEditingModes);
		
		rdbtnCreateMode = new JRadioButton("Create Points");
		mnPointsEditingModes.add(rdbtnCreateMode);
		
				point_edit_modes.add(rdbtnCreateMode);
				
				rdbtnEditMode = new JRadioButton("Edit Points");
				mnPointsEditingModes.add(rdbtnEditMode);
				point_edit_modes.add(rdbtnEditMode);
				
				rdbtnDeleteMode = new JRadioButton("Delete Points");
				mnPointsEditingModes.add(rdbtnDeleteMode);
				point_edit_modes.add(rdbtnDeleteMode);
		
		mnLabelsDeleteMode = new JMenu("Labels Delete Modes");
		mnLabelsDeleteMode.setBorder(null);
		menuBar.add(mnLabelsDeleteMode);
		
		delete_mode1 = new JRadioButton("Delete Labels Now");
		delete_mode1.setBorder(null);
		mnLabelsDeleteMode.add(delete_mode1);
		label_delete_modes.add(delete_mode1);
		
		delete_mode2 = new JRadioButton("Ask Before Delete");
		delete_mode2.setBorder(null);
		mnLabelsDeleteMode.add(delete_mode2);
		label_delete_modes.add(delete_mode2);
	    
	    color_button = new JButton("Choose Color   ");
	    color_button.setSize(new Dimension(120, 0));
	    menuBar.add(color_button);
	    color_button.setForeground(Color.BLACK);
	    color_button.setBorder(null);
	    color_button.setBackground(Color.WHITE);
	    color_button.setAlignmentX(0.5f);
	    
	    color_display = new JPanel();
	    color_display.setMaximumSize(new Dimension(50, 32767));
	    color_display.setBackground(Color.YELLOW);
	    color_display.setPreferredSize(new Dimension(50, 10));
	    editing_panel.add(color_display);

		JPanel outer_image_panel = new JPanel();
		centre_panel.add(outer_image_panel, BorderLayout.CENTER);
		//add(outer_image_panel, BorderLayout.CENTER);
		
		outer_image_panel.setBackground(Color.GRAY);
		GridBagLayout gbl_center_panel = new GridBagLayout();
		gbl_center_panel.columnWeights = new double[]{0.0};
		outer_image_panel.setLayout(gbl_center_panel);

		image_view = new ImageView(panelSize);
		image_view.setBackground(Color.GRAY);
		GridBagConstraints gbc_image_view = new GridBagConstraints();
		gbc_image_view.gridx = 0;
		outer_image_panel.add(image_view, gbc_image_view);

		image_view.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		note_panel = new JPanel();
		note_panel.setBackground(SystemColor.info);
		centre_panel.add(note_panel, BorderLayout.SOUTH);
		
		note_label = new JLabel("Notifications");
		note_label.setForeground(Color.BLACK);
		note_panel.add(note_label);
		
		east_panel = new JPanel();
		east_panel.setBackground(Color.GRAY);
		this.add(east_panel, BorderLayout.EAST);
		east_panel.setLayout(new BoxLayout(east_panel, BoxLayout.PAGE_AXIS));
		
		
		JLabel east_label = new JLabel("Label Tool Box");
		east_label.setAlignmentX(Component.CENTER_ALIGNMENT);
		east_label.setForeground(Color.WHITE);
		east_panel.add(east_label);
		east_label.setHorizontalAlignment(SwingConstants.CENTER);
				
		labels_panel = new JPanel();
		labels_panel.setBorder(null);
		labels_panel.setBackground(Color.GRAY);
		labels_panel.setLayout(new BoxLayout(labels_panel, BoxLayout.PAGE_AXIS));
		
		ls_pane = new JScrollPane(labels_panel);
		ls_pane.setBorder(null);
		ls_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ls_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		ls_pane.setPreferredSize(new Dimension(150,800));
		east_panel.add(ls_pane);
		
		//east_panel.add(labels_panel);
		//lblClickImageTo.setToolTipText("Click to add new object");
		
		/////////////////////
		
		JPanel south_panel = new JPanel();
		south_panel.setBackground(Color.DARK_GRAY);
		add(south_panel, BorderLayout.SOUTH);
		
		 
		imagePanel = new JPanel();
		imagePanel.setBackground(Color.BLACK);
		
		FlowLayout fl_imagePanel = new FlowLayout(FlowLayout.LEFT, 5, 5);
		imagePanel.setLayout(fl_imagePanel);
		
		
		scrollPane = new JScrollPane(imagePanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(700, 130));
		add(scrollPane, BorderLayout.SOUTH);
		
	}
	
	public void addColorListener(ActionListener al){
		color_button.addActionListener(al);
	}
	
	public void addDNOWListener(ActionListener al){
		delete_mode1.addActionListener(al);
	}
	
	public void addDLATERListener(ActionListener al){
		delete_mode2.addActionListener(al);
	}
	
	public void addCStateListener(ActionListener al){
		rdbtnCreateMode.addActionListener(al);
	}
	
	public void addEStateListener(ActionListener al){
		rdbtnEditMode.addActionListener(al);
	}
	
	public void addDStateListener(ActionListener al){
		rdbtnDeleteMode.addActionListener(al);
	}
	
	public void addChooseListener(MouseListener mol){
		btnChooseNewImage.addMouseListener(mol);
	}
	
	public void addSaveListener(MouseListener mol){
		btnSaveImage.addMouseListener(mol);
	}
	
	public void repaintImage() {
		image_view.revalidate();
		image_view.repaint();
	}
	
	public void addImagePanel(GalleryItemView image) {
		imagePanel.add(image);
		this.revalidate();
	}
	
	public void clearLabelPanel(){
		labels_panel.removeAll();
		this.revalidate();
	}
	
	public void cleanLabelPanel(){
		labels_panel.revalidate();
		labels_panel.repaint();
		labels_panel.updateUI();
	}
	

	public void displayRegionBtn(JButton button){
		labels_panel.add(button);
		this.revalidate();
	}

	public void setModel(ActiveImageModel a_model){
		
		
		/*
		for (Region reg : a_model.getCurrentImage().getRegions()){
			//if (!reg.displayed){
				displayRegionBtn(reg);
				//reg.displayed = true;
			//}
		}*/
		switch(a_model.getEditState()){
			case CREATE:
				rdbtnCreateMode.setSelected(true);
				break;
			case EDIT:
				rdbtnEditMode.setSelected(true);
				break;
			case DELETE:
				rdbtnDeleteMode.setSelected(true);
				break;
			default:
				rdbtnCreateMode.setSelected(true);
				break;
		}
		switch(a_model.getD_state()){
		case DELETE_NOW:
			delete_mode1.setSelected(true);
			break;
		case ASK_DELETE:
			delete_mode2.setSelected(true);
			break;
		}
		north_label.setText(a_model.getCurrentImage().getLocation());
		color_display.setBackground(a_model.getEditorColor());
		color_display.revalidate();
		this.revalidate();
		image_view.setModel(a_model, true);
		
	}
	
	public void setImageDim(Dimension size){
		image_view.setDimension(size);
	}
	
	public void setNotification(String note){
		note_label.setText(note);
		this.revalidate();
	}
	
	public void setNotification(String note, Color backColor){
		note_label.setText(note);
		note_panel.setBackground(backColor);
		this.revalidate();
	}
	
	public void setNotification(String note, Color backColor, Color textColor ){
		note_label.setText(note);
		note_panel.setBackground(backColor);
		note_label.setForeground(textColor);
		this.revalidate();
	}

	public void addMouseInputAdapter(MouseInputAdapter mTracker) {
		image_view.addMouseListener(mTracker);
		image_view.addMouseMotionListener(mTracker);
	}
	

	
	public void clearImagePanel() {
		this.remove(scrollPane);
		this.remove(imagePanel);
		imagePanel = new JPanel();
		imagePanel.setBackground(Color.DARK_GRAY);
		
		FlowLayout fl_imagePanel = new FlowLayout(FlowLayout.LEFT, 5, 5);
		imagePanel.setLayout(fl_imagePanel);
		
		scrollPane = new JScrollPane(imagePanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(700, 130));
		add(scrollPane, BorderLayout.SOUTH);
	}
	
	public void removeScrollbar() {
		this.remove(scrollPane);
		this.revalidate();
	}
}

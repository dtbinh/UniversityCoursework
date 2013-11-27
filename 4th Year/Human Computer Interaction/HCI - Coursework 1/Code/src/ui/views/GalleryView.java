package ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class GalleryView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel imagePanel;
	private JScrollPane scrollPane;
	
	public GalleryView(){
		setBackground(Color.DARK_GRAY);
		setLayout(new BorderLayout(0, 0));
		JLabel lblGalleryLabel = new JLabel("Gallery Label");
		this.add(lblGalleryLabel, BorderLayout.NORTH);
		
		imagePanel = new JPanel();
		imagePanel.setBackground(Color.DARK_GRAY);
		
		FlowLayout fl_imagePanel = new FlowLayout(FlowLayout.LEFT, 5, 5);
		imagePanel.setLayout(fl_imagePanel);
		
		
		scrollPane = new JScrollPane(imagePanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(700, 123));
		add(scrollPane, BorderLayout.SOUTH);

	}
	
	public void addImagePanel(GalleryItemView image) {
		imagePanel.add(image);
		this.revalidate();
	}
	
}

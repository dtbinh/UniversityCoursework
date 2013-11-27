package ui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

import ui.models.ActiveImageModel;
import ui.utilities.LabelledImage;

public class GalleryItemView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImageView image_panel;
	private ActiveImageModel a_model;
	public String location;
	
	public GalleryItemView(LabelledImage labelIm) {
		a_model = new ActiveImageModel();
		a_model.setImage(labelIm);
		Dimension panelSize = new Dimension(labelIm.getImage().getHeight(null), labelIm.getImage().getWidth(null));
		image_panel = new ImageView(panelSize);
		image_panel.setModel(a_model, false);
		this.add(image_panel, BorderLayout.CENTER);
		location = labelIm.getLocation();
	}
	
	public void addClickListener(MouseListener ml) {
		this.addMouseListener(ml);
	}
}

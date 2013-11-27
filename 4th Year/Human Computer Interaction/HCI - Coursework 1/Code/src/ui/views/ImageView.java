package ui.views;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

import ui.models.ActiveImageModel;
import ui.utilities.LabelledImage;
import ui.utilities.Point;
import ui.utilities.Region;

public class ImageView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private LabelledImage labelled_image = null;
	//private Dimension size;
	private Boolean draw_regions = false;
	//private Region current_region = null;
	//private ArrayList<Point> active_points;
	private ActiveImageModel model = new ActiveImageModel();
	
	public ImageView(Dimension panelSize){
		//size = panelSize;

		
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);	
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);

		this.setOpaque(true);
		
	}
	
	public void setDimension(Dimension panelSize){
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);	
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);

		this.setOpaque(true);
	}
	
	public void setModel(ActiveImageModel a_model, boolean BoolDrawRegions) {
		this.model = a_model;
		draw_regions = BoolDrawRegions;
		this.repaint();
		
	}	
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		LabelledImage labelled_image = model.getCurrentImage();
		ArrayList<Point> active_points = model.getActivePoints();
		if (labelled_image != null){
			g.drawImage(labelled_image.getImage(),0,0,null);
			//g.setXORMode(Color.white);
			//Draw Completed regions
			
			if (draw_regions){
				for (int i = 0; i < labelled_image.getRegions().size(); i++){
					Region reg = labelled_image.getRegion(i);
				//for (Region reg : labelled_image.getRegions()){
					ArrayList<Point> points = reg.getPoints();
					drawRegion(g, points);
					closeRegion(g, points);
					System.out.println(model.getCurrentImage().getSRegionIndex());
					if (i == model.getCurrentImage().getSRegionIndex()){
						drawRegionPolygon(g, points,reg.getRegionColor());
					}
					

				}
				
				drawRegion(g, active_points);
				drawRegionPolygon(g, active_points,model.getEditorColor());
			}
			//Draw current region.
		}else {
			Font textFont = new Font("SansSerif", Font.PLAIN, 32);
			String noImage = "No image. Choose a new image to load.";
			g.setFont(textFont);
	        int xx = this.getWidth();
	        int yy = this.getHeight();
	        int w2 = g.getFontMetrics().stringWidth(noImage) / 2;
	        int h2 = g.getFontMetrics().getDescent();

	     
			g.setColor(Color.white);
			g.drawString(noImage, xx/2 -w2, yy / 2 + h2);
		}
		

		
	}
	
	private void drawRegion(Graphics g, ArrayList<Point> points) {
		
		if (points != null && points.size() != 0) {
			
			for (int i = points.size()-1; i >=0 ; i--) {
				Point currentVertex = points.get(i);

				if ( i == 1){
					g.setPaintMode();
					g.setColor(Color.black);
				}
				if (i != 0) {
					Point prevVertex = points.get(i - 1);
					g.drawLine(prevVertex.getX(), prevVertex.getY(),
							currentVertex.getX(), currentVertex.getY());
				}
				if(i==0){
					g.setColor(Color.RED);
					g.setXORMode(Color.white);
					g.fillOval(currentVertex.getX() -6, currentVertex.getY() -6, 12, 12);
					
				} else {
					g.setPaintMode();
					g.setColor(Color.white);
				}
		
				g.fillOval(currentVertex.getX() -5, currentVertex.getY() -5, 10, 10);
				g.setPaintMode();
				g.setColor(Color.black);
				g.fillOval(currentVertex.getX()-4 , currentVertex.getY()-4 , 8, 8);
				


			}

		}
		
	}
	
	private void drawRegionPolygon(Graphics g, ArrayList<Point> points, Color c){
		if (points != null && points.size() != 0) {
		int[] xPoints= new int[points.size()];
		int[] yPoints= new int[points.size()];
		for (int i =0; i < points.size(); i++){
			xPoints[i] = points.get(i).getX();
			yPoints[i] = points.get(i).getY();
		}
		
		g.setColor(c);
		this.setOpaque(true);
		g.fillPolygon(xPoints, yPoints, points.size());
		}
		
	}
	
	private void closeRegion(Graphics g, ArrayList<Point> points) {
		if (points.size() >= 3) {
			Point firstVertex = points.get(0);
			Point lastVertex = points.get(points.size() - 1);
			g.drawLine(firstVertex.getX(), firstVertex.getY(), lastVertex.getX(), lastVertex.getY());
		}
		
	}

	public void addClickListener(MouseListener cli){
		this.addMouseListener(cli);
	}


	
}

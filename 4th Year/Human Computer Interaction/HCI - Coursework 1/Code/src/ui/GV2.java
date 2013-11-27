package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GV2 extends JPanel{

	private JLabel image_label;

	public GV2(){
		this.setLayout(new BorderLayout(0, 0));
		
		JLabel lblGalleryLabel = new JLabel("Gallery Label");
		this.add(lblGalleryLabel, BorderLayout.NORTH);
		
		JPanel image_panel = new JPanel();
		image_label = new JLabel();
		image_panel.add(image_label);
		Dimension panelSize = new Dimension(800, 600);
		image_panel.setSize(panelSize);
		image_panel.setMinimumSize(panelSize);	
		image_panel.setPreferredSize(panelSize);
		image_panel.setMaximumSize(panelSize);
		this.add(image_panel, BorderLayout.CENTER);
		
	}
	
	void setGalleryImage(LabelledImage image){

//		LabelledImage scaledImage = scale(image, .8f);
		ImageIcon icon = new ImageIcon(image.getImage());

		image_label.setIcon(icon);


		
	}
	
	/*Modified from here:
	 * http://stackoverflow.com/questions/1069095/how-do-you-create-a-thumbnail-image-out-of-a-jpeg-in-java

	private LabelledImage scale(LabelledImage source,double ratio) {
		  int w = (int) (source.getImage().getWidth() * ratio);
		  int h = (int) (source.getImage().getHeight() * ratio);
		  BufferedImage bi = getCompatibleImage(w, h);
		  Graphics2D g2d = bi.createGraphics();
		  double xScale = (double) w / source.getImage().getWidth();
		  double yScale = (double) h / source.getImage().getHeight();
		  AffineTransform at = AffineTransform.getScaleInstance(xScale,yScale);
		  g2d.drawRenderedImage(source.getImage(), at);
		  g2d.dispose();
		  System.out.println(source.getRegions().length);
		  return new LabelledImage(bi, source.getRegions());
	}
*/
	private BufferedImage getCompatibleImage(int w, int h) {
		  GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		  GraphicsDevice gd = ge.getDefaultScreenDevice();
		  GraphicsConfiguration gc = gd.getDefaultConfiguration();
		  BufferedImage image = gc.createCompatibleImage(w, h);
		  return image;
	}
}

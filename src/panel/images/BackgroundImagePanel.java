package panel.images;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BackgroundImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	Image backgroundImage;

	public BackgroundImagePanel() {
		backgroundImage = new ImageIcon("School.jpg").getImage();
	}
	
	public BackgroundImagePanel(String imageFileName) {
		backgroundImage = new ImageIcon(imageFileName).getImage();
	}

	public void paintComponent(Graphics g) {
		Dimension size = new Dimension(backgroundImage.getWidth(null),
				backgroundImage.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setSize(size);
		int width = getWidth();  
        int height = getHeight();  
        int imageW = backgroundImage.getWidth(this);  
        int imageH = backgroundImage.getHeight(this);  
   
        // Tile the image to fill our area.  
        for (int x = 0; x < width; x += imageW) {  
            for (int y = 0; y < height; y += imageH) {  
                g.drawImage(backgroundImage, x, y, this);  
            }  
        }  	}
}
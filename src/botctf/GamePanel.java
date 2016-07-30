package botctf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	BufferedImage image = new BufferedImage(640, 640, BufferedImage.TYPE_INT_RGB);
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(640, 640);
	}
	
	public BufferedImage updateImage()
	{
//		synchronized (image) {
	
			Graphics g = image.getGraphics();
			for (int y = 0; y < Bot.map[0].length; y++) {
				for (int x = 0; x < Bot.map.length; x++) {
	
					switch (Bot.map[x][y]) {
						case Bot.mine:
							g.setColor(Color.GRAY);
						break;
						case Bot.ammo:
							g.setColor(Color.ORANGE);
							break;
						case Bot.blueFlag:
							g.setColor(Color.BLUE);
							break;
						case Bot.redFlag:
							g.setColor(Color.red);
							break;
						case Bot.blueTeam:
							g.setColor(Color.cyan);
							break;
						case Bot.redTeam:
							g.setColor(Color.magenta);
							break;
						case Bot.wall:
							g.setColor(Color.green);
							break;
						case Bot.space:
							g.setColor(Color.black);
							break;
					}
					g.fillRect(x * 10, y * 10, 10, 10);
				}
			}
			
			g.setColor(Color.blue);
	g.drawString("Red Ammo:"+Arena.getAmmo()[Bot.redTeam]+"Blue Ammo"+Arena.getAmmo()[Bot.blueTeam], 10,10);
	g.dispose();
			this.repaint();
			return image;
//		}
	}

	@Override
	public void paintComponent(Graphics g) {
//		synchronized (image) {
		g.drawImage(image, 0, 0, null);
//		}
	}
}

package botctf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(640, 640);
	}

	@Override
	public void paintComponent(Graphics g) {
	
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
	}
}

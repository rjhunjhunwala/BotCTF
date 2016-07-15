/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package botctf;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;


	public class GameFrame extends JFrame {

		private static final long serialVersionUID = 1L;

		public GameFrame() {
			super("Maze Game");
			this.add(new GamePanel());
			this.pack();
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setVisible(true);
			this.setResizable(false);
			this.setLocationRelativeTo(null);
			this.addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {

				}

				@Override
				public void keyPressed(KeyEvent e) {
					//MazeGame.Player.move(e.getKeyChar());
				}

				@Override
				public void keyReleased(KeyEvent e) {

				}
			});
		}
	}
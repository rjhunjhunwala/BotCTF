/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package botctf;

/**
 *
 * @author rohan
 */
public class BotCTF {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
Bot.playOneGame();
GameFrame g = new GameFrame();
 
for(;;){
	g.repaint();
}
	}
	
}

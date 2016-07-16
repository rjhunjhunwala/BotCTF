/**
 * Capture the flag The game is simple Your job is to extend the Bot Class.
 * 
* Two bots (which you code) will be placed in your starting zone
 * 
* The board is represented as an integer[] while you are not permitted to
 * directly access this array, you may call getMap(); Each spot on the map i.e
 * map[x][y] is the object at spot x,y with the origin being in the upper left
 * corner Which integer represents what is hardcoded into constant declarations
 * of the bot class Each turn you can choose a direction from 0-3 3(up) 2(left)
 * 0 (right) 1(down) And an action. Each move you can either 1. MoveType.Move
 * one square in the direction selected 2. MoveType.Defuse the mine sitting in
 * the square in the choosen direcion 3. MoveType.Throw a bomb in the direction
 * which goes 4 blocks and kills the first enemy it sees (costs one shared team
 * ammo) 4. MoveType.Mine places a mine on the square in the chosen direction.
 * Any player stepping on this mine dies (costs one shared team ammo)!
 * 
* Dying results in your bot no longer being able to play.
 * 
* Your team (your two bots) share a starting ammo cache this can be replenished
 * by stepping on squares marekd ammo.
 * 
* The objective is to touch the opposing flag while being careful not to let
 * ANYONE (including teammates) touch your own flag. Killing the opposition may
 * not result in a win You still MUST get their flag!
 * 
* getMap(); Hides the location of mines at any distance and enemies more than
 * five blocks away
 * 
* THE ONLY METHODS YOU MAY CALL ARE getX(); getY() getMap(); Arena.getAmmo();
 * Arena.getAliveBots(); You may not abuse reflection or access any parts of
 * this code.
 * 
* Good Luck
 */
package botctf;

import botctf.Bot.Coord;
import botctf.comms.data.player.Player;

import javax.swing.JOptionPane;

/**
 *
 * @author rohan
 */
public class Arena implements Runnable {

	public static final int botsPerSide = 5;
	public static final int numBots = botsPerSide * 2;
	public static final Bot[] bots = new Bot[numBots];
	private static int[] ammo = new int[256];

	public static int getAliveBots() {
		int i = 0;
		for (Bot b : bots) {
			if (b.isAlive()) {
				i++;
			}
		}
		return i;
	}

	static {
		bots[0] = new Player("java TestPlayer",2, 2, Bot.blueTeam);
		bots[1] = new Player("java AllForOneBot",3, 3, Bot.blueTeam);
		bots[2] = new Player("java AllForOneBot",4, 4, Bot.blueTeam);
		bots[3] = new Player("java AllForOneBot",5, 5, Bot.blueTeam);
		bots[4] = new Player("java AllForOneBot",6, 6, Bot.blueTeam);
//		bots[0] = new Player("java TestPlayer",2, 2, Bot.blueTeam);
//		bots[1] = new PathFinder(3, 3, Bot.blueTeam);
//		bots[2] = new PathFinder(4, 4, Bot.blueTeam);
//		bots[3] = new PathFinder(5, 5, Bot.blueTeam);
//		bots[4] = new PathFinder(6, 6, Bot.blueTeam);
		bots[5] = new PathFinder(62, 62, Bot.redTeam);
		bots[6] = new PathFinder(61, 61, Bot.redTeam);
		bots[7] = new PathFinder(60, 60, Bot.redTeam);
		bots[8] = new PathFinder(59, 59, Bot.redTeam);
		bots[9] = new PathFinder(58, 58, Bot.redTeam);
		for (Bot b : bots) {
			Bot.map[b.getX()][b.getY()] = b.team;
		}
	}

	/**
	 * @return the ammo
	 */
	public static int[] getAmmo() {
		return ammo.clone();
	}

	@Override
	public void run() {
		for (int k = 0; k < 4096; k++) {
			int firstBot = (int) (Math.random() * numBots);
			botLoop:
			for (int i = 0; i < numBots; i++) {
				if (bots[firstBot].isAlive()) {
										int f = bots[firstBot].team==Bot.redTeam?63:1;
					Coord flag = new Coord(f,f);
					if((Math.sqrt(Math.pow(bots[firstBot].x-flag.x,2)+Math.pow(bots[firstBot].y-flag.y,2))<5&&k%3!=0)){
						firstBot++;
						firstBot%=numBots;
					continue;
					}
					Move m = bots[firstBot].move();
					int x = bots[firstBot].getX();
					int y = bots[firstBot].getY();
					if (Bot.map[x][y] != bots[firstBot].team) {
						System.err.println("Cheater detected");
						System.exit(1);
					}

					if (m.direction >= 4 || m.direction < 0){
						//nop
					} else {
						int dX = (int) Math.cos(m.direction * Math.PI / 2);
						int dY = (int) Math.sin(m.direction * Math.PI / 2);
						//System.out.println(dX+"|"+dY);
						int adjX = x + dX;
						int adjY = y + dY;
						switch (m.moveType) {
							case Move:
								if (Bot.map[adjX][adjY] == Bot.space) {
									Bot.map[x][y] = Bot.space;
									Bot.map[adjX][adjY] = bots[firstBot].team;
									bots[firstBot].x += dX;
									bots[firstBot].y += dY;
								} else if (Bot.map[adjX][adjY] == Bot.mine) {
									bots[firstBot].die();
									Bot.map[x][y] = Bot.space;
									Bot.map[adjX][adjY] = Bot.space;
								} else if (Bot.map[adjX][adjY] == Bot.ammo) {
									Bot.map[x][y] = Bot.space;
									Bot.map[adjX][adjY] = bots[firstBot].team;
									bots[firstBot].x += dX;
									bots[firstBot].y += dY;
									ammo[bots[firstBot].team]++;
								} else if (Bot.map[adjX][adjY] == Bot.blueFlag) {
									JOptionPane.showMessageDialog(null, "RED WINS!", "RED WINS!", JOptionPane.PLAIN_MESSAGE);
									System.exit(0);
								} else if (Bot.map[adjX][adjY] == Bot.redFlag) {
									JOptionPane.showMessageDialog(null, "BLUE WINS!", "BLUE WINS!", JOptionPane.PLAIN_MESSAGE);
									System.exit(0);
								}

								break;
							case Mine:

								if (ammo[bots[firstBot].team] > 0 && Bot.map[adjX][adjY] == Bot.space) {
									ammo[bots[firstBot].team]--;
									System.out.printf("Mine placed at %s %s \n", adjX, adjY);
									Bot.map[adjX][adjY] = Bot.mine;
								}
								break;
							case Throw:
								if (ammo[bots[firstBot].team] > 0) {
									ammo[bots[firstBot].team]--;
									for (int j = 1; j < 5; j++) {
										if (Bot.inBounds(x + dX * j, y + dY * j)) {
											if (bots[firstBot].team == Bot.blueTeam) {
												if (Bot.map[x + dX * j][y + dY * j] == Bot.redTeam) {
													Bot.map[x + dX * j][y + dY * j] = Bot.space;
													for (Bot b : bots) {
														if (b.getX() == x + dX * j && b.getY() == y + dY * j) {
															b.die();
														}
													}
												}
											}
											if (bots[firstBot].team == Bot.redTeam) {
												if (Bot.map[x + dX * j][y + dY * j] == Bot.blueTeam) {
													Bot.map[x + dX * j][y + dY * j] = Bot.space;
													for (Bot b : bots) {
														if (b.getX() == x + dX * j && b.getY() == y + dY * j) {
															b.die();
														}
													}
												}
											}
										}
									}
								}
								break;
							case Defuse:
								if (Bot.map[adjX][adjY] == Bot.mine) {
									Bot.map[adjX][adjY] = Bot.space;
								}

						}
					}
				}
				firstBot++;
				firstBot %= numBots;
			}
		}
	}
}

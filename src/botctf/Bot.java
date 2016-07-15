/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package botctf;

import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author rohan
 */
public abstract class Bot {

	public static final int redTeam = 'R';
	public static final int blueTeam = 'B';
	public static final int blueFlag = 'F';
	public static final int redFlag = 'G';
	public static final int space = 'O';
	public static final int wall = 'W';
	public static final int mine = 'M';
	public static final int ammo = 'A';
 static int[][] map;

	/**
	 * The boolean at maze[x][y] is whether or not that spot is open
	 */
	public static boolean[][] maze;
	private boolean alive = true;

	public boolean isAlive() {

		return alive;
	}

	public void die() {
		System.out.printf("BOOM! The bot died at x: %s y: %s on team %s \n", x, y, team == redTeam ? "red" : "blue");
		alive = false;
	}
	public static Stack<Coord> nodes = new Stack<>();

	static {
		map = new int[65][65];
		//		map = file.getMapFromFile("map.txt");
//		for(int[] a: map){
//			System.out.println(Arrays.toString(a));
//		}
		makeMaze();
		for (int i = 0; i < maze[0].length; i++) {
			for (int j = 0; j < maze.length; j++) {
				if (maze[i][j]) {
					map[i][j] = space;
				} else {
					map[i][j] = wall;
				}
			}
		}
		for (int i = 1; i < 20; i++) {
			for (int j = 1; j < 20; j++) {
				map[i][j] = space;
				map[64 - i][64 - j] = space;
			}
		}
				for (int i = 1; i < 12; i++) {
			for (int j = 1; j < 12; j++) {
				map[26+i][26+j] = space;
			}
		}
								for (int i = 1; i <3; i++) {
			for (int j = 1; j < 3; j++) {
				map[31+i][31+j] = ammo;
			}
		}
		map[1][1] = blueFlag;
		map[63][63] = redFlag;
		int x, y;
		for (int i = 0; i < 24; i++) {
			if (map[x = (int) (Math.random() * 65)][y = (int) (Math.random() * 65)] == space) {
				map[x][y] = ammo;
			}
		}
				for (int i = 0; i < 750; i++) {
			if (map[x =1+ (int) (Math.random() * 63)][y =1+ (int) (Math.random() * 63)] == wall) {
				if(x%2!=0||y%2!=0)
				map[x][y] = space;
			}
		}
		new Thread(new Arena()).start();
	}

	public static void makeMaze() {
		maze = new boolean[65][65];
		nodes.push(new Coord(1, 1));
		while (!nodes.empty()) {
			findNewNode();
		}
	}

	public static void findNewNode() {
		Coord c = nodes.peek();
		int x = c.x;
		int y = c.y;

		int startCase = (int) (Math.random() * 4);

		for (int i = 0; i < 4; i++) {
			int attemptX = x, attemptY = y;
			switch (startCase) {
				case 0:
					attemptX += 2;
					break;
				case 1:
					attemptX -= 2;
					break;
				case 2:
					attemptY += 2;
					break;
				case 3:
					attemptY -= 2;
					break;
			}
			if (inBounds(attemptX, attemptY)) {
				if (!maze[attemptX][attemptY]) {
					maze[attemptX][attemptY] = true;
					maze[x + ((attemptX - x) / 2)][y + ((attemptY - y) / 2)] = true;
					nodes.push(new Coord(attemptX, attemptY));
					return;
				}
			}
			startCase += 1;
			startCase %= 4;
		}
		nodes.pop();
	}

	public static boolean inBounds(int x, int y) {
		return x > 0 && y > 0 && x < maze.length && y < maze[x].length;
	}

	/**
	 * I do not want bots to be able to start a new game whenever they want Thus
	 * this is an empty method which will trigger the static blocks
	 */
	public static void playOneGame() {
	}

	/**
	 * This is public but please do not modify your position
	 */
	public int x;
	public int y;
	public final int team;

	public Bot(int inX, int inY, int inTeam) {
		x = inX;
		y = inY;
		team = inTeam;
	}

	public abstract Move move();

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * The following function will return an int[][] representing the map The
	 * integer at the position [x][y] is what object is sitting there See the
	 * constant declarations up at the top of the class for reference Your bot
	 * knows the map like the back of his hand, but the only caveat is that, since
	 * it is dark out your bot has limited vision It is hard to see the hostiles
	 * unless you are less than five blocks away The origin is the upper left of
	 * the map
	 *
	 * @return
	 */
	public int[][] getMap() {
		int[][] ret = new int[map.length][map[0].length];
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				switch (map[i][j]) {
					case mine:
						ret[i][j] = space;
						break;
					case ammo:
					case space:
					case blueFlag:
					case redFlag:
					case wall:
						ret[i][j] = map[i][j];
						break;
					case redTeam:
					case blueTeam:
						if (map[i][j] == team) {
							ret[i][j] = map[i][j];
						} else {
							if (Math.sqrt(Math.pow(x - i, 2) + Math.pow(y - i, 2)) <= 5.1) {
								ret[i][j] = map[i][j];
							} else {
								ret[i][j] = space;
							}
						}
				}
			}
		}
		return ret;
	}

	public static class Coord {

		public final int x;
		public final int y;

		Coord(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * todo fight
 */
package botctf;

import botctf.Move.MoveType;
import java.util.ArrayList;

/**
 *
 * @author rohan
 */
public class PathFinder extends Bot {

	public static boolean shouldCampingTroll = true;
	private int moveCounter = -1;//dont ask
	public boolean defend;

	public PathFinder(int inX, int inY, int inTeam) {

		super(inX, inY, inTeam);
		//System.out.println("Start");
		//floodFillMap(getX(), getY());
		//System.out.println("Finish");
		defend=inX%2==0;
	}
	public static int[][] navigationMap;

	/**
	 * We are using an integer matrix to represent the distance each spot if from
	 * the player The position being attempted to be reached is one Non reachable
	 * positions or walls or represented by Integer.MAX_VALUE The reset of the
	 * positions represent the distance needed to travel in the maze this is to be
	 * used for efficient navigation
	 *
	 * @param inX
	 * @param inY
	 * @return
	 */
	public int[][] floodFillMap(int inX, int inY) {
		//System.out.println(inX+"|"+inY);

		navigationMap = new int[65][65];
		//System.out.println(navigationMap.length+"|"+navigationMap[0].length);
		markOpen(inX, inY, 1);
		for (int i = 0; i < 65; i++) {
			for (int j = 0; j < 65; j++) {
				if (navigationMap[i][j] == 0) {
					navigationMap[i][j] = Integer.MAX_VALUE;
				}
			}
		}

		return navigationMap.clone();
	}
	boolean upMine = false;
	boolean sideMine = false;

	public void markOpen(int floodX, int floodY, int num) {
if(inBounds(floodX,floodY))
		navigationMap[floodX][floodY] = num;
		if (inBounds(floodX+1,floodY)&&(navigationMap[floodX + 1][floodY] == 0 || num + 1 < navigationMap[floodX + 1][floodY]) && (myMap[floodX + 1][floodY] != wall)) {
			markOpen(floodX + 1, floodY, num + 1);
		}

		if (inBounds(floodX-1,floodY)&&(navigationMap[floodX - 1][floodY] == 0 || num + 1 < navigationMap[floodX - 1][floodY]) && (myMap[floodX - 1][floodY] != wall)) {
			markOpen(floodX - 1, floodY, num + 1);
		}

		if (inBounds(floodX,floodY+1)&&(navigationMap[floodX][floodY + 1] == 0 || num + 1 < navigationMap[floodX][floodY + 1]) && (myMap[floodX][floodY + 1] != wall)) {
			markOpen(floodX, floodY + 1, num + 1);
		}

		if (inBounds(floodX,floodY-1)&&(navigationMap[floodX][floodY - 1] == 0 || num + 1 < navigationMap[floodX][floodY - 1]) && (myMap[floodX][floodY - 1] != wall)) {
			markOpen(floodX, floodY - 1, num + 1);
		}
	}
	int[][] myMap;

	@Override
	public Move move() {
				moveCounter++;
		myMap=getMap();
		int targetX, targetY;
		int enemyTeam=team==redTeam?blueTeam:redTeam;
		ArrayList<Coord> enemyCoordinates=new ArrayList<>();
		for(int i = 0; i<65;i++){
			for(int j = 0;j<65;j++){
				if(map[i][j]==enemyTeam){
					enemyCoordinates.add(new Coord(i,j));
				}
			}
		}
		for(Coord enemy:enemyCoordinates){
			int enemyX=enemy.x;
			int enemyY=enemy.y;
		 int dX= enemy.x-this.x;
			int dY= enemy.y-this.y;
			//System.out.println(dX+"|"+dY);
			if((dX==0||dY==0)){
			
				if(Arena.getAmmo()[this.team]>0){

					if(dX>0&&dX<5){
					return new Move(0,MoveType.Throw);
				}
				if(dX<0&&dX>-5){
					return new Move(2,MoveType.Throw);
				}
				if (dY>0&&dY<5){
					return new Move(1, MoveType.Throw);
				}
				if(dY<0&&dY>-5){
					return new Move(3,MoveType.Throw);
				}
			}
		}
		}
		if(myMap[x+1][y]==ammo){
			return new Move(0,MoveType.Move);
		}
				if(myMap[x-1][y]==ammo){
			return new Move(2,MoveType.Move);
		}
								if(myMap[x][y+1]==ammo){
			return new Move(1,MoveType.Move);
		}
																if(myMap[x][y-1]==ammo){
			return new Move(3,MoveType.Move);
		}

		if (defend) {
if(Arena.getAliveBots()==1){
	defend=false;
}
			int bestAmmoX = -1;
			int bestAmmoY = -1;
			int bestAmmoDist = Integer.MAX_VALUE;
			int[][] distancesToMe = floodFillMap(getX(), getY());
			for (int i = 0; i < 65; i++) {
				for (int j = 0; j < 65; j++) {
					if (getMap()[i][j] == ammo) {
						if (distancesToMe[i][j] < bestAmmoDist) {
							bestAmmoX = i;
							bestAmmoY = j;
							bestAmmoDist = distancesToMe[i][j];
						}
					}
				}
			}
			if (bestAmmoDist<15||Arena.getAmmo()[this.team]==0){
				targetX = bestAmmoX;
				targetY = bestAmmoY;
			} else {
				targetX = team == redTeam ? 62 : 2;
				targetY = team == redTeam ? 62 : 2;
			}
		} else {
			
			if(this.x>18&this.x<42&&this.y>16&&this.y<44&&getMap()[33][33]==ammo){
				targetX=33;
				targetY=33;
			}else{
			if (this.team == redTeam) {
				targetX = 1;
				targetY = 1;
			} else {
				targetX = 63;
				targetY = 63;
			}
			}
		}
		if(upMine&&sideMine){
			if(targetX==2||targetX==62){
				if(targetY==2||targetY==62){
					targetX+=targetX==2?3:-3;
					targetY+=targetY==2?3:-3;
				}
			}
		}else if (targetX == getX() && targetY == getY()) {
			if (!upMine) {
				upMine = true;
				if (this.team == redTeam) {
					return new Move(0, MoveType.Mine);
				} else {
					return new Move(2, MoveType.Mine);
				}
			}else if(!sideMine){
				sideMine=true;		
				if (this.team == redTeam) {
					return new Move(1, MoveType.Mine);
				} else {
					return new Move(3, MoveType.Mine);
				}
			}	else {
				return new Move(5, MoveType.Move);
			}
		}


//		targetX=1;
//		targetY=1;
		if(team ==redTeam){
			if(x>61&&y>61){
				targetX=58;
			 targetY=58;
			}
		}
		if(team==blueTeam){
						if(x<3&&y<3){
				targetX=4;
			 targetY=4;
			}
		}
int topMineLoc=2;
int botMineLoc=62;
//todo fix bug of stepping on own mines
		int[][] navMap = floodFillMap(targetX, targetY);
		int bestOption = 4;
		int shortestDist = Integer.MAX_VALUE;
				if ((x!=botMineLoc||y!=botMineLoc)&&navMap[getX() + 1][getY()] < shortestDist) {
			if (myMap[getX() + 1][getY()] == space || myMap[getX() + 1][getY()] == ammo || myMap[getX() + 1][getY()] == ((team == redTeam) ? blueFlag : redFlag)) {
				bestOption = 0;
				shortestDist = navMap[getX() + 1][getY()];
			}
		}
				if ((x!=topMineLoc||y!=topMineLoc)&&navMap[getX()][getY() - 1] < shortestDist) {
			if (myMap[getX()][getY() - 1] == space || myMap[getX()][getY() - 1] == ammo || myMap[getX()][getY() - 1] == ((team == redTeam) ? blueFlag : redFlag)) {
				bestOption = 3;
				shortestDist = navMap[getX()][getY() - 1];
			}
		}
		if ((x!=topMineLoc||y!=topMineLoc)&&navMap[getX() - 1][getY()] < shortestDist) {
			if (myMap[getX() - 1][getY()] == space || myMap[getX() - 1][getY()] == ammo || myMap[getX() - 1][getY()] == ((team == redTeam) ? blueFlag : redFlag)) {
				bestOption = 2;
				shortestDist = navMap[getX() - 1][getY()];
			}
		}
		if ((x!=botMineLoc||y!=botMineLoc)&&navMap[getX()][getY() + 1] < shortestDist) {
			if (myMap[getX()][getY() + 1] == space || myMap[getX()][getY() + 1] == ammo || myMap[getX()][getY() + 1] == ((team == redTeam) ? blueFlag : redFlag)) {
				bestOption = 1;
				shortestDist = navMap[getX()][getY() + 1];
			}
		}

MoveType m=MoveType.Move;
if(moveCounter%2==0){
	if(this.team==redTeam?x<25&&y<25:x>39&&y>39){
		m=MoveType.Defuse;
	}
}
//System.out.println(bestOption);
		return new Move(bestOption, m);
	}
}

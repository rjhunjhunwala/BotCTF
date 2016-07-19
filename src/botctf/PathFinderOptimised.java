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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author rohan
 */
public class PathFinderOptimised extends Bot {
    private static final int[][] offsets = new int[][]{{0,-1},{1,0},{0,1},{-1,0}};
	public static boolean shouldCampingTroll = true;
	private int moveCounter = -1;//dont ask
	public boolean defend;

	public PathFinderOptimised(int inX, int inY, int inTeam) {

		super(inX, inY, inTeam);
		//System.out.println("Start");
		//floodFillMap(getX(), getY());
		//System.out.println("Finish");
		defend=inX%2==0;
	}
	public static int[][] navigationMap;

	boolean upMine = false;
	boolean sideMine = false;

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


int bestOption = 4;																
		if (defend) {
if(Arena.getAliveBots()==1){
	defend=false;
}
			int bestAmmoX = -1;
			int bestAmmoY = -1;
			int bestAmmoDist = Integer.MAX_VALUE;
			for (int i = 0; i < 65; i++) {
				for (int j = 0; j < 65; j++) {
					if (myMap[i][j] == ammo) {
						int path = pathFind(getX(),getY(),i,j,myMap);
						if ((path & 0xFFFFFF) < bestAmmoDist) {
							bestAmmoX = i;
							bestAmmoY = j;
							bestAmmoDist = (path & 0xFFFFFF);
							bestOption = path >> 24;
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
			
			if(this.x>18&this.x<42&&this.y>16&&this.y<44&&myMap[33][33]==ammo){
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

		bestOption = pathFind(getX(),getY(),targetX,targetY,myMap) >> 24;


MoveType m=MoveType.Move;
if(moveCounter%2==0){
	if(this.team==redTeam?x<25&&y<25:x>39&&y>39){
		m=MoveType.Defuse;
	}
}
//System.out.println(bestOption);
		return new Move(bestOption, m);
	}
	
	/**
	 * returns a result that is the combination of movement direction and length of a path found from the given start position to the target
	 * position. result is ((direction) << 24 + path_length)
	 */
	private int pathFind(int startX, int startY, int targetX,int targetY,int[][] map)
	{
		class PathSegment
		{
			public PathSegment(int tileX, int tileY, int fscore, int gscore, PathSegment parent)
			{
				super();
				this.tileX = tileX;
				this.tileY = tileY;
				this.fscore = fscore;
				this.gscore = gscore;
				this.parent = parent;
			}
			public PathSegment(PathSegment parent)
			{
				this.parent = parent;
			}
			int tileX;
			int tileY;
			int fscore;
			int gscore;
			PathSegment parent; 
		}
	    // A*
	    if (startX==targetX && startY==targetY)
	    {
	    	return 4;
	    }
	    else
	    {
	    	int[][] tileIsClosed = new int[64][64];

	        PathSegment curSegment = new PathSegment(targetX,targetY,1,1,null);
	        PathSegment newSegment;
	        Set<PathSegment> openList = new HashSet<PathSegment>();
	        openList.add(curSegment);

	        do
	        {
	        	if (openList.isEmpty())
	        	{
	        		break;
	        	}
	          PathSegment currentBestScoringSegment = openList.iterator().next();
	          //  Look for the lowest F cost square on the open list
	          for (PathSegment segment : openList)
	          {
	            if (segment.fscore<currentBestScoringSegment.fscore)
	            {
	              currentBestScoringSegment = segment;
	            }
	          }
	          curSegment = currentBestScoringSegment;

	          // found path
	          if (startX==curSegment.tileX && startY==curSegment.tileY)
	          {
	            break;
	          }

	          // if not in closed list
	          if (tileIsClosed[curSegment.tileX][curSegment.tileY]==0)
	          {
	                // Switch it to the closed list.
	                tileIsClosed[curSegment.tileX][curSegment.tileY]=1;
	                // remove from openlist
	                openList.remove(curSegment);
  

	                // add neigbours to the open list if necessary
	                for (int i=0;i<4;i++)
	                {
                        final int surroundingCurrentTileX=curSegment.tileX+offsets[i][0];
                        final int surroundingCurrentTileY=curSegment.tileY+offsets[i][1];
                        if (surroundingCurrentTileX>=0 && surroundingCurrentTileX<64 &&
                            surroundingCurrentTileY>=0 && surroundingCurrentTileY<64 )
                        {
                        	newSegment = new PathSegment( curSegment);
                        	newSegment.tileX = surroundingCurrentTileX;
                        	newSegment.tileY = surroundingCurrentTileY;
                      
                          if (map[surroundingCurrentTileX][surroundingCurrentTileY]=='W')
                          {
                              continue;
                          }

                          int surroundingCurrentGscore=curSegment.gscore+1;
                          newSegment.gscore=surroundingCurrentGscore;
                          newSegment.fscore=surroundingCurrentGscore+Math.abs( surroundingCurrentTileX-startX)+Math.abs( surroundingCurrentTileY-startY);
                          openList.add(newSegment);
                        }
	                }
	          }
	          else
	          {
	        	  // remove from openlist
	        	  openList.remove(curSegment);    
	          }
	        } while(true);

	        if (curSegment.parent.tileX-startX<0) return (2 << 24) | curSegment.gscore;
	        else if (curSegment.parent.tileX-startX>0) return (0 << 24) | curSegment.gscore;
	        else if (curSegment.parent.tileY-startY<0) return (3 << 24) | curSegment.gscore;
	        else if (curSegment.parent.tileY-startY>0) return (1 << 24) | curSegment.gscore;
	    }
	    throw new RuntimeException("Path finding failed");
	 }
}

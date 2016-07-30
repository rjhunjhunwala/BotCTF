import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class NavPointBot implements Serializable 
{
    private static final int[][] offsets = new int[][]{{-1,0},{0,-1},{1,0},{0,1}};
    private static final List<int[]> navPointsBlue = Arrays.asList(new int[][]{{1,2},{2,1}});
    private static final List<int[]> navPointsRed = Arrays.asList(new int[][]{{63,62},{62,63}});
    transient private static int mapWidth=0;
    transient private static int mapHeight=0;
    transient private char[][] map;
    transient private char team;
    transient private int ammo;
    transient private int botsAlive;
    transient private int enemyFlagX;
    transient private int enemyFlagY;
    private int frameCount;
    private int botX;
    private int botY;
    private String id;
    private int navPointX;
    private int navPointY;
    
	transient static Object synchObject = new Object(); // used for file read/write synchronisation if multiple instances are run in the same VM
    
    final static class Data implements Serializable
    {
    	int frameCount;
    	boolean[][] diffusedMap = new boolean[mapWidth][mapHeight];
        Map<String,NavPointBot> teamMembers = new HashMap<>();
    }
    
    interface DistanceWeigher
    {
    	double applyWeight(NavPointBot p1Bot, PathSegment p1);
    }
    
    static class PathSegment
    {
		public PathSegment(int tileX, int tileY, int fscore, int gscore, PathSegment parent, int direction, int targetX, int targetY)
        {
            super();
            this.tileX = tileX;
            this.tileY = tileY;
            this.fscore = fscore;
            this.gscore = gscore;
            this.parent = parent;
            this.direction = direction;
            this.targetX = targetX;
            this.targetY = targetY;
        }
        public PathSegment(PathSegment parent)
        {
            this.parent = parent;
            this.targetX = parent.targetX;
            this.targetY = parent.targetY;
        }
        int tileX;
        int tileY;
        int fscore;
        int gscore;
        int direction;
        PathSegment parent; 
		int targetX;
		int targetY;
    }
    
    public static void main(String[] args) throws Exception
    {
        new NavPointBot(UUID.randomUUID().toString());
    }
    
    private NavPointBot(String id) throws Exception
    {
        this.id = id;
        System.err.println("NavPointBot ("+id+") STARTED");

        Data data;
        while(true)
        {
            String line=readLine(System.in);
            
            // decode initial frame
            if ("INIT".equals(line))
            {
                // read team membership
                team = readLine(System.in).charAt(0);
                
                // get the map
                line = readLine(System.in);

                List<char[]> mapLines = new ArrayList<>();
                while(!"TINI".equals(line))
                {
                    mapLines.add(line.toCharArray());
                    line = readLine(System.in);
                }
                map = mapLines.toArray(new char[][]{});
                mapHeight = map.length;
                mapWidth = map[0].length;
                
                out:
                for (int y = 0; y<mapHeight;y++)
                {
                	for (int x=0; x<mapWidth;x++)
                	{
                		if (map[y][x]==(team=='B'?'G':'F'))
                		{
                			enemyFlagX = x;
                			enemyFlagY = y;
                			break out;
                		}
                	}
                }
                data = readSharedData();
                data.diffusedMap=new boolean[mapWidth][mapHeight];
                writeSharedData(data);
                
            }
            else
            {
                System.err.println("Unknown command received: "+line);
                return;
            }
            
            line = readLine(System.in);
            while (true)
            {
            	// decode frame
                if ("FRAME".equals(line))
                {
                	frameCount = Integer.parseInt(readLine(System.in));
                    ammo = Integer.parseInt(readLine(System.in));
                    botsAlive = Integer.parseInt(readLine(System.in));
                    line = readLine(System.in);
                    String[] splits = line.split(",");
                    botX = Integer.parseInt(splits[0]);
                    botY = Integer.parseInt(splits[1]);
                    
                    // get the map
                    line = readLine(System.in);

                    int row=0;
                    while(!"EMARF".equals(line))
                    {
                        map[row++] = line.toCharArray();
                        line = readLine(System.in);
                    }
                }
                else
                {
                    System.err.println("Unknown command received: "+line);
                    return;
                }

                	
                data = readSharedData();
                
                // this bot is nomitated to be the leader for this frame
                if (data.frameCount<frameCount || (frameCount==0 && data.frameCount > 3))
                {
                	data.frameCount=frameCount;
                	
                	List<NavPointBot> unassignedBots = new ArrayList<>(data.teamMembers.values());
                	
                    // default nav points to be enemy flag location.
                	unassignedBots.forEach(t->{t.navPointY=enemyFlagY;t.navPointX=enemyFlagX;});
                	
                	// after 700 frames assume dead lock so just storm the flag, otherwise...
                	if (frameCount<700)
                	{
                    	// if the after the initial rush then we will assign guard(s) while we have enemies
                    	if (frameCount>70 && botsAlive > data.teamMembers.size())
                    	{
		                    Map<NavPointBot, PathSegment> navPointDistances = assignBotShortestPaths(unassignedBots,team=='B'?navPointsBlue:navPointsRed,true, new DistanceWeigher() {
								
								@Override
								public double applyWeight( NavPointBot owner ,PathSegment target) {
									return target.gscore;
								}
							});
		                    navPointDistances.keySet().forEach(s->{s.navPointX=navPointDistances.get(s).targetX;s.navPointY=navPointDistances.get(s).targetY;});
                    	}

	                    
	                    // the remaining bots will go to ammo depots with a preference to the middle ammo depots
	                    List<int[]> ammoDepots = new ArrayList<>();
	                    for (int y = 0; y<mapHeight;y++)
	                    {
	                    	for (int x=0; x<mapWidth;x++)
	                    	{
	                    		if (map[y][x]=='A')
	                    		{
	                    			ammoDepots.add(new int[]{x,y});
	                    		}
	                    	}
	                    }
	                    
	                    System.err.println("ammoDepots: "+ammoDepots.size());
	                    if (ammoDepots.size()>0)
		                {
		                    Map<NavPointBot, PathSegment> ammoDistances = assignBotShortestPaths(unassignedBots,ammoDepots,true, new DistanceWeigher() {
								
								@Override
								public double applyWeight( NavPointBot owner ,PathSegment target) {
									return target.gscore + (Math.abs(target.targetX-mapWidth/2)+Math.abs(target.targetY-mapHeight/2)*10);
								}
							});
	
		                    
		                    // assign ammo depot nav points to closest bots
		                    ammoDistances.keySet().forEach(s->{s.navPointX=ammoDistances.get(s).targetX;s.navPointY=ammoDistances.get(s).targetY;});
		                }
                	}
                    
                    System.err.println("FRAME: "+frameCount+" SET");
                    data.teamMembers.values().forEach(bot->System.err.println(bot.id+" nav point ("+bot.navPointX+","+bot.navPointY+")"));
                    System.err.println();
                }

                
                // check to see if enemies are in range, if so attack the closest
                List<int[]> enemies = new ArrayList<>();
                for (int y = 0; y<mapHeight;y++)
                {
                	for (int x=0; x<mapWidth;x++)
                	{
                		if (map[y][x]==(team=='B'?'R':'B'))
                		{
                			int attackDir = -1;
                			int distance = -1;
                			if (x==botX && Math.abs(y-botY) < 4) { distance =  Math.abs(y-botY); attackDir = botY-y<0?1:3;}
                			if (y==botY && Math.abs(x-botX) < 4) { distance =  Math.abs(x-botX); attackDir = botX-x<0?0:2;}
                			if (attackDir>-1)
                			{
                				enemies.add(new int[]{x,y,distance,attackDir});
                			}
                		}
                	}
                }
                
                enemies.sort(new Comparator<int[]>() {

					@Override
					public int compare(int[] arg0, int[] arg1) {
						return arg0[2]-arg1[2];
					}
				});
                
                String action;
                
                // attack enemy if one within range...
				if (enemies.size()>0)
                {
                	action = "Throw,"+enemies.get(0)[3];
                }
                else
                {
                	// set action to move to navpoint
                    PathSegment pathSegment = pathFind(botX,botY,navPointX,navPointY,map,true);
                    action = "Move,"+pathSegment.direction;
                	
                	// clear mines if within 5 spaces of enemy flag

                	if ((team=='B' && botX>=mapWidth-5 && botY>=mapHeight-5 ) ||
                		(team=='R' && botX<5 && botY<5 ))
            		{
                		if (!data.diffusedMap[pathSegment.parent.tileX][pathSegment.parent.tileY])
                		{
                			action = "Defuse,"+pathSegment.direction;
                			data.diffusedMap[pathSegment.parent.tileX][pathSegment.parent.tileY]=true;
                		}
            		}
                    
                }
                
                writeSharedData(data);
                System.out.println(action);
                line = readLine(System.in);
            }
        }
    }

    /**
     * assigns bots to paths to the given points based on distance to the points with weights adjusted by the given weigher implementation 
     */
	private Map<NavPointBot, PathSegment> assignBotShortestPaths(List<NavPointBot> bots, List<int[]> points, boolean exact, DistanceWeigher weigher) {
		
		Map<Integer,List<PathSegment>> pathMap = new HashMap<>();
		final Map<PathSegment,NavPointBot> pathOwnerMap = new HashMap<>();

		for (NavPointBot bot : bots)
		{
		    for(int[] navPoint: points)
		    {
		    	List<PathSegment> navPointPaths = pathMap.get((navPoint[0]<<8)+navPoint[1]);
		    	if (navPointPaths == null)
		    	{
		    		navPointPaths = new ArrayList<>();
		    		pathMap.put((navPoint[0]<<8)+navPoint[1],navPointPaths);
		    	}
		    	PathSegment path = pathFind(bot.botX,bot.botY,navPoint[0],navPoint[1],map,exact);
		    	pathOwnerMap.put(path, bot);
		    	navPointPaths.add(path);
		    }
		}
		
		
		// assign bot nav point based on shortest distance
		Map<NavPointBot, PathSegment> results = new HashMap<>();
		for (int[] navPoint: points )
		{
			List<PathSegment> navPointPaths = pathMap.get((navPoint[0]<<8)+navPoint[1]);
		
			if (navPointPaths !=null)
			{
				Collections.sort(navPointPaths, new Comparator<PathSegment>() {
	
					@Override
					public int compare(PathSegment p1, PathSegment p2) {
						
						NavPointBot p1Bot = pathOwnerMap.get(p1);
						NavPointBot p2Bot = pathOwnerMap.get(p2);
						double val = weigher.applyWeight(p1Bot, p1) - weigher.applyWeight(p2Bot, p2);
						if (val == 0)
						{
	
							return p1Bot.id.compareTo(p2Bot.id);
						}
						return val<0?-1:1;
					}
				});
				
				for (PathSegment shortestPath : navPointPaths)
				{
			       	NavPointBot bot = pathOwnerMap.get(shortestPath);
			       	
			       	if (!results.containsKey(bot) )
			       	{
			       		results.put(bot,shortestPath);
			       		bots.remove(bot);
			       		break;
			       	}
				}
			}
		}
		return results;
	}
    
	/**
	 * reads in the previous bot's view of teammates aka shared data
	 */
	private Data readSharedData() throws Exception
	{
		synchronized(synchObject)
		{
			File dataFile = new File(this.getClass().getName()+"_"+team);
			
			Data data;
			if (dataFile.exists())
			{
				FileInputStream in = new FileInputStream(dataFile);
				try {
				    java.nio.channels.FileLock lock = in.getChannel().lock(0L, Long.MAX_VALUE, true);
				    try {
				    	ObjectInputStream ois = new ObjectInputStream(in);
					    data = (Data) ois.readObject();
				    } catch(Exception e)
				    {
				    	System.err.println(id+": CORRUPT shared Data... re-initialising");
				    	data = new Data();
				    }
				    finally {
				        lock.release();
				    }
				} finally {
				    in.close();
				}
			}
			else
			{
		    	System.err.println(id+": No shared shared Data exists... initialising");
				data = new Data();
			}
				
		    //purge any dead teammates...
		    for (NavPointBot bot : new ArrayList<>(data.teamMembers.values()))
		    {
		        if (bot.frameCount < frameCount-3 || bot.frameCount > frameCount+3)
		        {
		            data.teamMembers.remove(bot.id);
		        }
		    }
		    
		    // update our local goals to reflect those in the shared data
		    NavPointBot dataBot = data.teamMembers.get(id);
		    if (dataBot !=null)
		    {
		    	this.navPointX=dataBot.navPointX;
		    	this.navPointY=dataBot.navPointY;
		    }
		    
		    // ensure that we are a team member
		    data.teamMembers.put(id, this);
		    
			return data;
		}
	}
	
	private void writeSharedData(Data data) throws Exception
	{
		synchronized(synchObject)
		{
			File dataFile = new File(this.getClass().getName()+"_"+team);
		    FileOutputStream out = new FileOutputStream(dataFile);
			
			try {
			    java.nio.channels.FileLock lock = out.getChannel().lock(0L, Long.MAX_VALUE, false);
			    try {
			    	ObjectOutputStream oos = new ObjectOutputStream(out);
				    oos.writeObject(data);
					oos.flush();
			    } finally {
			        lock.release();
			    }
			} finally {
			    out.close();
			}
		}
	}
    
    /**
     * return the direction to move to travel for the shortest route to the desired target location
     */
    private PathSegment pathFind(int startX, int startY, int targetX,int targetY,char[][] map,boolean exact)
    {
        // A*
        if (startX==targetX && startY==targetY)
        {
            return new PathSegment(targetX,targetY,0, 0,null,4,targetX,targetY);//PathSegment.DEFAULT;
        }
        else
        {
            int[][] tileIsClosed = new int[mapWidth][mapHeight];
            
            // find an open space in the general vicinity if exact match not required
            if (!exact)
            {
            	out:
            	for (int y=-1;y<=1;y++)
            	{
            		for (int x=-1;x<=1;x++)
            		{
            			if (startX == targetX+x && startY==targetY+y)
            			{
            				return new PathSegment(targetX,targetY,0, 0,null,4,targetX,targetY);//PathSegment.DEFAULT;
            			}
            			else if (targetY+y>=0 && targetY+y<mapHeight && targetX+x>=0 && targetX+x < mapWidth && map[targetY+y][targetX+x]=='O')
            			{
            				targetX+=x;
            				targetY+=y;
            				break out;
            			}
            		}
            	}
            }

            PathSegment curSegment = new PathSegment(targetX,targetY,1,1,null,4,targetX,targetY);
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

                        int surroundingCurrentTileX=curSegment.tileX+offsets[i][0];
                        int surroundingCurrentTileY=curSegment.tileY+offsets[i][1];
                        if (surroundingCurrentTileX>=0 && surroundingCurrentTileX<mapWidth &&
                            surroundingCurrentTileY>=0 && surroundingCurrentTileY<mapHeight )
                        {
                            newSegment = new PathSegment( curSegment);
                            newSegment.tileX = surroundingCurrentTileX;
                            newSegment.tileY = surroundingCurrentTileY;
                            newSegment.direction = i;
                      
                            switch(map[surroundingCurrentTileY][surroundingCurrentTileX])
                    		{
                            	case 'W':
                            	case 'F':
                            	case 'G':
                            		continue;
                    		}

                          int surroundingCurrentGscore=curSegment.gscore+1 + ((surroundingCurrentTileX!=startX && surroundingCurrentTileY!=startY && map[surroundingCurrentTileY][surroundingCurrentTileX]==team)?20:0);//+map[surroundingCurrentTileY][surroundingCurrentTileX]!='O'?100:0;
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

            return curSegment;
        }
     }
    
    /**
     * Reads a line of text from the input stream. Blocks until a new line character is read.
     * NOTE: This method should be used in favor of BufferedReader.readLine(...) as BufferedReader buffers data before performing
     * text line tokenization. This means that BufferedReader.readLine() will block until many game frames have been received. 
     * @param in a InputStream, nominally System.in
     * @return a line of text or null if end of stream.
     * @throws IOException
     */
    private static String readLine(InputStream in) throws IOException
    {
       StringBuilder sb = new StringBuilder();
       int readByte = in.read();
       while (readByte>-1 && readByte!= '\n')
       {
          sb.append((char) readByte);
          readByte = in.read();
       }
       return readByte==-1?null:sb.toString();
        
    }

}


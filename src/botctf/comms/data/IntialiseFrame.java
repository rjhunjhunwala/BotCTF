package botctf.comms.data;

import botctf.Bot;
import botctf.comms.data.player.Player;

public class IntialiseFrame {

	private Player player;


	public IntialiseFrame(Player player)
	{
		this.player = player;
	}
	
	public void toMessage(StringBuilder sb)
	{
		sb.append("INIT"); sb.append("\n");
		appendMap(sb); sb.append("\n");
		sb.setLength(sb.length()-1); // sending message already appends new line character
	}
	
	private void appendMap(StringBuilder sb)
	{
		int[][] map = player.getMap();
		final int maxX = map[0].length;
		final int maxY = map.length;
		for (int y=0;y<maxY;y++)
		{
			for (int x=0;x<maxX;x++)
			{
				int cellValue = (char) map[x][y];
				switch (cellValue)
				{
					case Bot.mine:
					case Bot.blueTeam:
					case Bot.redTeam:
					case Bot.ammo:
						cellValue=Bot.space;
				}
				sb.append((char) cellValue);
			}
			sb.append("\n");
		}
	}

}

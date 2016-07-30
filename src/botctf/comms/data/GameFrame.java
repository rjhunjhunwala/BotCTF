package botctf.comms.data;

import botctf.Arena;
import botctf.comms.data.player.Player;

public class GameFrame {

	private Player player;

	public GameFrame(Player player) {
		this.player = player;
	}

	public void toMessage(StringBuilder sb) {
		sb.append("FRAME");
		sb.append("\n");
		sb.append(player.frame);
		sb.append("\n");
		sb.append(Arena.getAmmo()[player.team]);
		sb.append("\n");
		sb.append(Arena.getAliveBots());
		sb.append("\n");
		sb.append(player.getX());
		sb.append(",");
		sb.append(player.getY());
		sb.append("\n");
		appendLocalMap(sb);
		sb.append("EMARF");
		sb.append("\n");
		sb.setLength(sb.length() - 1); // sending message already appends new line character
	}

	private void appendLocalMap(StringBuilder sb) {
		int[][] map = player.getMap();
		final int maxX = map[0].length;
		final int maxY = map.length;
		for (int y = 0; y <maxY; y++) {
			for (int x = 0; x < maxX; x++) {

				sb.append((char) map[x][y]);

			}
			sb.append("\n");
		}
	}

}

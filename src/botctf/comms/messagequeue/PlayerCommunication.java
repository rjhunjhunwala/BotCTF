package botctf.comms.messagequeue;

import java.io.InputStream;
import java.io.OutputStream;

import botctf.comms.data.GameFrame;
import botctf.comms.data.player.Player;
import botctf.comms.handlers.InputMessageHandler;

public class PlayerCommunication implements InputMessageHandler
{
	private InputMessageChannel inputMessageQueue;
	private OutputMessageChannel outputMessageQueue;
	private Player player;
	
	public PlayerCommunication(InputStream is, OutputStream os, Player player)
	{
		inputMessageQueue = new InputMessageChannel(is, this);
		outputMessageQueue = new OutputMessageChannel(os);
		this.player = player;
	}
	
	public boolean processNextMessage()
	{
		return inputMessageQueue.processNextMessage();
	}
	
	public void sendMessage(String message)
	{
		outputMessageQueue.sendMessage(message);
	}
	
	public void sendFrame(GameFrame frame)
	{
		StringBuilder sb= new StringBuilder();
		frame.toMessage(sb);
		outputMessageQueue.sendFrame(sb.toString());
	}

	@Override
	public void invalidCommand(String reason) {
		reason = reason.replace("\r", "");
		StringBuilder sb = new StringBuilder();
		sb.append("NACK");
		sb.append(",");
		sb.append(reason);		
		player.communications.sendMessage(sb.toString());
	}

	@Override
	public String getPlayerName() {
		return player.name;
	}

	@Override
	public Result setMove(String move, int dir) {
		return player.setMove(move,dir);
	}

	public void sendInit(botctf.comms.data.IntialiseFrame frame) {
		StringBuilder sb= new StringBuilder();
		frame.toMessage(sb);
		outputMessageQueue.sendFrame(sb.toString());
	}}

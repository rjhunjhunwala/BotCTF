package botctf.comms.handlers;

import botctf.comms.messagequeue.Result;

public interface InputMessageHandler 
{
	public Result setMove(String move, int dir);
	public void invalidCommand(String resaon);
	public String getPlayerName();
}

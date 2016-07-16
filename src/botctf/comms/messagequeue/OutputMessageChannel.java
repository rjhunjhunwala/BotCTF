package botctf.comms.messagequeue;

import java.io.IOException;
import java.io.OutputStream;

public class OutputMessageChannel
{
	OutputStream os;

	String currentFrame;
	
	public OutputMessageChannel(OutputStream os)
	{
		this.os = os;
	}
	
	public void sendMessage(String message)
	{
		if (message!=null)
		{
			try
			{
				os.write(message.getBytes());
				os.write('\n');
				os.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
				System.err.println("Lost connection to Player");
			}
		}
	}
	
	public void sendFrame(String frame)
	{
		sendMessage(frame);
	}

}

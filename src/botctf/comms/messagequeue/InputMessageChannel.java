package botctf.comms.messagequeue;

import java.io.IOException;
import java.io.InputStream;

import botctf.comms.handlers.InputMessageHandler;

public class InputMessageChannel
{
	InputStream is;
	public boolean alive=true;
	InputMessageHandler handler;
	
	public InputMessageChannel(InputStream is, InputMessageHandler handler)
	{
		this.is = is;
		this.handler=handler;
	}
	
	public boolean processNextMessage()
	{
		String cmd = null;
		try {
			cmd = readLine(is);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		if (cmd !=null)
		{
			cmd = cmd.replace("\r", "");
			String[] tokens = cmd.split(",");
			if (tokens.length!=2)
			{
				handler.invalidCommand("Command should be \"<Move>,<int>\" where <Move> is {Move|Defuse|Mine|Throw} and <int> is an integer between 0-4");
				return false;
			}
			else
			{
				try
				{
					Result result = handler.setMove(tokens[0],Integer.parseInt(tokens[1]));
					
					if (result !=null)
					{
						if (!result.isSuccess())
						{
							handler.invalidCommand(result.getReason());
							return false;
						}
					}
					else
					{
						throw new Exception("No valid result was returned when processing Command ("+cmd+") recieved from player " +handler.getPlayerName());
					}
				} catch (Throwable e)
				{
					handler.invalidCommand(e.getMessage());
					return false;
				}
			}
		}
		else
		{
			return false;
		}
		return true;
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

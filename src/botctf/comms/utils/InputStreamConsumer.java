package botctf.comms.utils;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamConsumer 
{
	public interface InputReceivedCallback
	{
		void inputReceieved(int input);
	}
	
	public InputStreamConsumer(InputStream is, InputReceivedCallback callback)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				int read;
				try
				{
					read = is.read();
					callback.inputReceieved(read);
					
					while(read > -1)
					{
						read = is.read();
						callback.inputReceieved(read);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
}

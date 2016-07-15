import java.io.IOException;
import java.io.InputStream;

public class TestPlayer {

	
	public static void main(String[] args) throws Exception
	{
		System.err.println("TEST PLAYER STARTED");
		
		while(true)
		{
			String line=readLine(System.in);
			System.err.println(line);
			System.out.println("Move,2");
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

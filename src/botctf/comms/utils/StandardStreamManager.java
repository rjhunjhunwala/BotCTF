package botctf.comms.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Manages Standard streams to allow per-thread std in and std out
 * @author nklaebe
 *
 */
public class StandardStreamManager
{
	static public PrintStream stdOut;
	static public InputStream stdIn;
	private static boolean initialised;
	
	static public void intialise()
	{
		if (!initialised)
		{
			// use custom std streams to allow per thread stream redirection
			StandardStreamManager.stdIn=System.in;
			StandardStreamManager.stdOut=System.out;
			System.setIn(StandardStreamManager.getInputStreamRedirector());
			System.setOut(new PrintStream(StandardStreamManager.getOutputStreamRedirector()));
			
			initialised=true;
		}
	}
	
	static private InputStream getInputStream()
	{
		Streams streams = registry.get(Thread.currentThread());
		if (streams == null)
		{
			return stdIn;
		}
		else
		{
			return streams.is;
		}
	}
	
	static private OutputStream getOutputStream()
	{
		Streams streams = registry.get(Thread.currentThread());
		if (streams == null)
		{
			return stdOut;
		}
		else
		{
			return streams.os;
		}
	}
	
	static private class Streams
	{
		public Streams(InputStream inputStreamRedirectWorker,
				OutputStream outputStreamRedirectWorker) {
			this.is=inputStreamRedirectWorker;
			this.os=outputStreamRedirectWorker;
		}
		InputStream is;
		OutputStream os;
	}
	static HashMap<Thread, Streams> registry = new HashMap<>();
	
	static public void register(InputStream is, OutputStream os)
	{
		registry.put(Thread.currentThread(), new Streams(is,os));
	}

	public static InputStream getInputStreamRedirector() {

		//synchronized(registry)
//		{
			return new InputStream() {
				
				@Override
				public int read() throws IOException {
					InputStream is = getInputStream();
					return is.read();
				}
			};
//		}
	}

	public static OutputStream getOutputStreamRedirector() {

		//synchronized(registry)
//		{
			return new OutputStream() {
				
				@Override
				public void write(int data) throws IOException {
					getOutputStream().write(data);
				}
			};
//		}
	}
	
	
}

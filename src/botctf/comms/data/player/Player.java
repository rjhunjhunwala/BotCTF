package botctf.comms.data.player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import botctf.Bot;
import botctf.Move;
import botctf.Move.MoveType;
import botctf.comms.data.GameFrame;
import botctf.comms.data.IntialiseFrame;
import botctf.comms.messagequeue.PlayerCommunication;
import botctf.comms.messagequeue.Result;
import botctf.comms.utils.StandardStreamManager;

public class Player extends Bot 
{

	public static boolean RUN_IN_SAME_VM = true;
	private static int port=14000;

	public int id;
	public double money;
	public double incomeRate;
	public int powerUpTokens;

	public PlayerCommunication communications;

	public String name;
	private Move moveResult;
	
	private static int idCount = 0;
	
	public Player(String commandline,int x, int y, int team)
	{
		super(x,y,team);
		this.id = idCount++;
		
		InputStream stdin;
		OutputStream stdout;
		InputStream stderr;
		
		 List<String> command = Arrays.asList(commandline.split(" "));
		 name=command.get(1);
		
		if (!(RUN_IN_SAME_VM && command.get(0).equals("java")) )
	   	{
			ProcessBuilder pb = new ProcessBuilder(command);
			Process proc = null;
			try {
				proc = pb.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stdin = proc.getInputStream();
			stderr = proc.getErrorStream();
			stdout = proc.getOutputStream();
			communications= new PlayerCommunication(stdin, stdout,this);
	   	}
	   	else
	   	{
	   		final int thisPort = port++; 
	   		Thread thread = new Thread(new Runnable() 
	   		{
					
				@Override
				public void run() {
					ServerSocket ss=null;
					try
					{
						ss = new ServerSocket(thisPort);
						Socket socket = ss.accept();
						StandardStreamManager.intialise();
						StandardStreamManager.register(socket.getInputStream(),socket.getOutputStream());

						Class playerClass;
						playerClass = getClass().getClassLoader().loadClass(command.get(1));
			   			String[] commands = command.subList(2, command.size()).toArray(new String[0]);
			   			Method method = playerClass.getMethod("main", new String[commands.length].getClass());
			   			Object[] args = new Object[1];
			   			args[0] = commands;
			   			method.invoke(null,args);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						if (ss!=null)
							try {
								ss.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}
			});
	   		thread.start();
	   		
	   		int retries=0;
	   		while (retries++<200)
	   		{
	   			try
	   			{
		   			Socket socket = new Socket("127.0.0.1", thisPort);
		   			communications= new PlayerCommunication(socket.getInputStream(), socket.getOutputStream(), Player.this);
		   			sendInit();
		   			break;
	   			} catch (IOException e)
	   			{
	   				try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	   			}
	   		}
		}
	}

	public Player(String string, int x, int y, int team, String name) {
		this(string,x,y,team);
		this.name = name;
	}
	
	private void sendInit()
	{
		IntialiseFrame frame = new IntialiseFrame(this);
		communications.sendInit(frame);
	}

	@Override
	public Move move() {
		
		GameFrame frame = new GameFrame(this);
		communications.sendFrame(frame);
		communications.processNextMessage();

		if (moveResult == null)
		{
			System.out.println("Bot failed to relpy in time... NOP move applied.");
			moveResult= new Move(4, MoveType.Defuse);
		}
		return moveResult;
	}

	public Result setMove(String moveStr, int dir) 
	{
		Result result = new Result(true, null); 
		Move move= null;
		try
		{
			if (dir < 0 )
			{
				result = new Result(false, "invalid direction: " +dir + " . Direction should be between 0 and 4");
			}
			move = new Move(dir, MoveType.valueOf(moveStr));
		} catch (Exception e)
		{
			e.printStackTrace();
			result = new Result(false, "invalid move: " +moveStr);
		}

		moveResult = move;
		
		return result;
	}
}

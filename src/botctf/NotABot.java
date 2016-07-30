/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package botctf;

import botctf.Move.*;
import java.util.Scanner;

/**
 *
 * @author rohan
 */
public class NotABot extends Bot {

	public NotABot(int inX, int inY, int inTeam) {
		super(inX, inY, inTeam);
	}
	@Override
	public Move move(){
	Scanner s = new Scanner(System.in);
	System.out.print("Move:");
	String move = s.nextLine();
	int dir=Integer.parseInt(""+move.charAt(0));
	MoveType t = MoveType.Defuse;
	switch(move.charAt(1)){
		case 'm':
			t = MoveType.Move;
			break;
		case 'd':
			t = MoveType.Defuse;
			break;
		case 'i':
			t = MoveType.Mine;
			break;
		case 't':
			t = MoveType.Throw;
	}
	return new Move(dir,t);
	}
	
	@Override
	public void setFrame(int frame ){
	}
}

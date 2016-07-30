/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package botctf;

import static botctf.Move.MoveType.*;

/**
 *
 * @author rohan
 */
public class NopBot extends Bot {

	public NopBot(int inX, int inY, int inTeam) {
		super(inX, inY, inTeam);
	}
	@Override
	public Move move(){
		return new Move(5,Defuse);
	}
	
	@Override
	public void setFrame(int frame ){
	}
}

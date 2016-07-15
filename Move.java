/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package botctf;

/**
 *
 * @author rohan
 */
public class Move {

	/**
	 * From 0-3
	 *
	 *
	 *  3
	 * 2 0
	 *  1
	 *
	 *
	 * Anything more or less is a no -op
	 */
	public final int direction;

	public static enum MoveType {

		Move,
		Defuse,
		Mine,
		Throw;
	}
	public final MoveType moveType;

	public Move(int inDir, MoveType t) {
		moveType = t;
		direction = inDir;
	}
}

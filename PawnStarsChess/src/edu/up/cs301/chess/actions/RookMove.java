/**
 * 
 */
package edu.up.cs301.chess.actions;

import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.game.GamePlayer;

/**
 * A move from a rook that can castle
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 *
 */
public class RookMove extends ChessMoveAction {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -1949275900587108452L;
	
	//the types of special moves for rooks
	public static final int CASTLE_RIGHT = 1;
	public static final int CASTLE_LEFT = 2;
	public static final int NONE = 3;
	
	//the type of this move
	private int type;
	
	/**
	 * Default constructor for RookMove, does what ChessMoveAction does
	 */
	public RookMove(GamePlayer player, ChessPiece whichPiece, int[] newPos, ChessPiece takenPiece, int type) {
		super(player, whichPiece, newPos, takenPiece);
		this.type = type;
	}
	
	/**
	 * Returns the type of the move
	 * @return type
	 */
	public int getType() {
		return type;
	}
}

/**
 * 
 */
package edu.up.cs301.chess.actions;

import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.game.GamePlayer;

/**
 * An action that contains the different ways a pawn can move
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 *
 */
public class PawnMove extends ChessMoveAction {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -1949275900587108452L;
	
	//The types of special moves for pawns
	public final int EN_PASSANT = 0;
	public final int PROMOTION = 1;
	public final int FIRST_MOVE = 2;
	public final int NONE = 3;
	
	public final static int NUM_PAWN_ATTACKS_NORMAL = 2;
	
	// The type of this move
	private int type;
	
	/**
	 * Default constructor
	 */
	public PawnMove(GamePlayer player, ChessPiece whichPiece, int[] newPos, ChessPiece takenPiece, int type) {
		super(player, whichPiece, newPos, takenPiece);
		this.type = type;
	}

	/**
	 * Returns this move's type
	 * @return
	 */
	public int getType() {
		return type;
	}
}

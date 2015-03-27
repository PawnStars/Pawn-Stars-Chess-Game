/**
 * 
 */
package edu.up.cs301.chess.actions;

import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * An action that describes when a player does a promotion
 * and chooses the new type of piece it should be
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 *
 */
public class SelectUpgradeAction extends GameAction {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -1949275900587108452L;
	
	//the current piece
	private ChessPiece piece;
	
	/**
	 * Default constructor
	 * @param Player the Player who made the move
	 * @param ChessPiece the piece being promoted
	 * @param int the type of piece the pawn will become
	 */
	public SelectUpgradeAction(GamePlayer player, ChessPiece currentPiece) {
		super(player);
		//TODO check if it is the right player
		this.piece = currentPiece;
	}

	/**
	 * Get the piece to be upgraded
	 * @return
	 */
	public ChessPiece getPiece() {
		return piece;
	}

	/**
	 * Set the new type of the piece
	 * @param the type of the new piece
	 */
	public void setType(int type) {
		piece.setType(type);
	}
}

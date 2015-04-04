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
 * @version April 2015
 *
 */
public class SelectUpgradeAction extends GameAction {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -1949275900587108452L;
	
	//the current piece
	private ChessPiece piece;
	
	//its new type
	private int type;
	
	/**
	 * Default constructor
	 * @param Player the Player who made the move
	 * @param ChessPiece the piece being promoted
	 * @param int the type of piece the pawn will become
	 */
	public SelectUpgradeAction(GamePlayer player, ChessPiece currentPiece, int type) {
		super(player);
		//TODO check if it is the right player
		this.piece = currentPiece;
		this.type = type;
	}

	/**
	 * Get the piece to be upgraded
	 * @return
	 */
	public ChessPiece getPiece() {
		return piece;
	}
	
	/**
	 * Returns the type the player selected
	 * @return the pawn's new type
	 */
	public int getType()
	{
		return type;
	}
}

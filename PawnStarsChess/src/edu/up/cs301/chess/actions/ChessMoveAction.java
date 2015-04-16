package edu.up.cs301.chess.actions;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.chess.ChessPlayer;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * A ChessMoveAction is an action that is a "move" in the game.
 * A piece or pieces are moved on the board and may take opponent pieces.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 */
public class ChessMoveAction extends GameAction {
	
	// to satisfy the serializable interface
	private static final long serialVersionUID = 2806249547013L;
	
	//The piece moved by the player
	private ChessPiece whichPiece;
	
	//The piece to be taken
	private ChessPiece takenPiece;
	
	//The new location of the selected piece
	private int[] newPos;
	
	//The old location of the selected piece
	private int[] oldPos;
	
	//TODO could make it possible to undo a turn with this info and the last position
	
	/**
	 * Constructor for the ChessMoveAction class.
	 * 
	 * @param player
	 *            the player making the move
	 * @param whichPlayer
	 *            value to initialize which player is white
	 */
	public ChessMoveAction(GamePlayer player, ChessPiece whichPiece, int[] newPos, ChessPiece takenPiece)
	{
		super(player);
		
		this.takenPiece = takenPiece;
		this.whichPiece = whichPiece;
		this.newPos = newPos.clone();
		this.oldPos = whichPiece.getLocation().clone();
	}

	/**
	 * Copy constructor for adding a player to an invalid ChessMoveAction
	 * @param player
	 * @param action
	 */
	public ChessMoveAction(GamePlayer player, ChessMoveAction action) {
		super(player);
		
		this.whichPiece = action.getWhichPiece();
		this.newPos = action.getNewPos();
		this.takenPiece = action.getTakenPiece();
		this.oldPos = whichPiece.getLocation().clone();
	}

	/**
	 * Gets the piece to be moved
	 * @return ChessPiece the piece that will move
	 */
	public ChessPiece getWhichPiece()
	{
		return whichPiece;
	}

	/**
	 * Gets the piece to be taken
	 * @return ChessPiece the piece that will be captured
	 */
	public ChessPiece getTakenPiece() {
		return takenPiece;
	}

	/**
	 * Gets the new location of the piece
	 * @return an int array of size 2 with the new position of the ChessPiece
	 */
	public int[] getNewPos() {
		return newPos;
	}
	
	public int[] getOldPos() {
		return oldPos;
	}

	/**
	 * Convert the move into standard chess notation for debugging.
	 */
	@Override
	public String toString()
	{
		String rtnVal = "";
		rtnVal += whichPiece.toCharacter();
		int[] loc = whichPiece.getLocation();
		
		//convert to chess notation
		//TODO doesn't work for special moves
		rtnVal += (char)(97+loc[1]);
		rtnVal += ChessGameState.BOARD_HEIGHT-loc[0];
		rtnVal += " ";
		
		rtnVal += (char)(97+newPos[1]);
		rtnVal += ChessGameState.BOARD_HEIGHT-newPos[0];
		rtnVal += " ";
		return rtnVal;
	}
}//class CounterMoveAction

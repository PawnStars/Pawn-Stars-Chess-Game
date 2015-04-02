package edu.up.cs301.chess.actions;

import java.util.Arrays;

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
	private static final long serialVersionUID = 28062497013L;
	
	//The piece moved by the player
	private ChessPiece whichPiece;
	
	//The piece to be taken
	private ChessPiece takenPiece;
	
	//The new location of the selected piece
	private int[] newPos;
	
	//Color of the mover
	private boolean whichColor;
	
	//Whether or not the move is valid
	private boolean valid;
	
	//TODO could make it possible to undo a turn with this info and the last position
	
	/**
	 * Constructor for the ChessMoveAction class.
	 * 
	 * @param player
	 *            the player making the move
	 * @param whichPlayer
	 *            value to initialize which player is white
	 */
	public ChessMoveAction(GamePlayer player, ChessPiece whichPiece, int[] newPos, ChessPiece takenPiece) {
		super(player);
		valid = false;
		if(player instanceof ChessPlayer)
		{
			if(takenPiece == null)
			{
				valid = true;
			}
			else
			{
				whichColor = ((ChessPlayer)player).isWhite();
				if(whichColor != takenPiece.isWhite())
				{
					valid = true;
				}
			}
		}
		
		this.whichPiece = whichPiece;
		this.newPos = newPos;
		this.takenPiece = takenPiece;
	}
	
	/**
	 * Constructor for the ChessMoveAction class.
	 * 
	 * @param player
	 *            the player making the move
	 * @param whichPiece
	 * 			the piece the player moved
	 * @param newPos
	 * 			position the player is trying to move the piece to
	 */
	public ChessMoveAction(GamePlayer player, ChessPiece whichPiece, int[] newPos) {
		super(player);
		valid = false;
		if(player instanceof ChessPlayer)
		{
			if(takenPiece == null)
			{
				valid = true;
			}
			else
			{
				whichColor = ((ChessPlayer)player).isWhite();
				if(whichColor != takenPiece.isWhite())
				{
					valid = true;
				}
			}
		}
		
		this.whichPiece = whichPiece;
		this.newPos = newPos;
	}
	
	/**
	 * Copy constructor for adding a player to an invalid ChessMoveAction
	 * @param player
	 * @param action
	 */
	public ChessMoveAction(GamePlayer player, ChessMoveAction action) {
		super(player);
		valid = false;
		if(player instanceof ChessPlayer)
		{
			if(takenPiece == null)
			{
				valid = true;
			}
			else
			{
				whichColor = ((ChessPlayer)player).isWhite();
				if(whichColor != action.getTakenPiece().isWhite())
				{
					valid = true;
				}
			}
		}
		
		this.whichPiece = action.getWhichPiece();
		this.newPos = action.getNewPos();
		this.takenPiece = action.getTakenPiece();
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
	
	/**
	 * Returns true if the move is a move that can be applied
	 * @return true if valid
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 * Convert the move into standard chess notation for debugging.
	 */
	@Override
	public String toString()
	{
		String rtnVal = "";
		rtnVal += whichPiece.toString();
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

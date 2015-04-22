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
	private static final long serialVersionUID = 2806249547013L;
	
	//The piece moved by the player
	protected ChessPiece whichPiece;
	
	//The piece to be taken
	protected ChessPiece takenPiece;
	
	//The new location of the selected piece
	protected int[] newPos;
	
	//The old location of the selected piece
	protected int[] oldPos;
	
	protected boolean makesCheck;
	
	protected boolean makesCheckmate;
	
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
		
		if(takenPiece != null)
		{
			this.takenPiece = new ChessPiece(takenPiece);
		}
		else
		{
			this.takenPiece = null;
		}
		
		this.whichPiece = new ChessPiece(whichPiece);
		this.newPos = Arrays.copyOf(newPos,2);
		this.oldPos = Arrays.copyOf(whichPiece.getLocation(),2);
		makesCheck = false;
		makesCheckmate = false;
	}

	/**
	 * Copy constructor for adding a player to an invalid ChessMoveAction
	 * @param player
	 * @param action
	 */
	public ChessMoveAction(GamePlayer player, ChessMoveAction action) {
		super(player);
		
		if(takenPiece != null)
		{
			this.takenPiece = new ChessPiece(takenPiece);
		}
		else
		{
			this.takenPiece = null;
		}
		
		this.whichPiece = new ChessPiece(action.getWhichPiece());
		this.newPos = Arrays.copyOf(action.getNewPos(),2);
		this.oldPos = Arrays.copyOf(action.getOldPos(),2);
		makesCheck = action.isMakesCheck();
		makesCheckmate = action.isMakesCheckmate();
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

	public boolean isMakesCheck() {
		return makesCheck;
	}

	public void setMakesCheck(boolean makesCheck) {
		this.makesCheck = makesCheck;
	}

	public boolean isMakesCheckmate() {
		return makesCheckmate;
	}

	public void setMakesCheckmate(boolean makesCheckmate) {
		this.makesCheckmate = makesCheckmate;
	}

	/**
	 * Convert the move into standard chess notation for debugging.
	 */
	@Override
	public String toString()
	{
		String msg = "";
		int pieceType = whichPiece.getType();
		if(pieceType == ChessPiece.QUEEN)
		{
			msg += "Q";
		}
		if(pieceType == ChessPiece.KING)
		{
			msg += "K";
		}
		if(pieceType == ChessPiece.ROOK)
		{
			msg += "R";
		}
		if(pieceType == ChessPiece.BISHOP)
		{
			msg += "B";
		}
		if(pieceType == ChessPiece.KNIGHT)
		{
			msg += "N";
		}
		if(takenPiece != null)
		{
			msg += "x";
		}
		msg += (char)(97+oldPos[1]);
		msg += ChessGameState.BOARD_HEIGHT-oldPos[0];
		msg += (char)(97+newPos[1]);
		msg += ChessGameState.BOARD_HEIGHT-newPos[0];
		
		if(makesCheck)
		{
			msg += "+";
		}
		else if(makesCheckmate)
		{
			msg += "#";
		}
		
		return msg;
	}
	
	public ChessMoveAction clone()
	{
		//
		return new ChessMoveAction(null,this);
	}
}//class CounterMoveAction

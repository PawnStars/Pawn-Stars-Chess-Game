package edu.up.cs301.chess.actions;

import java.util.Arrays;
import java.util.Locale;

import android.util.Log;

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
		//TODO: this caused a null pointer exception... 
		//may need to fix after we implement checkmate properly
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
		return new ChessMoveAction(super.getPlayer(),this);
	}
	
	public static ChessMoveAction moveTextToAction(ChessGameState state,ChessPlayer player, String text)
	{
		if(text == null || text.equals("(none)"))
		{
			return null;
		}
		
		//ensure lower case so capital letters don't affect character arithmetic
		text = text.toLowerCase(Locale.US);
		
		//TODO use isCheck and checkmate
		//find out if the move will put the other player in check or checkmate
		boolean isCheck = false;
		boolean isCheckmate = false;
		char lastChar = text.charAt(text.length()-1);
		if(lastChar == '+')
		{
			text = text.substring(0, text.length()-2);
			isCheck = true;
		}
		else if(lastChar == '#')
		{
			text = text.substring(0, text.length()-2);
			isCheckmate = true;
		}
		
		boolean enPassant = false;
		boolean rightCastle = false;
		boolean leftCastle = false;
		int promotion = -1;
		if(text.contains("e.p."))
		{
			enPassant = true;
		}
		else if(text.contains("/"))
		{
			char type = text.charAt(text.indexOf('/')+1);
			type = Character.toLowerCase(type);
			if(type == 'q')
			{
				promotion = ChessPiece.QUEEN;
			}
			else if(type == 'r')
			{
				promotion = ChessPiece.ROOK;
			}
			else if(type == 'n')
			{
				promotion = ChessPiece.KNIGHT;
			}
			else if(type == 'b')
			{
				promotion = ChessPiece.BISHOP;
			}
		}
		else if(text.contains("0-0"))
		{
			if(text.contains("0-0-0"))
			{
				leftCastle = true;
			}
			else
			{
				rightCastle = true;
			}
		}
		
		
		
		int oldX = text.charAt(text.length()-4)-97;
		int oldY = ChessGameState.BOARD_HEIGHT-text.charAt(text.length()-3)+48;
		int newX = text.charAt(text.length()-2)-97;
		int newY = ChessGameState.BOARD_HEIGHT-text.charAt(text.length()-1)+48;
		
		Log.d("computer player","x:"+oldX+" y:"+oldY+" newX:"+newX+" newY:"+newY);
		
		int[] newLoc = new int[]{newY,newX};
		
		ChessMoveAction move = null;
		if(!ChessGameState.outOfBounds(newLoc) && !ChessGameState.outOfBounds(oldX,oldY))
		{
			ChessPiece whichPiece = state.getPieceMap()[oldY][oldX];
			ChessPiece takenPiece = state.getPieceMap()[newY][newX];
			if(promotion != -1)
			{
				//TODO implement taken piece
				//int[] takenLoc = state.
				move = new PawnMove(player,whichPiece,newLoc,null,PawnMove.PROMOTION);
				((PawnMove)move).setNewType(promotion);
			}
			else if(enPassant)
			{
				
			}
			else if(leftCastle || rightCastle)
			{
				
			}
			else
			{
				move = new ChessMoveAction(player,whichPiece,newLoc,takenPiece);
			}
			//TODO make special moves work
		}
		return move;
	}
}//class CounterMoveAction

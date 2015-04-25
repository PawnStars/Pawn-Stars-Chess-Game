/**
 * 
 */
package edu.up.cs301.chess.actions;

import java.io.Serializable;

import edu.up.cs301.chess.ChessGameState;
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
public class PawnMove extends ChessMoveAction implements Serializable {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -1949275901587108452L;
	
	//The types of special moves for pawns
	public static final byte EN_PASSANT = 0;
	public static final byte PROMOTION = 1;
	public static final byte FIRST_MOVE = 2;
	public static final byte NONE = 3;
	
	public final static int NUM_PAWN_ATTACKS_NORMAL = 2;
	
	// The type of this move
	private byte type;
	
	private byte newType;
	
	/**
	 * Default constructor
	 */
	public PawnMove(GamePlayer player, ChessPiece whichPiece, byte[] newPos, ChessPiece takenPiece, byte type) {
		super(player, whichPiece, newPos, takenPiece);
		this.type = type;
	}
	
	/**
	 * Copy constructor
	 * @param player
	 * @param move
	 */
	public PawnMove(GamePlayer player, PawnMove move)
	{
		super(player, move.getWhichPiece(), move.getNewPos(), move.getTakenPiece());
		this.type = move.getType();
		this.newType = move.getNewType();
	}

	/**
	 * Returns this move's type
	 * @return
	 */
	public byte getType() {
		return type;
	}

	public byte getNewType() {
		return newType;
	}

	public void setNewType(byte newType) {
		this.newType = newType;
	}
	
	@Override
	public String toString()
	{
		if(type == PROMOTION)
		{
			String rtnVal = "";
			rtnVal += (char)(97+whichPiece.getLocation()[1]);
			rtnVal += ChessGameState.BOARD_HEIGHT-whichPiece.getLocation()[0];
			rtnVal += "=";
			if(newType == ChessPiece.QUEEN)
			{
				rtnVal += "Q";
			}
			if(newType == ChessPiece.ROOK)
			{
				rtnVal += "R";
			}
			if(newType == ChessPiece.BISHOP)
			{
				rtnVal += "B";
			}
			if(newType == ChessPiece.KNIGHT)
			{
				rtnVal += "N";
			}
			
			if(makesCheck)
			{
				rtnVal += "+";
			}
			else if(makesCheckmate)
			{
				rtnVal += "#";
			}
			
			return rtnVal;
		}
		else
		{
			return super.toString();
		}
	}
	
	public PawnMove clone()
	{
		//super.getPlayer();
		return new PawnMove(null,this);
	}
	
}

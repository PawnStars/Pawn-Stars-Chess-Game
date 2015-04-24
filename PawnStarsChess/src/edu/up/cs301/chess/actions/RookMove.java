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
	public static final byte CASTLE_RIGHT = 1;
	public static final byte CASTLE_LEFT = 0;
	public static final byte NONE = 2;
	
	//the type of this move
	private byte type;
	
	/**
	 * Default constructor for RookMove, does what ChessMoveAction does
	 */
	public RookMove(GamePlayer player, ChessPiece whichPiece, byte[] newPos, ChessPiece takenPiece, byte type) {
		super(player, whichPiece, newPos, takenPiece);
		this.type = type;
	}
	
	/**
	 * Copy constructor
	 * @param player
	 * @param move
	 */
	public RookMove(GamePlayer player, RookMove move)
	{
		super(player, move.getWhichPiece(), move.getNewPos(), move.getTakenPiece());
		this.type = move.getType();
	}
	
	
	
	/**
	 * Returns the type of the move
	 * @return type
	 */
	public byte getType() {
		return type;
	}
	
	@Override
	public String toString()
	{
		if(type == CASTLE_LEFT)
		{
			return "O-O-O ";
		}
		else if(type == CASTLE_RIGHT)
		{
			return "O-O";
		}
		else
		{
			return super.toString();
		}
	}
	
	public RookMove clone()
	{
		//super.getPlayer()
		return new RookMove(null,this);
	}
	
}

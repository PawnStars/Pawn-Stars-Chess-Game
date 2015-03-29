package edu.up.cs301.chess.engine;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPiece;
import edu.up.cs301.chess.actions.ChessMoveAction;

/**
 * This generates a list of moves that the AI will use to make a move.
 * .
 * @author Allison Liedtke
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @version March 2015
 *
 */
public class MoveGenerator {
	
	public static final ChessMoveAction[] getPossibleMoves(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public static final ChessMoveAction[] getEvasions(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public static final ChessMoveAction[] getCapturesAndChecks(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public static final ChessMoveAction[] getCaptures(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public static final boolean isInCheck(ChessGameState state, boolean whichPlayer)
	{
		return false;
	}
	
	public static final boolean givesCheck(ChessGameState state, boolean whichPlayer, ChessMoveAction move)
	{
		return false;
	}
	
	public static final boolean canTakeKing(ChessGameState state, boolean whichPlayer)
	{
		return false;
	}
	
	public static final ChessMoveAction[] getPieceMoves(ChessGameState state, ChessPiece piece)
	{
		int type = piece.getType();
		if(type == ChessPiece.PAWN)
		{
			
		}
		else if(type == ChessPiece.ROOK)
		{
			
		}
		else if(type == ChessPiece.QUEEN)
		{
			
		}
		else if(type == ChessPiece.KNIGHT)
		{
			
		}
		else if(type == ChessPiece.KING)
		{
			
		}
		else if(type == ChessPiece.BISHOP)
		{
			
		}
		return null;
	}
	
	/**
	 * You must defend yourself from check threats, so this function removes
	 * all moves that do not protect the king.
	 * 
	 * @param state
	 * @param moves
	 * @return a list of legal moves
	 */
	public final ChessMoveAction[] removeIllegalMoves(ChessGameState state, ChessMoveAction[] moves)
	{
		return moves;
	}
	
	
}

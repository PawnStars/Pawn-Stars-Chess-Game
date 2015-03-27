package edu.up.cs301.chess.engine;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.actions.ChessMoveAction;

/**
 * This generates a list of moves that the AI will use to make a move.
 * .
 * @authors Allison Liedtke
 * 		    Anthony Donaldson
 * 		    Derek Schumacher
 * 			Scott Rowland
 * @version March 2015
 *
 */
public class MoveGenerator {
	
	public final ChessMoveAction[] getPossibleMoves(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public final ChessMoveAction[] getEvasions(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public final ChessMoveAction[] getCapturesAndChecks(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public final ChessMoveAction[] getCaptures(ChessGameState state, boolean whichPlayer)
	{
		return null;
	}
	
	public final boolean isInCheck(ChessGameState state, boolean whichPlayer)
	{
		return false;
	}
	
	public final boolean givesCheck(ChessGameState state, boolean whichPlayer, ChessMoveAction move)
	{
		return false;
	}
	
	public final boolean canTakeKing(ChessGameState state, boolean whichPlayer)
	{
		return false;
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

package edu.up.cs301.chess.engine;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.actions.ChessMoveAction;

/**
 * This looks for a move based on the intelligence of the AI.
 * 
 * @author Allison Liedtke
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @version March 2015
 *
 */
public class Search {
	//the depth of the recursive search
	private int maxDepth = 3;
	
	//The maximum amount of time to take for a search.
	private long maxTime;
	/**
	 * Chooses a move based on intelligence.
	 * 
	 * @param intelligence
	 * @return
	 */
	public ChessMoveAction findMove(int intelligence)
	{
		return null;
	}
	
	/**
	 * Orders the possible moves according to potential score increases. 
	 * It generates a search tree of game states and takes their average
	 * score. It then sorts the possible moves by favorability to the
	 * player whose turn it is.
	 * 
	 * @param the total depth of the search tree
	 * @param state the current ChessGameState
	 * @return a ChessMoveAction favorable to 
	 */
	private ChessMoveAction[] alphaBeta(int depth, ChessGameState state)
	{
		return null;
	}
	
	/**
	 * Helps the main alphaBeta() method
	 * @param depth the remaining depth of the search tree
	 * @param state the current ChessGameState
	 * @param alpha the max score assured for player 1 for a given move
	 * @param beta the min score assured for player 2 for a given move
	 * @return
	 */
	private ChessMoveAction[] alphaBetaHelper(int depth, ChessGameState state, float alpha, float beta, boolean maxPlayer)
	{
		return null;
	}
	
	/**
	 * Does alpha beta searches until it runs out of time.
	 * 
	 * @param state
	 * @return
	 */
	private ChessMoveAction iterativeDeepening(ChessGameState state)
	{
		return null;
	}
}

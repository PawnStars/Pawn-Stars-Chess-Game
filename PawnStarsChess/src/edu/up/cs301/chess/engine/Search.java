package edu.up.cs301.chess.engine;

import edu.up.cs301.chess.ChessGameState;
import edu.up.cs301.chess.ChessPlayer;
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
	public static final ChessMoveAction findMove(ChessPlayer player,ChessGameState state,int intelligence)
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
	private static final ChessMoveAction[] negaScout(int depth, ChessGameState state)
	{
		//TODo finish
		float score = negaScoutHelper(depth,state,Float.POSITIVE_INFINITY,Float.NEGATIVE_INFINITY,state.isWhoseTurn());
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
	private static final float negaScoutHelper(int depth, ChessGameState state, float alpha, float beta, boolean maxPlayer)
	{
		if(state.isGameOver() || depth == 0)
		{
			if(maxPlayer == true)
			{
				return Evaluator.evalulate(state);
			}
			else
			{
				return -(Evaluator.evalulate(state));
			}
		}
		//will make invalid ChessMoveActions
		ChessMoveAction[] possibleMoves = MoveGenerator.getPossibleMoves(state, null);
		
		boolean first = true;
		float score = 0;
		for(ChessMoveAction move: possibleMoves)
		{
			if(first)
			{
				ChessGameState newState = new ChessGameState(state);
				state.applyMove(move);
				score -=negaScoutHelper(depth-1, newState, -beta, -alpha, !maxPlayer);
				first = false;
			}
			else
			{
				ChessGameState newState = new ChessGameState(state);
				state.applyMove(move);
				score -=negaScoutHelper(depth-1, newState, -alpha-1, -alpha, !maxPlayer);
				if(alpha < score && score < beta)
				{
					score -=negaScoutHelper(depth-1, newState, -beta, -score, !maxPlayer);
				}
			}
			alpha = Math.max(alpha, score);
			if(alpha >= beta)
			{
				break;
			}
		}
		return alpha;
	}
	
	/**
	 * Does alpha beta searches until it runs out of time.
	 * 
	 * @param state
	 * @return
	 */
	private static final ChessMoveAction iterativeDeepening(ChessGameState state)
	{
		return null;
	}
}

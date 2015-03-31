package edu.up.cs301.chess.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
	private static int maxDepth = 4;
	
	//The maximum amount of time to take for a search.
	private static long maxTime = 15000;
	
	public static final int MAX_INTELLIGENCE = 10;
	/**
	 * Chooses a move based on intelligence.
	 * 
	 * @param intelligence
	 * @return
	 */
	public static final ChessMoveAction findMove(ChessPlayer player,ChessGameState state,int intelligence)
	{
		
		if(intelligence > MAX_INTELLIGENCE)
		{
			intelligence = MAX_INTELLIGENCE;
		}
		if(intelligence < 0)
		{
			intelligence = 0;
		}
		
		//dynamic depth so the calculation does not take as long for weak AIs
		int depth = (intelligence*maxDepth/MAX_INTELLIGENCE);
		
		ChessMoveAction[] sortedMoves = iterativeDeepening(state,depth);
		
		//might want to choose the best move, might not
		int index = (sortedMoves.length-1)*intelligence/MAX_INTELLIGENCE;
		//int index = sortedMoves.length-1;
		
		//make a copy of the move with a reference to the player
		ChessMoveAction chosenMove = new ChessMoveAction(player, sortedMoves[index]);
		
		return chosenMove;
	}
	
	/**
	 * Orders the possible moves according to potential score increases. 
	 * It generates a search tree of game states and takes their average
	 * score. It then sorts the possible moves by favorability to the
	 * player whose turn it is.
	 * 
	 * Read more about this algorithm at:
	 * http://en.wikipedia.org/wiki/Principal_variation_search
	 * 
	 * @param the total depth of the search tree
	 * @param state the current ChessGameState
	 * @return a ChessMoveAction favorable to 
	 */
	private static final ChessMoveAction[] negaScout(int depth, ChessGameState state)
	{
		long initTime = System.currentTimeMillis();
		Map<Float,ChessMoveAction> moveMap = new HashMap<Float,ChessMoveAction>();
		Map<Float,ChessMoveAction> lastMoveMap = new HashMap<Float,ChessMoveAction>();
		ChessMoveAction[] possibleMoves = MoveGenerator.getPossibleMoves(state, null);
		
		outerloop:
		for(int d = 0; d<depth;d++)
		{
			moveMap = new HashMap<Float,ChessMoveAction>();
			
			//Calculate the score of each move
			for(ChessMoveAction move: possibleMoves)
			{
				//Apply each move to a new GameState
				ChessGameState newState = new ChessGameState(state);
				newState.applyMove(move);
				
				float score = negaScoutHelper(depth,newState,
						Float.POSITIVE_INFINITY,Float.NEGATIVE_INFINITY,
						state.isWhoseTurn());
				
				/*
				 * Add the moves to a hash map so insertion doesn't take too much effort.
				 * Sort the list just before the method returns so it takes O(n log(n))
				 * time instead of O(n^2) time. The moves could be inserted into a sorted
				 * TreeMap directly, but this seems more efficient.
				 * 
				 * There shouldn't be more than 109 moves possible in one state, so the order
				 * in which this is sorted probably does not matter.
				 */
				
				moveMap.put(Float.valueOf(score), move);
				
				//Reached time limit, so stop the search
				if(System.currentTimeMillis()-initTime > maxTime)
				{
					break outerloop;
				}
			}
			lastMoveMap = moveMap;
		}
		
		//Sort the last successful moveMap
		TreeMap<Float,ChessMoveAction> moveTreeMap = new TreeMap<Float,ChessMoveAction>(lastMoveMap);
		
		//Convert to array
		Collection<ChessMoveAction> values = moveTreeMap.values();
		ChessMoveAction[] sortedList = values.toArray(new ChessMoveAction[values.size()]);
		
		return sortedList;
	}
	
	/**
	 * Helps the main negaScout() method
	 * 
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
		
		//TODO This works best with non random move ordering, so implement if possible
		ChessMoveAction[] possibleMoves = MoveGenerator.getPossibleMoves(state, null);
		
		boolean first = true;
		float score = 0;
		for(ChessMoveAction move: possibleMoves)
		{
			//Apply the move first
			ChessGameState newState = new ChessGameState(state);
			state.applyMove(move);
			
			//Recursively call until depth is finished
			if(first)
			{
				score -=negaScoutHelper(depth-1, newState, -beta, -alpha, !maxPlayer);
				first = false;
			}
			else
			{
				//assume first node is the best move
				score -=negaScoutHelper(depth-1, newState, -alpha-1, -alpha, !maxPlayer);
				
				//it wasn't the best move, so do a normal alpha-beta search
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
	 * Does searches until it runs out of time.
	 * 
	 * @param state
	 * @return
	 */
	private static final ChessMoveAction[] iterativeDeepening(ChessGameState state, int depth)
	{
		long initTime = System.currentTimeMillis();
		Map<Float,ChessMoveAction> moveMap;
		Map<Float,ChessMoveAction> lastMoveMap = new HashMap<Float,ChessMoveAction>();
		ChessMoveAction[] possibleMoves = MoveGenerator.getPossibleMoves(state, null);
		
		outerloop:
		for(int d = 0; d<depth;d++)
		{
			moveMap = new HashMap<Float,ChessMoveAction>();
			
			//Calculate the score of each move
			for(ChessMoveAction move: possibleMoves)
			{
				//Apply each move to a new GameState
				ChessGameState newState = new ChessGameState(state);
				newState.applyMove(move);
				
				float score = negaScoutHelper(depth,newState,
						Float.POSITIVE_INFINITY,Float.NEGATIVE_INFINITY,
						state.isWhoseTurn());
				
				moveMap.put(Float.valueOf(score), move);
				
				//Reached time limit, so reduce the depth of the search to save time
				if(System.currentTimeMillis()-initTime > maxTime)
				{
					break outerloop;
				}
			}
			lastMoveMap = moveMap;
		}
		
		//Sort the moveMap
		TreeMap<Float,ChessMoveAction> moveTreeMap = new TreeMap<Float,ChessMoveAction>(lastMoveMap);
		
		//Convert to array
		Collection<ChessMoveAction> values = moveTreeMap.values();
		ChessMoveAction[] sortedList = values.toArray(new ChessMoveAction[values.size()]);
		return sortedList;
	}
}

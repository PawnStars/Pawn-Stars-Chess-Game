package edu.up.cs301.chess.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import android.util.Log;

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
 * @version April 2015
 *
 */
public class Search {
	//the depth of the recursive search
	private static int maxDepth = 2;
	
	//The maximum amount of time to take for a search.
	private static long maxTime = 30000;//30 seconds
	
	public static final int MAX_INTELLIGENCE = 10;
	/**
	 * Chooses a move based on intelligence.
	 * 
	 * @param intelligence
	 * @return
	 */
	public static final ChessMoveAction findMove(ChessPlayer player,ChessGameState state,int intelligence)
	{
		//make sure the intelligence is within bounds
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
		
		ChessMoveAction[] sortedMoves = negaScout(depth, state, player.isWhite());//iterativeDeepening(state,depth);
		
		if(sortedMoves.length == 0 || sortedMoves == null)
		{
			Log.d("search","did not receive any moves from negascout");
			return null;
		}
		
		//might want to choose the best move, might not
		
		//make sure the move is not null
		int index = (sortedMoves.length-1)*intelligence/MAX_INTELLIGENCE;
		for(int i=index;i>=0;i--)
		{
			if(sortedMoves[i] != null)
			{
				index = i;
				break;
			}
		}
		
		//if index still points to a null move, search the rest of the move list
		if(sortedMoves[index] == null)
		{
			for(int i=index;i<sortedMoves.length;i++)
			{
				if(sortedMoves[i] != null)
				{
					index = i;
					break;
				}
			}
		}
		
		//make a copy of the move with a reference to the player
		ChessMoveAction chosenMove = new ChessMoveAction(player, sortedMoves[index]);
		Log.d("search","Picked this move: "+chosenMove);
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
	private static final ChessMoveAction[] negaScout(int depth, ChessGameState state, boolean color)
	{
		//long initTime = System.currentTimeMillis();//TODO replace with better time keeping system
		
		Map<Integer,ChessMoveAction> moveMap;
		Map<Integer,ChessMoveAction> lastMoveMap = new HashMap<Integer,ChessMoveAction>();
		
		ChessMoveAction[] possibleMoves = MoveGenerator.getPossibleMoves(state, null, color);
		
		//outerloop:
		for(int d = 0; d<depth;d++)
		{
			moveMap = new HashMap<Integer,ChessMoveAction>();
			
			//Calculate the score of each move
			for(ChessMoveAction move: possibleMoves)
			{
				//Apply each move to a new GameState
				ChessGameState newState = new ChessGameState(state);
				newState.applyMove(move);
				
				int score = negaScoutHelper(depth,newState,
						Integer.MIN_VALUE,Integer.MAX_VALUE,
						state.isWhoseTurn(), color);
				
				/*
				 * Add the moves to a hash map so insertion doesn't take too much effort.
				 * Sort the list just before the method returns so it takes O(n log(n))
				 * time instead of O(n^2) time.
				 */
				
				moveMap.put(Integer.valueOf(score), move);
				Log.d("search","Move: "+move+"\nScore: "+score);
				
				//Reached time limit, so stop the search
				/*if(System.currentTimeMillis()-initTime > maxTime)
				{
					break outerloop;
				}*/
			}
			lastMoveMap = moveMap;
		}
		
		//Sort the last successful moveMap
		TreeMap<Integer,ChessMoveAction> moveTreeMap = new TreeMap<Integer,ChessMoveAction>(lastMoveMap);
		
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
	 * @param alpha the min score bound
	 * @param beta the max score bound
	 * @return
	 */
	private static final int negaScoutHelper(int depth, ChessGameState state, int alpha, int beta, boolean maxPlayer, boolean color)
	{
		int score = 0;
		
		//Start evaluating when the max depth is reached
		if(state.isGameOver() || depth == 0)
		{
			if(maxPlayer)
			{
				score = Evaluator.evalulate(state);
			}
			else
			{
				score = -(Evaluator.evalulate(state));
			}
			return score;
		}
		
		//TODO This works best with non random move ordering, so implement if possible
		ChessMoveAction[] possibleMoves = MoveGenerator.getPossibleMoves(state, null, color);
		
		boolean first = true;
		for(ChessMoveAction move: possibleMoves)
		{
			//Apply the move first
			ChessGameState newState = new ChessGameState(state);
			state.applyMove(move);
			
			//Recursively call until depth is finished
			if(first)
			{
				score -=negaScoutHelper(depth-1, newState, -beta, -alpha, !maxPlayer, !color);
				first = false;
			}
			else
			{
				//assume first node is the best move
				score -=negaScoutHelper(depth-1, newState, -alpha-1, -alpha, !maxPlayer, !color);
				
				//it wasn't the best move, so do a normal alpha-beta search
				if(alpha < score && score < beta)
				{
					score -=negaScoutHelper(depth-1, newState, -beta, -score, !maxPlayer, !color);
				}
			}
			alpha = Math.max(alpha, score);
			if(alpha >= beta)
			{
				break;
			}
		}
		
		//Return the score that is assured (lower bound)
		return alpha;
	}
	
	/**
	 * Does searches until it runs out of time.
	 * 
	 * @param state
	 * @return
	 */
	private static final ChessMoveAction[] iterativeDeepening(ChessGameState state, int depth, boolean color)
	{
		long initTime = System.currentTimeMillis();
		Map<Integer,ChessMoveAction> moveMap;
		Map<Integer,ChessMoveAction> lastMoveMap = new HashMap<Integer,ChessMoveAction>();
		ChessMoveAction[] possibleMoves = MoveGenerator.getPossibleMoves(state, null, !color);
		
		outerloop:
		for(int d = 0; d<depth;d++)
		{
			moveMap = new HashMap<Integer,ChessMoveAction>();
			
			//Calculate the score of each move
			for(ChessMoveAction move: possibleMoves)
			{
				//Apply each move to a new GameState
				ChessGameState newState = new ChessGameState(state);
				newState.applyMove(move);
				
				int score = negaScoutHelper(depth,newState,
						Integer.MIN_VALUE,Integer.MAX_VALUE,
						state.isWhoseTurn(), !color);
				
				moveMap.put(Integer.valueOf(score), move);
				Log.d("search","Move: "+move+"\nScore: "+score);
				//Reached time limit, so reduce the depth of the search to save time
				if(System.currentTimeMillis()-initTime > maxTime)
				{
					break outerloop;
				}
			}
			lastMoveMap = moveMap;
		}
		
		//Sort the moveMap
		TreeMap<Integer,ChessMoveAction> moveTreeMap = new TreeMap<Integer,ChessMoveAction>(lastMoveMap);
		
		//Convert to array
		Collection<ChessMoveAction> values = moveTreeMap.values();
		ChessMoveAction[] sortedList = values.toArray(new ChessMoveAction[values.size()]);
		return sortedList;
	}
}

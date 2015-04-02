package edu.up.cs301.chess;

import android.util.Log;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.engine.MoveGenerator;
import edu.up.cs301.chess.engine.Search;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;
    
/**
 * A computer-version of a chess player.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 */
public class ChessComputerPlayer1 extends GameComputerPlayer implements ChessPlayer, Tickable {
	
	/*
	 * The intelligence of the AI
	 * A value of MAX_INTELLIGENCE corresponds to a deeper search
	 * for the best move and selecting the best one. Lower values
	 * correspond to a shallower search and a lower inclination to
	 * pick the best move.
	 */
    protected int smart;
    
    // The current game state according to this player
    protected ChessGameState gameState;
    
    //true if this player is white, false if not
    protected boolean isWhite;
    
    //true if this player is player1 in the game state, false if not
    protected boolean isPlayer1;
    
	/**
     * Constructor for objects of class CounterComputerPlayer1
     * 
     * @param name
     * 		the player's name
     */
    public ChessComputerPlayer1(String name, int intelligence) {
        // invoke superclass constructor
        super(name);
        smart = intelligence;
        
        /*
         * Pick white or black randomly. The game state will
         * decide which player's choice goes through depending
         * on how fast they initialize the state.
         */
        
        isWhite = Math.random() > 0.5;
    }
    
    /**
     * callback method--game's state has changed
     * 
     * @param info
     * 		the information (presumably containing the game's state)
     */
	@Override
	protected void receiveInfo(GameInfo info) {
		
		Log.i("computer player", "receiving");
		
		// if there is no game, ignore
		if (game == null) {
			return;
		}
		else if (info instanceof ChessGameState) {
			// if we indeed have a chess game state, update the GUI
			ChessGameState newState = (ChessGameState)info;
			
			//null check and make sure a move was made
			if(newState == null || newState.equals(gameState))
			{
				return;
			}
			
			//send player info to the state if it wasn't done already
			
			gameState = newState;
			boolean success = makeMove();
			if(!success)
			{
				System.out.println(name+" did not make a move.");
			}
		}
	}
	
	/**
	 * Generates a move according to the AI's intelligence
	 * level.
	 */
	public boolean makeMove()
	{
		boolean success = false;
		
		//TODO check if it can make a move
		ChessGameState newState = new ChessGameState(gameState);
		
		if(smart == 0)
		{
			//Get all the possible moves
			ChessMoveAction[] possibleActions = MoveGenerator.getPossibleMoves(newState, this);
			
			//Check if the move generator found any possible moves
			if(possibleActions != null && possibleActions.length > 0)
			{
				int randomIndex = (int) (Math.random()*(possibleActions.length));
				
				if(possibleActions[randomIndex] != null)
				{
					possibleActions[randomIndex].setPlayer(this);
					success = newState.applyMove(possibleActions[randomIndex]);
					System.out.println(possibleActions[randomIndex]);
				}
			}
			else
			{
				//TODO do something if there are no possible actions
				System.out.println("Counld not generate any valid moves.");
			}
		}
		else if(smart > 0)
		{
			ChessMoveAction bestMove = Search.findMove(this, newState, smart);
			success = newState.applyMove(bestMove);
		}
		
		//send the new game state if it worked
		if(success)
		{
			gameState = newState;
			sendInfo(gameState);
		}
		return success;
	}

	/**
	 * Returns the color of this player as a boolean.
	 * 
	 * @return true if this color is white,
	 * 		   false if not.
	 */
	public boolean isWhite() {
		return isWhite;
	}
	
	/**
	 * Sets the color as white if the parameter is true.
	 * 
	 * @param boolean true if white, false if black
	 */
	public void setWhite(boolean color)
	{
		isWhite = color;
	}

	/**
	 * Returns true if this player is player 1 in the game state.
	 * 
	 * @return boolean true if this player is player 1
	 */
	public boolean isPlayer1() {
		return isPlayer1;
	}

	/**
	 * Sets the player as player 1 in the game state if
	 * the parameter is true.
	 * 
	 * @param boolean true if this is player 1
	 */
	public void setPlayer1(boolean isPlayer1) {
		this.isPlayer1 = isPlayer1;
	}

	public int getPlayerID() {
		return playerNum;
	}
	
	
}

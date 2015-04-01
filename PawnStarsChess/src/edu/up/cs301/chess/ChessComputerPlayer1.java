package edu.up.cs301.chess;

import android.util.Log;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.engine.MoveGenerator;
import edu.up.cs301.chess.engine.Search;
import edu.up.cs301.game.Game;
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
	
    private int smart;
    protected ChessGameState gameState;
    private boolean isWhite = false;//ask for this somehow
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
			// if we indeed have a counter-state, update the GUI
			ChessGameState newState = (ChessGameState)info;
			if(newState == null || newState.equals(gameState))
			{
				return;
			}
			
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
		//TODO check if it can make a move
		ChessGameState newState = new ChessGameState(gameState);
		if(smart == 0)
		{
			//Random move
			ChessMoveAction[] possibleActions = MoveGenerator.getPossibleMoves(newState, this);
			if(possibleActions != null && possibleActions.length > 0)
			{
				int randomIndex = (int) (Math.random()*(possibleActions.length));
				possibleActions[randomIndex].setPlayer(this);
				newState.applyMove(possibleActions[randomIndex]);
			}
			else
			{
				return false;
			}
			//TODO do something if there are no possible actions
		}
		else if(smart > 0)
		{
			ChessMoveAction bestMove = Search.findMove(this, newState, smart);
			newState.applyMove(bestMove);
		}
		gameState = newState;
		sendInfo(gameState);
		return true;
	}

	/**
	 * Returns the color of this player as a boolean
	 * @return true if this color is white,
	 * 		   false if not.
	 */
	public boolean isWhite() {
		return isWhite;
	}
	
	public void setWhite(boolean color)
	{
		isWhite = color;
	}
}

package edu.up.cs301.chess;

import android.util.Log;
import edu.up.cs301.chess.actions.ChessMoveAction;
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
    private ChessGameState gameState;
    
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
			gameState = (ChessGameState)info;
		}
	}
	
	/**
	 * Generates a move according to the AI's intelligence
	 * level.
	 */
	public void makeMove()
	{
		//TODO implement AI
	}

	/**
	 * Returns the color of this player as a boolean
	 * @return true if this color is white,
	 * 		   false if not.
	 */
	public boolean isWhite() {
		//TODO: implement color
		return false;
	}
}

/**
 * 
 */
package edu.up.cs301.chess.actions;

import edu.up.cs301.chess.ChessHumanPlayer;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * A basic action that indicates when a player is ready to resign.
 * It will return a losing message.
 * 
 * @authors Allison Liedtke
 * 		    Anthony Donaldson
 * 		    Derek Schumacher
 * 			Scott Rowland
 * @version March 2015
 *
 */
public class ResignAction extends GameAction {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -1949275934252L;
	
	//the player who lost
	private int playerIdx;
	
	//the name of who lost
	private String name;
	
	/**
	 * Default constructor. It sets the player instance variable
	 */
	public ResignAction(GamePlayer player) {
		super(player);
		if(player instanceof ChessHumanPlayer)
		{
			//playerIdx = ((ChessHumanPlayer)player).;
			//TODO get the name of who lost somehow
		}
	}
	
	public String toString()
	{
		return name+" has lost.";
	}
	
	public boolean hasLost(int playerIdx) 
	{
		if(this.playerIdx == playerIdx)
		{
			return true;
		}
		return false;
	}
	
}

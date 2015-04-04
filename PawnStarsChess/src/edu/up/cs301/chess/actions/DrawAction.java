/**
 * 
 */
package edu.up.cs301.chess.actions;

import edu.up.cs301.chess.ChessPlayer;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * A basic action that indicates when a player is ready to ask for a draw.
 * It will return a String with a message.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 *
 */
public class DrawAction extends GameAction {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -199275942952L;
	
	//the player who asks for a draw
	private ChessPlayer player1;
	
	//the message player2 will receive
	private String msg;
	
	/**
	 * Default constructor. It sets the player instance variable
	 */
	public DrawAction(GamePlayer player1, boolean player1IsWhite) {
		super(player1);
		if(player1 instanceof ChessPlayer)
		{
			this.player1 = (ChessPlayer)player1;
			//TODO get the name of who lost somehow
		}
	}

	/**
	 * Get the message asking for a draw
	 * @return msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Set the message asking for a draw
	 * @return msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * Get the player asking for a draw
	 * @return Player asking for a draw
	 */
	public ChessPlayer getPlayer1() {
		return player1;
	}


	
}

/**
 * 
 */
package edu.up.cs301.chess.actions;

import java.io.Serializable;

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
 * @version April 2015
 *
 */
public class DrawAction extends GameAction implements Serializable {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -1992234942952L;
	
	//the message player2 will receive
	private String msg;
	
	//whether of not the players agree to draw
	private boolean accepted;
	
	/**
	 * Default constructor. It sets the player instance variable
	 */
	public DrawAction(GamePlayer player, boolean accepted) {
		super(player);
		this.accepted = accepted;
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
	 * Gets whether or not the other player accepted the draw request
	 * @return true if the draw is accepted, false if the player needs to accept
	 */
	public boolean isAccepted() {
		return accepted;
	}
	
	public String toString()
	{
		return "Draw";
	}
	
	public DrawAction clone() {
		DrawAction newAction = new DrawAction(null,  this.accepted);
		return newAction;
	}
}

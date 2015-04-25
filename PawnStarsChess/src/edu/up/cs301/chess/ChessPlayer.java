package edu.up.cs301.chess;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;


/**
 * An interface that implements a chess player.
 * 
 * @author Allison Liedtke
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @version April 2015
 */
public interface ChessPlayer extends GamePlayer
{
	public boolean isWhite();
	
	public boolean isPlayer1();
	
	public int getPlayerID();
	
	public void askDraw(GameAction act);
}

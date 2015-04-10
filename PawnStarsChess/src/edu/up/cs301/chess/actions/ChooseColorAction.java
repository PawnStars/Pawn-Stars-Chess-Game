/**
 * 
 */
package edu.up.cs301.chess.actions;
import edu.up.cs301.chess.ChessPlayer;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * An action that determines what color each player will be
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version April 2015
 *
 */
public class ChooseColorAction extends GameAction {

	/**
	 * instance variables
	 */
	private static final long serialVersionUID = -192757587109452L;
	
	boolean whichColor;
	
	private ChessPlayer currentPlayer;
	
	private int playerID;
	
	boolean player1IsWhite;
	
	/**
	 * Default constructor
	 * @param Player the Player who made the move
	 * @param ChessPiece the piece being promoted
	 * @param int the type of piece the pawn will become
	 */
	public ChooseColorAction(GamePlayer player, boolean whichColor) {
		super(player);
		//TODO check if it is the right player
		this.whichColor = whichColor;
		if(player instanceof ChessPlayer)
		{
			currentPlayer = (ChessPlayer)player;
			playerID = currentPlayer.getPlayerID();
			if(playerID == 0 && whichColor)
			{
				player1IsWhite = true;
			}
		}
	}

	public boolean isWhichColor() {
		return whichColor;
	}

	public ChessPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public int getPlayerID() {
		return playerID;
	}

	public boolean isPlayer1IsWhite() {
		return player1IsWhite;
	}
	
}

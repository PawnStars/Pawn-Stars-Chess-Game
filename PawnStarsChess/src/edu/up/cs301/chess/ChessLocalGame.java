package edu.up.cs301.chess;

import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.DrawAction;
import edu.up.cs301.chess.actions.PawnMove;
import edu.up.cs301.chess.actions.ResignAction;
import edu.up.cs301.chess.actions.RookMove;
import edu.up.cs301.chess.actions.SelectUpgradeAction;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;
import android.util.Log;

/**
 * A class that represents the state of a chess game. 
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version March 2015
 */
public class ChessLocalGame extends LocalGame implements ChessGame {

	// the game's state
	private ChessGameState gameState;
	
	/**
	 * Can this player move?
	 * 
	 * @return
	 * 		true, if it is this player's turn and
	 * 		false if not 
	 */
	@Override
	public boolean canMove(int playerIdx) {
		//TODO: implement turns
		return true;
	}

	/**
	 * This ctor should be called when a new game is started
	 */
	public ChessLocalGame() {
		// initialize the game state with a default board setup
		this.gameState = new ChessGameState(true);
		
		/*TODO implement a part where the player selects their
		 * color or one is randomly chosen.
		 * */
	}

	/**
	 * The actions that should be sent are ChessMoveAction,
	 * SelectPieceAction, SelectUpgradeAction, ResignAction,
	 * and DrawActions. It returns true if it can handle the
	 * action and false if it cannot.
	 */
	@Override
	public boolean makeMove(GameAction action) {
		Log.i("action", action.getClass().toString());
		
		//TODO deselect the pieces on the board, update board, etc.
		//TODO implement what each move does
		if (action instanceof ChessMoveAction) {
		
			ChessMoveAction act = (ChessMoveAction)action;
			if (act instanceof PawnMove) {
				//TODO implement what each move does
			}
			if (act instanceof RookMove) {
				//TODO implement what each move does
			}
			else {
				ChessGameState newState = new ChessGameState(gameState);
				newState.applyMove(act);
				gameState = newState;
			}
			return true;
			
		}
		else if (action instanceof SelectUpgradeAction) {
			//TODO implement what each move does
		}
		else if (action instanceof ResignAction) {
			//TODO implement what each move does
		}
		else if (action instanceof DrawAction) {
			//TODO implement what each move does
		}
		else {
			// denote that this was an illegal move
			return false;
		}
		return true;
	}//makeMove
	
	/**
	 * Gets the most recent ChessGameState
	 * @param gameState the current state of the game
	 */
	public ChessGameState getGameState() {
		return gameState;
	}

	/**
	 * Sets a new ChessGameState after a move has been made
	 * @param gameState the current state of the game
	 */
	public void setGameState(ChessGameState gameState) {
		this.gameState = gameState;
	}

	/**
	 * Send the updated state to a given player
	 */
	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
		// this is a perfect-information game, so we'll make a
		// complete copy of the state to send to the player
		p.sendInfo(new ChessGameState(gameState));
		
	}//sendUpdatedSate
	
	/**
	 * Check if the game is over. It is over, return a string that tells
	 * who the winner, if any, is. If the game is not over, return null.
	 * 
	 * @return
	 * 		a message that tells who has won the game, or null if the
	 * 		game is not over
	 */
	@Override
	public String checkIfGameOver() {
		
		//TODO check if the game's over
		if(gameState.isGameOver())
		{
			//if(gameState.isPlayer1Won())
			//TODO implement
			return "Game over.";
		}
		else
		{
			return "";
		}
	}

}// class CounterLocalGame

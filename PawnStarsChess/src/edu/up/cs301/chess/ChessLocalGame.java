package edu.up.cs301.chess;

import edu.up.cs301.chess.actions.*;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.ProxyPlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import android.util.Log;

/**
 * A class that represents the state of a chess game. 
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version April 2015
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
	public boolean canMove(int playerIdx)
	{
		//isWhoseTurn is true if it is player 1's turn
		//player 1 has an idx of 0
		return (gameState.isWhoseTurn() == (playerIdx == 0));
	}

	/**
	 * This ctor should be called when a new game is started
	 */
	public ChessLocalGame() {
		// initialize the game state with a default board setup
		this.gameState = new ChessGameState(true);
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
		
		if (action instanceof ChessMoveAction) {
		
			ChessMoveAction act = (ChessMoveAction)action;
			
			if(canMove(getPlayerIdx(act.getPlayer())))
			{
				gameState.applyMove(act);
			}
			return true;
		}
		/*else if (action instanceof SelectUpgradeAction) {
			//A player select an upgrade at any time after a pawn reaches the end of the board
			gameState.applyMove(action);
			return true;
		}*/
		else if (action instanceof ResignAction) {
			
			ResignAction act = (ResignAction)action;
			
			//Set which player won and make the game over
			int losingPlayer = getPlayerIdx(act.getPlayer());
			if(losingPlayer == 0)
			{
				gameState.setPlayer1Won(true);
			}
			else
			{
				gameState.setPlayer2Won(true);
			}
			gameState.setGameOver(true);
			
			return true;
		}
		else if (action instanceof DrawAction)
		{
			DrawAction drawAct = (DrawAction)action;
			
			/*
			 * Ask the other player if the draw was not
			 * confirmed by both players
			 * 
			 */
			if(!drawAct.isAccepted())
			{
				for(GamePlayer p: players)
				{
					if(!p.equals(action.getPlayer()))
					{
						ChessPlayer player = (ChessPlayer)p;
						player.askDraw(drawAct.getMsg());
					}
				}
			}
			else
			{
				gameState.applyMove(action);
			}
			
			return true;
		}
		else if(action instanceof ChooseColorAction)
		{
			//whoever sends the choose color action determines color
			ChooseColorAction act = (ChooseColorAction)action;
			gameState = new ChessGameState(act.isPlayer1IsWhite());
			/*if(!(act.getCurrentPlayer() instanceof ProxyPlayer))
			{
				gameState = new ChessGameState(act.isPlayer1IsWhite());
				Log.d("local game","num players:"+players.length);
			}*/
			
			//Make sure both players get updated state information
			this.sendAllUpdatedState();
			
			return true;
		}
		else {
			// denote that this was an illegal move
			Log.i("action", "invalid move");
			return false;
		}
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
		
		if(gameState.isGameOver())
		{
			Log.i("game", "game over");
			
			//both players winning means there was a draw
			if(gameState.isPlayer1Won() && gameState.isPlayer2Won())
			{
				return "The game has ended in a draw.";
			}
			
			//Find the name of the player who won
			String winningPlayerName = "";
			if(gameState.isPlayer1Won())
			{
				winningPlayerName  = playerNames[0];
			}
			else if(gameState.isPlayer2Won())
			{
				winningPlayerName = playerNames[1];
			}
			else
			{
				winningPlayerName = "Nobody";
			}
			
			return "The game is over. "+winningPlayerName+" has won.";
		}
		else
		{
			return null;
		}
	}

}// class CounterLocalGame

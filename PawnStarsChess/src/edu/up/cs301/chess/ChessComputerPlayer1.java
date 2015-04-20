package edu.up.cs301.chess;

import android.util.Log;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.ChooseColorAction;
import edu.up.cs301.chess.actions.DrawAction;
import edu.up.cs301.chess.actions.PawnMove;
//import edu.up.cs301.chess.actions.SelectUpgradeAction;
import edu.up.cs301.chess.engine.MoveGenerator;
import edu.up.cs301.chess.engine.Search;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Tickable;
    
/**
 * A computer-version of a chess player.
 * 
 * @author Anthony Donaldson
 * @author Derek Schumacher
 * @author Scott Rowland
 * @author Allison Liedtke
 * @version April 2015
 */
public class ChessComputerPlayer1 extends GameComputerPlayer implements ChessPlayer, Tickable {
	
	public static final int RANDOM = 0;
	public static final int TAKE_PIECES = 1; 
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
			Log.d("computer player", "no game");
			return;
		}
		else if (info instanceof ChessGameState) {
			ChessGameState newState = (ChessGameState)info;
			
			//make sure the state was changed
			if(newState.equals(gameState))
			{
				Log.d("computer player", "equal game state");
				return;
			}
			else
			{
				gameState = newState;
				Log.d("computer player", "trying to make a move");
				GameAction move = makeMove();
				if(move != null)
				{
					//Log.d("computer player",gameState.toString());
					//Log.d("computer player", "Sending this move: "+move);
					game.sendAction(move);
				}
			}
		}
		else
		{
			Log.d("computer player", "error");
		}
	}
	
	/**
	 * Generates a move according to the AI's intelligence
	 * level.
	 */
	public GameAction makeMove()
	{
		//Log.d("computer player", "trying to make a move");
		
		if(gameState == null || gameState.isWhoseTurn() != isPlayer1())
		{
			return null;
		}
		ChessGameState newState = new ChessGameState(gameState);
		GameAction chosenMove = null;
		if(smart == RANDOM || smart == TAKE_PIECES)
		{
			//Get all the possible moves
			ChessMoveAction[] possibleActions = MoveGenerator.getPossibleMoves(newState, this, isWhite);
			
			//Check if the move generator found any possible moves
			if(possibleActions != null && possibleActions.length > 0)
			{
				//scramble the order of the moves so it won't be predictable
				for(int i=0;i<possibleActions.length;i++)
				{
					int randomIndex = (int) (Math.random()*(possibleActions.length));
					ChessMoveAction temp = possibleActions[i];
					
					possibleActions[i] = possibleActions[randomIndex];
					possibleActions[randomIndex] = temp;
				}
				
				for(int i=0;i<possibleActions.length;i++)
				{
					System.out.print(possibleActions[i].toString()+", ");
					ChessMoveAction tempMove = new ChessMoveAction(this,possibleActions[i]);
					if(tempMove != null)
					{
						chosenMove = tempMove;
						if(smart == RANDOM)
						{
							//Apply any valid non-null move
							break;
						}
						if(smart == TAKE_PIECES)
						{
							//Do any move that would result in taking a piece if it exists
							if(tempMove.getTakenPiece() != null)
							{
								break;
							}
						}
					}
				}
			}
			
			//Make sure the player is included as a reference
			//chosenMove = new ChessMoveAction(this,(ChessMoveAction)chosenMove);

		}
		else if(smart > 1)
		{
			ChessMoveAction bestMove = Search.findMove(this, newState, smart);
			bestMove = new ChessMoveAction(this,bestMove);
			chosenMove = bestMove;
		}
		if(chosenMove == null)
		{
			/*
			 * The game ends in a stalemate if one of the
			 * players cannot make a valid move
			 */
			chosenMove = new DrawAction(this,isWhite(),true);
		}
		//send the new game state if it worked
		return chosenMove;
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
		GameAction move = makeMove();
		if(move != null)//TODO not sure about this
		{
			Log.d("computer player",gameState.toString());
			Log.d("computer player", "Sending this move: "+move);
			game.sendAction(move);
		}
	}

	/**
	 * Returns true if this player is player 1 in the game state.
	 * 
	 * @return boolean true if this player is player 1
	 */
	public boolean isPlayer1() {
		return playerNum == 0;//return isPlayer1;
	}

	public int getPlayerID() {
		return playerNum;
	}

	/**
	 * How to respond when a player asks for a draw
	 */
	public void askDraw(String msg)
	{
		if(smart < 10)
		{
			DrawAction act = new DrawAction(this,isWhite,true);
			game.sendAction(act);
		}
		//Do not accept if the computer is smart
		
	}
	
	
}

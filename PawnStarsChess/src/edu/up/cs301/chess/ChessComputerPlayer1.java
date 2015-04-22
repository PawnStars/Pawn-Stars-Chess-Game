package edu.up.cs301.chess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import android.util.Log;
import edu.up.cs301.chess.actions.ChessMoveAction;
import edu.up.cs301.chess.actions.DrawAction;
import edu.up.cs301.chess.engine.MoveGenerator;
import edu.up.cs301.chess.engine.UCIInterface;
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
	
	public static final int CRITTER = 9; 
	public static final int STOCKFISH = 10; 
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
    //protected boolean isWhite;
    
    private String[] engines = {
    	"/data/data/edu.up.cs301.game/cache/stockfish.engine",
    	"/data/data/edu.up.cs301.game/cache/critter1.6a.engine"
    	};
    
    //
    private String engine;
    
    //whether or not the executable was copied
    private boolean copied;
    
    //The interface between the chess engine executable and java
    private UCIInterface client;
    
    //the time to wait before sending a move
    private int waitTime;
    
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
        copied = false;
        waitTime = 2000;
        
        
        
        /*
         * Pick white or black randomly. The game state will
         * decide which player's choice goes through depending
         * on how fast they initialize the state.
         */
        
        //isWhite = Math.random() > 0.5;
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
			
			//keep trying to copy assets until it succeeds
			if(!copied)
			{
				//copyAssets();
				if(smart > 1)
				{
					String engine = "";
					if(smart == STOCKFISH)
			        {
			        	engine = engines[0];
			        }
			        
			        if(smart == CRITTER)
			        {
			        	engine = engines[1];
			        }
					client = new UCIInterface(engine);
				}
			}
			
			//make sure the state was changed
			if(newState.equals(gameState))
			{
				Log.d("computer player", "equal game state");
				return;
			}
			else
			{
				gameState = newState;
				Log.d("computer player",gameState.toString());
				//not sure if we need to apply move
				GameAction move = makeMove();
				Log.d("computer player", "Sending this move: "+move);
				gameState.applyMove(move);
				game.sendAction(move);
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
		
		long start = System.currentTimeMillis();
		ChessGameState newState = new ChessGameState(gameState);
		GameAction chosenMove = null;
		if(smart > 1)
		{
			String FEN = gameState.toFEN();
			
			if(FEN == null || FEN.equals(""))
			{
				return null;
			}
			
			// initialize and connect to engine
			if (client.startEngine())
			{
				Log.d("search","Engine has started..");
				// send commands manually
				client.sendCommand("uci");
				
				String bestMove = client.getBestMove(FEN,5000);
				
				Log.d("computer player","best move:"+bestMove);
				if(bestMove != null && !bestMove.equals("(none)"))
				{
					//get coordinates
					bestMove = bestMove.toLowerCase(Locale.US);
					
					int oldX = bestMove.charAt(bestMove.length()-4)-97;
					int oldY = ChessGameState.BOARD_HEIGHT-bestMove.charAt(bestMove.length()-3)+48;
					int newX = bestMove.charAt(bestMove.length()-2)-97;
					int newY = ChessGameState.BOARD_HEIGHT-bestMove.charAt(bestMove.length()-1)+48;

					Log.d("computer player","x:"+oldX+" y:"+oldY+" newX:"+newX+" newY:"+newY);
					
					int[] newLoc = new int[]{newY,newX};
					if(!ChessGameState.outOfBounds(newLoc) && !ChessGameState.outOfBounds(oldX,oldY))
					{
						ChessPiece whichPiece = gameState.getPieceMap()[oldY][oldX];
						ChessPiece takenPiece = gameState.getPieceMap()[newY][newX];
						chosenMove = new ChessMoveAction(this,whichPiece,newLoc,takenPiece);
						//TODO make special moves work
					}
					
				}

				// get the evaluation score of current position
				//System.out.println("Eval score : " + client.getEvalScore(FEN, 2000));

				// stop the engine
				Log.d("computer player","Stopping engine..");
				client.stopEngine();
				
			}
			else
			{
			    Log.d("search","Something went wrong..");
			}
		}
		if(smart == RANDOM || smart == TAKE_PIECES || chosenMove == null)
		{
			//Get all the possible moves
			ChessMoveAction[] possibleActions = MoveGenerator.getPossibleMoves(newState, this, isWhite());
			
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
						if(smart >= TAKE_PIECES)
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
			chosenMove = new ChessMoveAction(this,(ChessMoveAction)chosenMove);

		}
		
		//wait the remaining amount of time
		if(start-System.currentTimeMillis() < waitTime)
		{
			try
			{
				Thread.sleep(waitTime+System.currentTimeMillis()-start);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
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
		
		return (isPlayer1() == gameState.isPlayer1IsWhite());
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
			DrawAction act = new DrawAction(this,isWhite(),true);
			game.sendAction(act);
		}
		//Do not accept if the computer is smart
		
	}
}
